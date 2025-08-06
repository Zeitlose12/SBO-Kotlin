package net.sbo.mod.general

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.sbo.mod.render.WaypointRenderer
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.SboVec
import kotlin.math.roundToInt

object WaypointManager { // works
    var guessWp: Waypoint? = null
    val waypoints = mutableMapOf<String, MutableList<Waypoint>>()
    var closestBurrow: Pair<Waypoint?, Double> = null to 1000.0

    init {
        if (guessWp == null) {
            guessWp = Waypoint("§7Guess", SboVec(100.0, 100.0, 100.0), 0.0, 0.964, 1.0, type = "guess")
        }

        // create test waypoint at 100, 100, 100
        val testWaypointTreasure = Waypoint("Treasure", SboVec(102.0, 100.0, 100.0), 1.0, 0.666, 0.0, type = "burrow", line = true, beam = true)
        val testWaypointStart = Waypoint("Start", SboVec(100.0, 100.0, 102.0), 0.333, 1.0, 0.333, type = "burrow", line = true, beam = true)
        val testWaypointMob = Waypoint("Mob", SboVec(100.0, 100.0, 100.0), 1.0, 0.333, 0.333, type = "burrow", line = true, beam = true)

        Register.command("addwp") {
            addWaypoint(testWaypointMob)
            addWaypoint(testWaypointTreasure)
            addWaypoint(testWaypointStart)
            1
        }

        Register.command("removeburrowwps") {
            removeAllOfType("burrow")
            1
        }

        Register.command("sendcoords") {
            val playerPos = Player.getLastPosition()
            Chat.command("cc x: ${playerPos.x.roundToInt()}, y: ${playerPos.y.roundToInt()}, z: ${playerPos.z.roundToInt()}")
        }

        Register.onChatMessage(
            Regex("^(?<channel>.*> )?(?<playerName>.+?)[§&]f: (?:[§&]r)?x: (?<x>[^ ,]+),? y: (?<y>[^ ,]+),? z: (?<z>[^ ,]+)$")
        ) { message, match ->
            val playerName = match.groups["playerName"]?.value ?: "Unknown"

            val x = match.groups["x"]?.value?.toDoubleOrNull() ?: 0.0
            val y = match.groups["y"]?.value?.toDoubleOrNull() ?: 0.0
            val z = match.groups["z"]?.value?.toDoubleOrNull() ?: 0.0

            val channel = match.groups["channel"]?.value ?: "Unknown"

            if (!channel.contains("Guild")) {
//                Chat.chat("Added waypoint for $playerName at ($x, $y, $z) channel: $channel")
//                Chat.chat(message)
                // show title to player

                addWaypoint(Waypoint(playerName, SboVec(x+2, y, z), 1.0, 0.84, 0.0, 45, type = "inq"))

                addWaypoint(Waypoint(playerName, SboVec(x, y, z), 0.0, 0.2, 1.0, 30))

            }
            1
        }

        Register.onTick(5) { _ ->
            this.formatAllWaypoints()
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
     * Formats all waypoints, including distance calculations and warp information.
     */
    private fun formatAllWaypoints() {
        val inqWps = getWaypointsOfType("inq")

        forEachWaypoint { waypoint ->
            waypoint.format(inqWps, closestBurrow.second)
        }
        guessWp?.format(inqWps, closestBurrow.second)
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

    // not implemented yet
    fun getClosestWarp(pos: SboVec): String? {

        return null
    }
}