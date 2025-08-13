package net.sbo.mod.utils

import com.mojang.serialization.DataResult
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.SboVec
import net.minecraft.nbt.NbtCompound
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

    fun getPlayerInventory(): MutableMap<String, Item> {
        val inventory = mc.player?.inventory
        if (inventory == null) return mutableMapOf()
        val invItems = mutableMapOf<String, Item>()
        for (slot in 0 until inventory.size()) {
            if (slot == 8) continue // Skip SB Star
            val stack: ItemStack = inventory.getStack(slot)
            if (!stack.isEmpty) {
                val customData = stack.get(DataComponentTypes.CUSTOM_DATA)
                val item = Item(
                    ItemUtils.getSBID(customData),
                    ItemUtils.getUUID(customData),
                    ItemUtils.getDisplayName(stack),
                    ItemUtils.getTimestamp(customData),
                    stack.count
                )

                if (invItems.containsKey(item.itemId)) {
                    invItems[item.itemId]?.count += item.count
                } else {
                    invItems[item.itemId] = item
                }
            }
        }

        return invItems
    }
}