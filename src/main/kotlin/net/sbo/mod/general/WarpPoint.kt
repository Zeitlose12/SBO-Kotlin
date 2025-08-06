package net.sbo.mod.general

/**
 * Represents a single warp point with coordinates and optional data.
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

val additionalHubWarps: Map<String, WarpPoint> = mapOf(
    "wizard" to WarpPoint(x = 42, y = 122, z = 69, unlocked = true, setting = "wizardWarp"),
    "crypt" to WarpPoint(x = -161, y = 61, z = -99, unlocked = true, setting = "cryptWarp"),
    "stonks" to WarpPoint(x = -53, y = 72, z = -53, unlocked = true, setting = "stonksWarp"),
    "da" to WarpPoint(x = 92, y = 75, z = 174, unlocked = true, setting = "darkAuctionWarp"),
    "castle" to WarpPoint(x = -250, y = 130, z = 45, unlocked = true, setting = "castleWarp")
)
