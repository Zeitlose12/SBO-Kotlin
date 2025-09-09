package net.sbo.mod.utils.waypoint

import net.sbo.mod.utils.math.SboVec

/**
 * Represents a single warp point with coordinates and optional data.
 * @property pos The position of the warp point in the game world.
 * @property unlocked Indicates if the warp is unlocked.
 * @property setting An optional name for an associated setting.
 */
data class WarpPoint(
    val pos: SboVec = SboVec(0.0, 0.0, 0.0),
    val unlocked: Boolean,
    val setting: String? = null // Nullable, since not all warps have a setting
)

val hubWarps: Map<String, WarpPoint> = mapOf(
    "hub" to WarpPoint(SboVec(-3.0, 70.0, -70.0), true),
    "museum" to WarpPoint(SboVec(-76.0, 76.0, 81.0), true)
)

val additionalHubWarps: Map<String, WarpPoint> = mapOf(
    "castle" to WarpPoint(SboVec(-250.0, 130.0, 45.0), true, "castleWarp"),
    "wizard" to WarpPoint(SboVec(42.0, 122.0, 69.0), true, "wizardWarp"),
    "crypt" to WarpPoint(SboVec(-161.0, 61.0, -99.0), true, "cryptWarp"),
    "stonks" to WarpPoint(SboVec(-53.0, 72.0, -53.0), true, "stonksWarp"),
    "da" to WarpPoint(SboVec(92.0, 75.0, 174.0), true, "darkAuctionWarp"),
    "taylor" to WarpPoint(SboVec(22.0, 71.0, -42.0), true, "taylorWarp")
)

enum class AdditionalHubWarps {
    CASTLE,
    WIZARD,
    CRYPT,
    STONKS,
    DA,
    TAYLOR
}
