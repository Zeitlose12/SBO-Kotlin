package net.sbo.mod.overlays

import net.sbo.mod.utils.Register
import net.sbo.mod.settings.categories.General
import net.sbo.mod.utils.World
import net.minecraft.entity.projectile.FishingBobberEntity


object Bobber {
    private var bobberCount: Int = 0

    fun init() {
        Register.onTick(20) { client ->
            if (!General.bobberOverlay || !World.isInSkyblock()) return@onTick
            val player = client.player ?: return@onTick
            val world = client.world ?: return@onTick
            val nearbyBobbers = world.entities.filter { entity ->
                entity is FishingBobberEntity && entity.distanceTo(player) < 31
            }
            bobberCount = nearbyBobbers.size
        }
    }
}