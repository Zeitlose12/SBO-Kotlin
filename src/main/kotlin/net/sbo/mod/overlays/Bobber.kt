package net.sbo.mod.overlays

import net.sbo.mod.utils.Register
import net.sbo.mod.settings.categories.General
import net.sbo.mod.utils.World
import net.minecraft.entity.projectile.FishingBobberEntity
import net.sbo.mod.utils.overlay.Overlay
import net.sbo.mod.utils.overlay.OverlayManager
import net.sbo.mod.utils.overlay.OverlayTextLine


object Bobber {
    var bobberCount: Int = 0
    val bobberOverlay: Overlay = Overlay("bobberOverlay", 10.0f, 10.0f, 1.0f)
    val overlayText: OverlayTextLine = OverlayTextLine("")

    fun init() {
        bobberOverlay.setCondition { General.bobberOverlay }
        bobberOverlay.addLine(overlayText)
        Register.onTick(20) { client ->
            if (!General.bobberOverlay || !World.isInSkyblock()) return@onTick
            val player = client.player ?: return@onTick
            val world = client.world ?: return@onTick
            val nearbyBobbers = world.entities.filter { entity ->
                entity is FishingBobberEntity && entity.distanceTo(player) < 31
            }
            bobberCount = nearbyBobbers.size
            overlayText.text = "§e§lBobber: §b§l$bobberCount"
        }
    }
}