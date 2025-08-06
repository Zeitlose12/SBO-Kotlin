package net.sbo.mod.general

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.sbo.mod.render.RenderUtil
import net.sbo.mod.settings.Settings
import java.awt.Color
import net.sbo.mod.utils.SboVec
import net.sbo.mod.utils.Player
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.roundToInt

fun javaColorToHex(color: Color): String = String.format("%06X", color.rgb and 0xFFFFFF)

/**
 * @class Waypoint
 * @description A class to create waypoints in the game.
 * @param text The text to display on the waypoint.
 * @param pos The position of the waypoint in the game world.
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
    var pos: SboVec,
    var r: Double,
    var g: Double,
    var b: Double,
    val ttl: Int = 0,
    val type: String = "normal",
    var line: Boolean = false,
    var beam: Boolean = true,
    var distance: Boolean = true
) {
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
        return sqrt((playerPos.x - this.pos.x).pow(2) + (playerPos.y - this.pos.y).pow(2) + (playerPos.z - this.pos.z).pow(2))
    }

    private fun setWarpText() {
        this.warp = WaypointManager.getClosestWarp(this.pos)
        this.formattedText = this.warp?.let { "$text§7 (warp $it)${this.distanceText}" } ?: "${this.text}${this.distanceText}"
    }

    private fun formatGuess(
        closestBurrowDistance: Double,
        inqWaypoints: List<Waypoint>
    ) {
//        this.line =  Settings.guessLine && (closestBurrowDistance > 60) && inqWaypoints.isEmpty()
//        this.r = Settings.guessColor.red / 255.0
//        this.g = Settings.guessColor.green / 255.0
//        this.b = Settings.guessColor.blue / 255.0
//        this.hexCodeString = javaColorToHex(Settings.guessColor)

        val (exists, wp) = WaypointManager.waypointExists("burrow", this.pos)
        if (exists && wp != null) {
            this.hidden = wp.distanceToPlayer() < 60
        }
    }

    fun format(
        inqWaypoints: List<Waypoint>,
        closestBurrowDistance: Double
    ) {
        this.distanceRaw = this.distanceToPlayer()
        this.distanceText = if (this.distance) " §b[${this.distanceRaw.roundToInt()}m]" else ""

        if (this.type == "guess" || (this.type == "inq" && inqWaypoints.lastOrNull() == this)) {
            this.setWarpText()
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

//        if (this.type == "guess" && this.distanceRaw <= Settings.removeGuessDistance && Settings.removeGuess) return

        RenderUtil.renderWaypoint(
            context,
            this.formattedText,
            this.pos,
            floatArrayOf(this.r.toFloat(), this.g.toFloat(), this.b.toFloat()),
            this.hexCodeString.toLong(16).toInt(),
            this.alpha.toFloat(),
            true,
            this.line,
            5f,
            this.beam
        )
    }
}