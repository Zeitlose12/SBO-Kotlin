package net.sbo.mod.diana

import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Helper.allowSackTracking
import net.sbo.mod.utils.Helper.checkDiana
import net.sbo.mod.utils.Helper.lastLootShare
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.SBOTimerManager.timerSession
import net.sbo.mod.utils.data.DianaTracker
import net.sbo.mod.utils.data.DianaTrackerSessionData
import net.sbo.mod.utils.data.Item
import net.sbo.mod.utils.data.SboDataObject
import net.sbo.mod.utils.data.SboDataObject.dianaTrackerMayor
import net.sbo.mod.utils.data.SboDataObject.dianaTrackerSession
import net.sbo.mod.utils.data.SboDataObject.dianaTrackerTotal
import net.sbo.mod.utils.data.SboDataObject.saveTrackerData

object DianaTracker {
    private var lastDianaMobDeath: Long = 0L // replaces mobDeath2SecsTrue and mobDeath4SecsTrue todo: implement this properly

    private val rngDrops = listOf("Enchanted Book", "Daedalus Stick")
    private val rareDrops = listOf("DWARF_TURTLE_SHELLMET", "CROCHET_TIGER_PLUSHIE", "ANTIQUE_REMEDIES", "MINOS_RELIC") // not trackable by chat
    private val otherDrops = listOf("ENCHANTED_ANCIENT_CLAW", "ANCIENT_CLAW", "ENCHANTED_GOLD", "ENCHANTED_IRON")
    private val sackDrops = listOf("Enchanted Gold", "Enchanted Iron", "Ancient Claw")
    private val forbiddenCoins = listOf(1L, 5L, 20L, 1000L, 2000L, 3000L, 4000L, 5000L, 7500L, 8000L, 10000L, 12000L, 15000L, 20000L, 25000L, 40000L, 50000L)

    fun init() {
        Register.command("sboresetsession") {
            dianaTrackerSession = DianaTrackerSessionData()
            timerSession.reset()
            SboDataObject.save("DianaTrackerSessionData")
            Chat.chat("§6[SBO] §aDiana session tracker has been reset.")
        }
    }

    fun trackWithPickuplog(item: Item) {
        if (Helper.getSecondsPassed(item.creation) > 2) return
        if (Helper.getSecondsPassed(lastDianaMobDeath) > 2) return
        if (Helper.getSecondsPassed(lastLootShare) > 2) return
        if (!checkDiana()) return
        if (item.itemId in rareDrops) {
            Chat.chat("§6[SBO] §aYou picked up a Diana item: §e${item.name} (${item.count}) itemcreation date: ${Helper.timestampToDate(item.creation)}")
            trackItem(item.itemId, item.count)
            return
        }
        Chat.chat("§6[SBO] §aYou probably picked up a Diana item: §e${item.name}: age in seconds: ${Helper.getSecondsPassed(item.creation)}")
    }

    fun trackWithPickuplogStackable(item: Item, amount: Int) {
        if (Helper.getSecondsPassed(lastDianaMobDeath) > 2) return
        if (Helper.getSecondsPassed(lastLootShare) > 2) return
        if (!checkDiana()) return
        if (item.itemId in otherDrops) {
            Chat.chat("§6[SBO] §aYou picked up a Diana item: §e${item.name} (${amount}) itemcreation date: ${Helper.timestampToDate(item.creation)}")
            trackItem(item.itemId, amount)
            return
        }
        Chat.chat("§6[SBO] §aYou probably picked up a Diana item thats stackable: §e${item.name}")
    }

    fun trackWithSacksMessage(itemName: String, amount: Int) {
        if (!allowSackTracking) return
        if (!checkDiana()) return
        if (sackDrops.contains(itemName)) {
            Chat.chat("§6[SBO] §aYou picked up a Diana item from a sack: §e$itemName (${amount})")
            trackItem(itemName, amount)
        }
    }

    fun trackScavengerCoins(amount: Long) {
        if (amount <= 0) return
        if (Helper.getSecondsPassed(lastDianaMobDeath) > 4) return
        if (Helper.getSecondsPassed(lastLootShare) > 4) return
        if (!checkDiana()) return
        if (amount in forbiddenCoins) return
        trackItem("SCAVENGER_COINS", amount.toInt())
        trackItem("COINS", amount.toInt())
    }

    fun trackItem(item: String, amount: Int) {
        val itemName = Helper.toUpperSnakeCase(item)
        trackOne(dianaTrackerMayor, itemName, amount)
        trackOne(dianaTrackerSession, itemName, amount)
        trackOne(dianaTrackerTotal, itemName, amount)
        saveTrackerData()
    }

    fun trackOne(tracker: DianaTracker, item: String, amount: Int) {
        when (item) {
            "COINS" -> tracker.items.COINS += amount
            "GRIFFIN_FEATHER" -> tracker.items.GRIFFIN_FEATHER += amount
            "CROWN_OF_GREED" -> tracker.items.CROWN_OF_GREED += amount
            "WASHED_UP_SOUVENIR" -> tracker.items.WASHED_UP_SOUVENIR += amount
            "CHIMERA" -> tracker.items.CHIMERA += amount
            "CHIMERA_LS" -> tracker.items.CHIMERA_LS += amount
            "DAEDALUS_STICK" -> tracker.items.DAEDALUS_STICK += amount
            "DWARF_TURTLE_SHELMET" -> tracker.items.DWARF_TURTLE_SHELMET += amount
            "ANTIQUE_REMEDIES" -> tracker.items.ANTIQUE_REMEDIES += amount
            "ENCHANTED_ANCIENT_CLAW" -> tracker.items.ENCHANTED_ANCIENT_CLAW += amount
            "ANCIENT_CLAW" -> tracker.items.ANCIENT_CLAW += amount
            "MINOS_RELIC" -> tracker.items.MINOS_RELIC += amount
            "ENCHANTED_GOLD" -> tracker.items.ENCHANTED_GOLD += amount
            "ENCHANTED_IRON" -> tracker.items.ENCHANTED_IRON += amount
            "MINOS_INQUISITOR" -> tracker.mobs.MINOS_INQUISITOR += amount
            "MINOS_CHAMPION" -> tracker.mobs.MINOS_CHAMPION += amount
            "MINOTAUR" -> tracker.mobs.MINOTAUR += amount
            "GAIA_CONSTRUCT" -> tracker.mobs.GAIA_CONSTRUCT += amount
            "SIAMESE_LYNXES" -> tracker.mobs.SIAMESE_LYNXES += amount
            "MINOS_HUNTER" -> tracker.mobs.MINOS_HUNTER += amount
            "TOTAL_MOBS" -> tracker.mobs.TOTAL_MOBS += amount
            "MINOS_INQUISITOR_LS" -> tracker.mobs.MINOS_INQUISITOR_LS += amount
            "DWARF_TURTLE_SHELMET_LS" -> tracker.inquis.DWARF_TURTLE_SHELMET_LS += amount
            "CROCHET_TIGER_PLUSHIE" -> tracker.inquis.CROCHET_TIGER_PLUSHIE += amount
            "CROCHET_TIGER_PLUSHIE_LS" -> tracker.inquis.CROCHET_TIGER_PLUSHIE_LS += amount
            "ANTIQUE_REMEDIES_LS" -> tracker.inquis.ANTIQUE_REMEDIES_LS += amount
            "SCAVENGER_COINS" -> tracker.items.SCAVENGER_COINS += amount
            "FISH_COINS" -> tracker.items.FISH_COINS += amount
            "TOTAL_BURROWS" -> tracker.items.TOTAL_BURROWS += amount
        }
    }
}