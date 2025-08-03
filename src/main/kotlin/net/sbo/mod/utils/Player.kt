package net.sbo.mod.utils

import net.minecraft.client.MinecraftClient
import net.sbo.mod.utils.SboVec

object Player {
    fun getLastPosition(): SboVec {
        val player = MinecraftClient.getInstance().player ?: return SboVec(0.0, 0.0, 0.0)
        return SboVec(player.x, player.y, player.z)
    }
}