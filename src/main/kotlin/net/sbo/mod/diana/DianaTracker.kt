package net.sbo.mod.diana

import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.data.Item

object DianaTracker {
    private var isDianaActive: Boolean = true // replaces checkDiana
    private var lastDianaMobDeath: Long = 0L // replaces mobDeath2SecsTrue and mobDeath4SecsTrue
    private var gotLootShare: Boolean = false

    private val rngDrops = listOf("Enchanted Book", "Daedalus Stick")
    private val rareDrops = listOf("DWARF_TURTLE_SHELLMET", "CROCHET_TIGER_PLUSHIE", "ANTIQUE_REMEDIES", "MINOS_RELIC") // not trackable by chat
    private val otherDrops = listOf("ENCHANTED_ANCIENT_CLAW", "ANCIENT_CLAW", "ENCHANTED_GOLD", "ENCHANTED_IRON", "")
    private val forbiddenCoins = listOf(1, 5, 20, 1000, 2000, 3000, 4000, 5000, 7500, 8000, 10000, 12000, 15000, 20000, 25000, 40000, 50000)

    fun init() {

    }

    fun trackWithPickuplog(item: Item) {
        val currentTime = System.currentTimeMillis()
        if (item.creation - currentTime > 2000) return // ignore items older than 2 seconds
        if (lastDianaMobDeath - currentTime > 2000) return // ignore items when the last mob died more than 2 seconds ago
        if (!isDianaActive) return
        if (item.itemId in rareDrops) {
            Chat.chat("§6[SBO] §aYou picked up a Diana item: §e${item.name} (${item.count}) itemcreation date: ${Helper.timestampToDate(item.creation)}")
            trackItem(item.itemId, item.count)
            return
        }
        Chat.chat("§6[SBO] §aYou probably picked up a Diana item: §e${item.name}")
    }

    fun trackWithPickuplogStackable(item: Item, amount: Int) {
        if (lastDianaMobDeath - System.currentTimeMillis() > 2000) return // ignore items when the last mob died more than 2 seconds ago
        if (!isDianaActive) return
        if (item.itemId in otherDrops) {
            Chat.chat("§6[SBO] §aYou picked up a Diana item: §e${item.name} (${amount}) itemcreation date: ${Helper.timestampToDate(item.creation)}")
            trackItem(item.itemId, amount)
            return
        }
        Chat.chat("§6[SBO] §aYou probably picked up a Diana item thats stackable: §e${item.name}")
    }

    fun trackWithSacksMessage() {

    }

    fun trackItem(item: String, amount: Int) {

    }

}