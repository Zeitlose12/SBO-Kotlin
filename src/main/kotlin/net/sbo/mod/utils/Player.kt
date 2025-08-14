package net.sbo.mod.utils

import com.mojang.serialization.DataResult
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.SboVec
import net.minecraft.nbt.NbtCompound
import net.sbo.mod.utils.Helper.getCursorItemStack
import net.sbo.mod.utils.data.Item
import java.util.UUID

object Player {
    fun getLastPosition(): SboVec {
        val player = mc.player ?: return SboVec(0.0, 0.0, 0.0)
        return SboVec(player.x, player.y, player.z)
    }

    fun getUUIDString(): String {
        return mc.player?.uuidAsString ?: ""
    }

    fun getUUID(): UUID {
        return mc.player?.uuid ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
    }

    fun getPlayerInventory(): List<ItemStack> {
        val inventory = mc.player?.inventory?.toList()
        return inventory ?: emptyList()
    }
}