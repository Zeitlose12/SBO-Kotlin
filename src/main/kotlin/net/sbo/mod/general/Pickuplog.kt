package net.sbo.mod.general

import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.diana.DianaTracker
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.World
import net.sbo.mod.utils.data.Item

object Pickuplog {
    private var oldPurse: Long = 0L
    private var newPurse: Long = 0L

    private var oldInventory = mutableMapOf<String, Item>()
    private var newInventory = mutableMapOf<String, Item>()

    fun init() {
        Register.onTick(20) {
            if (mc.player == null || !World.isInSkyblock()) return@onTick
            newInventory = Helper.readPlayerInv(true)
            newPurse = Helper.getPurse()
            if (oldInventory.isEmpty()) {
                oldInventory = newInventory
                oldPurse = newPurse
                return@onTick
            }
            compareInventory()
        }
    }

    fun compareInventory() {
        val purseChange = newPurse - oldPurse
        if (purseChange != 0L) {
            Chat.chat("§6[SBO] §aYour purse has changed by §e$purseChange coins.")
        }

        val newItems = mutableListOf<Item>()
        val changedItemCounts = mutableListOf<Pair<Item, Int>>()

        for ((key, newItem) in newInventory) {
            val oldItem = oldInventory[key]
            if (oldItem == null) {
                newItems.add(newItem)
            } else if (newItem.count != oldItem.count) {
                val countChange = newItem.count - oldItem.count
                changedItemCounts.add(Pair(newItem, countChange))
            }
        }

        val removedItems = oldInventory.filter { (key, _) -> !newInventory.containsKey(key) }

        if (newItems.isNotEmpty()) {
            val itemList = newItems.joinToString(", ") { "${it.name} (${it.count})" }
            Chat.chat("§6[SBO] §aYou picked up new items: §e$itemList")
            for (item in newItems) {
                if (item.itemUUID != "") {
                    DianaTracker.trackWithPickuplog(item)
                } else {
                    DianaTracker.trackWithPickuplogStackable(item, item.count)
                }
            }
        }

        if (changedItemCounts.isNotEmpty()) {
            val itemList = changedItemCounts.joinToString(", ") {
                "${it.first.name} (${if (it.second > 0) "+" else ""}${it.second})"
            }
            Chat.chat("§6[SBO] §aItem counts changed: §e$itemList")
            for ((item, countChange) in changedItemCounts) {
                if (countChange > 0) {
                    DianaTracker.trackWithPickuplogStackable(item, countChange)
                }
            }
        }

        if (removedItems.isNotEmpty()) {
            val removedItemList = removedItems.values.joinToString(", ") { "${it.name} (${it.count})" }
            Chat.chat("§6[SBO] §cYou lost items: §e$removedItemList")
        }

        oldPurse = newPurse
        oldInventory = newInventory
    }
}
