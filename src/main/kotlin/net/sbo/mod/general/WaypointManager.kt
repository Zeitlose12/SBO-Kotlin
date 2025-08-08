package net.sbo.mod.general

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.sbo.mod.render.WaypointRenderer
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper.sleep
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.SboVec
import kotlin.math.roundToInt

object WaypointManager {
    var guessWp: Waypoint? = null
    val waypoints = mutableMapOf<String, MutableList<Waypoint>>()
    var closestBurrow: Pair<Waypoint?, Double> = null to 1000.0

    init {
        if (guessWp == null) {
            guessWp = Waypoint("Guess", 100.0, 100.0, 100.0, 0.0f, 0.964f, 1.0f, 0,"guess")
        }

        // create test waypoint at 100, 100, 100
        val testWaypointTreasure = Waypoint(
            "Treasure",
            102.0, 100.0, 100.0,
            1.0f, 0.666f, 0.0f,
            0,
            "burrow"
        )
        val testWaypointStart = Waypoint(
            "Start",
            100.0, 100.0, 102.0,
            0.333f, 1.0f, 0.333f,
            0,
            "burrow"
        )
        val testWaypointMob = Waypoint(
            "Mob",
            100.0, 100.0, 100.0,
            1.0f, 0.333f, 0.333f,
            0,
            "burrow"
        )
        Register.command("removeAllInq") {
            removeAllOfType("inq")
            Chat.chat("Removed all Inquisitor waypoints.")
        }

        Register.command("sbosendinq") {
            val playerPos = Player.getLastPosition()
            Chat.command("pc x: ${playerPos.x.roundToInt()}, y: ${playerPos.y.roundToInt() - 1}, z: ${playerPos.z.roundToInt()} | Inquisitor spawned")
        }

        Register.onChatMessage(
            Regex("^(?<channel>.*> )?(?<playerName>.+?)[§&]f: (?:[§&]r)?x: (?<x>[^ ,]+),? y: (?<y>[^ ,]+),? z: (?<z>[^ ,]+)(?<trailing>.*)$")
        ) { message, match ->
            val channel = match.groups["channel"]?.value ?: "Unknown"
            val playerName = match.groups["playerName"]?.value ?: "Unknown"

            val x = match.groups["x"]?.value?.toIntOrNull() ?: 0.0
            val y = match.groups["y"]?.value?.toIntOrNull() ?: 0.0
            val z = match.groups["z"]?.value?.toIntOrNull() ?: 0.0

            val trailing = match.groups["trailing"]?.value ?: ""

            if (!channel.contains("Guild")) {
                if (trailing.startsWith(" ") || trailing.lowercase().contains("inquisitor") || Diana.allWaypointsAreInqs)
                    addWaypoint(Waypoint(playerName, x.toDouble(), y.toDouble(), z.toDouble(), 1.0f, 0.84f, 0.0f, 45, type = "inq"))
                else
                    addWaypoint(Waypoint(playerName, x.toDouble(), y.toDouble(), z.toDouble(), 0.0f, 0.2f, 1.0f, 30))
            }
            1
        }

        Register.onTick(5) { _ ->
            closestBurrow = getClosestWaypoint(Player.getLastPosition(), "burrow") ?: (null to 1000.0)
            val inqWps = getWaypointsOfType("inq")

            this.forEachWaypoint { waypoint ->
                if (waypoint.ttl > 0 && waypoint.creation + waypoint.ttl * 1000 < System.currentTimeMillis()) {
                    removeWaypoint(waypoint)
                }

                waypoint.line = closestBurrow.first == waypoint && Diana.burrowLine && closestBurrow.second <= 60 && inqWps.isEmpty()

                waypoint.format(inqWps, closestBurrow.second)
            }

            guessWp?.format(inqWps, closestBurrow.second)
        }

        WorldRenderEvents.AFTER_TRANSLUCENT.register(WaypointRenderer)
    }

    /**
     * Renders all waypoints in the management system.
     * @param context The world render context.
     */
    fun renderAllWaypoints(context: WorldRenderContext) {
        this.forEachWaypoint { waypoint ->
            waypoint.render(context)
        }

        this.guessWp?.render(context)
    }

    /**
     * Adds a waypoint to the management system.
     * @param waypoint The waypoint to add.
     */
    fun addWaypoint(waypoint: Waypoint) {
        if (waypoint.type.lowercase() != "guess") {
            waypoints.getOrPut(waypoint.type.lowercase()) { mutableListOf() }.add(waypoint)
        }
    }

    /**
     * Removes a specific waypoint from the management system.
     * @param waypoint The waypoint to remove.
     */
    fun removeWaypoint(waypoint: Waypoint) {
        if (closestBurrow.first == waypoint) {
            closestBurrow = null to 1000.0
        }
        waypoints[waypoint.type.lowercase()]?.remove(waypoint)
    }

    /**
     * Removes a waypoint at a specific position and type.
     * @param pos The position of the waypoint to remove.
     * @param type The type of the waypoint to remove.
     */
    fun removeWaypointAt(pos: SboVec, type: String) {
        val waypoint = waypoints[type.lowercase()]?.find { it.pos == pos }
        if (waypoint != null) {
            removeWaypoint(waypoint)
        }
    }

    /**
     * Updates the guess waypoint position.
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     * @param z The new Z coordinate.
     */
    fun updateGuess(x: Double, y: Double, z: Double) {
        guessWp?.apply {
            show()
            pos.x = x
            pos.y = y
            pos.z = z
        }
    }

    /**
     * Checks if a waypoint of a specific type exists at a given position.
     * @param type The type of the waypoint to check.
     * @param pos The position to check for the waypoint.
     * @return A pair containing a boolean indicating existence and the waypoint if found.
     */
    fun waypointExists(type: String, pos: SboVec): Pair<Boolean, Waypoint?> {
        val waypoint = getWaypointsOfType(type).find { it.pos == pos }
        return (waypoint != null) to waypoint
    }

    /**
     * Iterates over all waypoints and applies the given action to each.
     * @param action The action to apply to each waypoint.
     */
    fun forEachWaypoint(action: (Waypoint) -> Unit) {
        waypoints.values.flatten().forEach(action)
    }

    /**
     * Removes all waypoints of a specific type.
     * @param type The type of waypoints to remove.
     */
    fun removeAllOfType(type: String) {
        waypoints[type.lowercase()]?.clear()
    }

    /**
     * Retrieves all waypoints of a specific type.
     * @param type The type of waypoints to retrieve.
     * @return A list of waypoints of the specified type.
     */
    fun getWaypointsOfType(type: String): List<Waypoint> {
        return waypoints[type.lowercase()] ?: emptyList()
    }

    /**
     * Gets the closest waypoint of a specific type to a given position.
     * @param pos The position to find the closest waypoint to.
     * @param type The type of waypoint to search for.
     * @return The closest waypoint of the specified type, or null if none are found.
     */
    fun getClosestWaypoint(pos: SboVec, type: String): Pair<Waypoint, Double>? {
        val waypointsOfType = getWaypointsOfType(type)
        if (waypointsOfType.isEmpty()) return null

        var closestWaypoint: Waypoint? = null
        var closestDistance = Double.MAX_VALUE

        for (waypoint in waypointsOfType) {
            val distance = pos.distanceTo(waypoint.pos)
            if (distance < closestDistance) {
                closestDistance = distance
                closestWaypoint = waypoint
            }
        }

        return closestWaypoint?.let { it to closestDistance }
    }

    /**
     * Gets the closest warp point to a given position.
     * @param pos The position to find the closest warp to.
     * @return The name of the closest warp, or null if no warps are available.
     */
    fun getClosestWarp(pos: SboVec): String? {
        var warps = hubWarps.filter { it.value.unlocked }.mapValues { it.value }
        for (warp in Diana.allowedWarps) {
            if (additionalHubWarps.containsKey(warp.name.lowercase())) {
                val additionalWarp = additionalHubWarps[warp.name.lowercase()]
                if (additionalWarp != null && additionalWarp.unlocked) {
                    warps = warps + (warp.name.lowercase() to additionalWarp)
                }
            }
        }

        var closestWarp: String? = null
        var closestDistance = Double.MAX_VALUE
        val playerDistance = pos.distanceTo(Player.getLastPosition())
        for ((name, warp) in warps) {
            val distance = pos.distanceTo(warp.pos)
            if (distance < closestDistance) {
                closestDistance = distance
                closestWarp = name
            }
        }

        val condition1 = playerDistance > (closestDistance + Diana.warpDiff)
        val condition2 = condition1 && (closestBurrow.second > 60 || getWaypointsOfType("inq").isNotEmpty())

        val condition = if (Diana.dontWarpIfBurrowClose) condition2 else condition1

        return if (condition) closestWarp else null
    }

    fun warpToGuess() {
        val warp = getClosestWarp(guessWp?.pos ?: SboVec(0.0, 0.0, 0.0))
        if (warp != null) {
            executeWarpCommand(warp)
            Chat.chat("Warping to guess waypoint at $warp")
        } else {
            Chat.chat("No valid warp found for guess waypoint.")
        }
    }

    fun warpToInq() {
        val newestInq = getWaypointsOfType("inq").maxByOrNull { it.creation }
        if (newestInq == null) return

        val warp = getClosestWarp(newestInq.pos)
        if (warp == null) return

        executeWarpCommand(warp)
    }

    fun warpBoth() {
        if (getWaypointsOfType("inq").isEmpty()) {
            warpToGuess()
        } else {
            warpToInq()
        }
    }

    var tryWarp: Boolean = false
    fun executeWarpCommand(warp: String) {
        if (warp.isNotEmpty() && !tryWarp) {
            tryWarp = true
            Chat.command("warp $warp")
            sleep(500) {
                tryWarp = false
            }
        }
    }
}