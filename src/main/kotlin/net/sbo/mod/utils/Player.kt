package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.SboVec

object Player {
    fun getLastPosition(): SboVec {
        val player = mc.player ?: return SboVec(0.0, 0.0, 0.0)
        return SboVec(player.x, player.y, player.z)
    }
}