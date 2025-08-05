package net.sbo.mod.general

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.sbo.mod.render.RenderUtil
import java.awt.Color
import net.sbo.mod.utils.SboVec
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.Register
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.roundToInt


// Simulates the settings object
object Settings {
    var guessLine = true
    var guessColor: Color = Color.RED
    var inqLine = true
    var burrowLineWidth = 3
    var removeGuessDistance = 10.0
    var removeGuess = true
    var waypointTextSize = 10.0
    var warpDiff = "20"
    var dontWarpIfBurrowNearby = true
}

// --- WaypointManager to manage all waypoints ---

/**
 * Represents a single warp point with coordinates and optional data.
 *
 * @property x The X-coordinate.
 * @property y The Y-coordinate.
 * @property z The Z-coordinate.
 * @property unlocked Indicates if the warp is unlocked.
 * @property setting An optional name for an associated setting.
 */
data class WarpPoint(
    val x: Int,
    val y: Int,
    val z: Int,
    val unlocked: Boolean,
    val setting: String? = null // Nullable, since not all warps have a setting
)

val hubWarps: Map<String, WarpPoint> = mapOf(
    "hub" to WarpPoint(x = -3, y = 70, z = -70, unlocked = true),
    "museum" to WarpPoint(x = -76, y = 76, z = 81, unlocked = true)
)

val additionalWarps: Map<String, WarpPoint> = mapOf(
    "wizard" to WarpPoint(x = 42, y = 122, z = 69, unlocked = true, setting = "wizardWarp"),
    "crypt" to WarpPoint(x = -161, y = 61, z = -99, unlocked = true, setting = "cryptWarp"),
    "stonks" to WarpPoint(x = -53, y = 72, z = -53, unlocked = true, setting = "stonksWarp"),
    "da" to WarpPoint(x = 92, y = 75, z = 174, unlocked = true, setting = "darkAuctionWarp"),
    "castle" to WarpPoint(x = -250, y = 130, z = 45, unlocked = true, setting = "castleWarp")
)

fun javaColorToHex(color: Color): String = String.format("%06X", color.rgb and 0xFFFFFF)

object WaypointRenderer : WorldRenderEvents.AfterTranslucent {
    override fun afterTranslucent(context: WorldRenderContext) {
        WaypointManager.renderAllWaypoints(context)
    }
}

object WaypointManager { // works
    var guessWp: Waypoint? = null
    val waypoints = mutableMapOf<String, MutableList<Waypoint>>()
    var closestBurrow: Pair<Waypoint?, Double> = null to 1000.0

    init {
        if (guessWp == null) {
            guessWp = Waypoint("§7Guess", 100.0, 100.0, 100.0, 1.0, 0.0, 0.0, type = "guess")
        }

        // create test waypoint at 100, 100, 100
        val testWaypoint = Waypoint("Mob", 100.0, 100.0, 100.0, 1.0, 0.0, 0.0, type = "burrow", line = true, beam = true)
        val testWaypoint2 = Waypoint("Mob2", 102.0, 100.0, 100.0, 1.0, 0.0, 0.0, type = "burrow", line = true, beam = true)

        Register.command("addwp") {
            addWaypoint(testWaypoint)
            addWaypoint(testWaypoint2)
            1
        }

        Register.command("removeburrowwps") {
            removeAllOfType("burrow")
            1
        }

//        val testWaypoint = Waypoint("Mob", 100.0, 100.0, 100.0, 1.0, 0.0, 0.0, type = "burrow", line = true, beam = true)
//        addWaypoint(testWaypoint)

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
        val warpProvider = ::getClosestWarp //

        forEachWaypoint { waypoint ->
            waypoint.format(inqWps, closestBurrow.second, warpProvider)
        }
        guessWp?.format(inqWps, closestBurrow.second, warpProvider)
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
    fun getClosestWarp(waypoint: Waypoint): String? {
        // Your logic to find the closest warp would go here.
        return null
    }
}


/**
 * @class Waypoint
 * @description A class to create and manage waypoints in the game.
 * @param text The text to display on the waypoint.
 * @param x The x coordinate of the waypoint.
 * @param y The y coordinate of the waypoint.
 * @param z The z coordinate of the waypoint.
 * @param r The red color component of the waypoint.
 * @param g The green color component of the waypoint.
 * @param b The blue color component of the waypoint.
 * @param ttl The time to live for the waypoint in seconds (0 for infinite).
 * @param type The type of the waypoint for customization.
 * @param line Whether to draw a line to the waypoint.
 * @param beam Whether to draw a beam at the waypoint.
 * @param distance Whether to display the distance in meters (blocks) to the waypoint.
 */
class Waypoint(
    var text: String,
    var x: Double,
    var y: Double,
    var z: Double,
    var r: Double,
    var g: Double,
    var b: Double,
    val ttl: Int = 0,
    val type: String = "normal",
    var line: Boolean = false,
    var beam: Boolean = true,
    var distance: Boolean = true
) {
    var pos: SboVec = SboVec(x, y, z)
    var hexCodeString: String = javaColorToHex(Color(r.toFloat(), g.toFloat(), b.toFloat()))
    val alpha: Double = 0.5
    var hidden: Boolean = false
    val creation: Long = System.currentTimeMillis()
    var formatted: Boolean = false
    var distanceRaw: Double = 0.0
    var distanceText: String = ""
    var formattedText: String = ""
    var warp: String? = null

    fun distanceToPlayer(): Double { // works
        val playerPos = Player.getLastPosition()
        return sqrt((playerPos.x - x).pow(2) + (playerPos.y - y).pow(2) + (playerPos.z - z).pow(2))
    }

    private fun setWarpText(closestWarpProvider: (Waypoint) -> String?) {
        this.warp = closestWarpProvider(this)
        this.formattedText = this.warp?.let { "$text§7 (warp $it)${this.distanceText}" } ?: "${this.text}${this.distanceText}"
    }

    private fun formatGuess(
        closestBurrowDistance: Double,
        inqWaypoints: List<Waypoint>
    ) {
        this.line = Settings.guessLine && (closestBurrowDistance > 60) && inqWaypoints.isEmpty()
        this.r = Settings.guessColor.red / 255.0
        this.g = Settings.guessColor.green / 255.0
        this.b = Settings.guessColor.blue / 255.0
        this.hexCodeString = javaColorToHex(Settings.guessColor)

        val (exists, wp) = WaypointManager.waypointExists("burrow", this.pos)
        if (exists && wp != null) {
            this.hidden = wp.distanceToPlayer() < 60
        }
    }

    fun format(
        inqWaypoints: List<Waypoint>,
        closestBurrowDistance: Double,
        closestWarpProvider: (Waypoint) -> String?
    ) {
        this.distanceRaw = this.distanceToPlayer()
        this.distanceText = if (this.distance) " §b[${this.distanceRaw.roundToInt()}m]" else ""

        if (this.type == "guess" || (this.type == "inq" && inqWaypoints.lastOrNull() == this)) {
            this.setWarpText(closestWarpProvider)
        } else {
            this.warp = null
            this.formattedText = "${this.text}${this.distanceText}"
        }

        if (this.type.lowercase() == "guess") {
            this.formatGuess(closestBurrowDistance, inqWaypoints)
        }

//        if (this.distanceRaw >= 230) {
//            val playerPos = Player.getLastPosition()
//            val scale = 230 / this.distanceRaw
//            this.fx = playerPos.x + (this.fx - playerPos.x) * scale
//            this.fz = playerPos.z + (this.fz - playerPos.z) * scale
//        }

        this.formatted = true
    }

    fun hide(): Waypoint { // works
        this.hidden = true
        return this
    }

    fun show(): Waypoint { // works
        this.hidden = false
        return this
    }

    fun render(context: WorldRenderContext) {
        if (!this.formatted || this.hidden) return

        if (this.type == "guess" && this.distanceRaw <= Settings.removeGuessDistance && Settings.removeGuess) return // works

        RenderUtil.renderWaypoint(
            context,
            this.formattedText,
            this.pos,
            floatArrayOf(this.r.toFloat(), this.g.toFloat(), this.b.toFloat()),
            this.hexCodeString.toLong(16).toInt(),
            this.alpha.toFloat(),
            true,
            this.line,
            Settings.burrowLineWidth.toFloat(),
            this.beam
        )
    }
}