package net.sbo.mod.diana

import net.sbo.mod.guis.partyfinder.pages.Help
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.settings.categories.QOL
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Helper.allowSackTracking
import net.sbo.mod.utils.Helper.checkDiana
import net.sbo.mod.utils.Helper.lastDianaMobDeath
import net.sbo.mod.utils.Helper.lastInqDeath
import net.sbo.mod.utils.Helper.lastLootShare
import net.sbo.mod.utils.Helper.removeFormatting
import net.sbo.mod.utils.Helper.sleep
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.SBOTimerManager.timerSession
import net.sbo.mod.utils.World.isInSkyblock
import net.sbo.mod.utils.data.DianaTracker
import net.sbo.mod.utils.data.DianaTrackerSessionData
import net.sbo.mod.utils.data.Item
import net.sbo.mod.utils.data.SboDataObject
import net.sbo.mod.utils.data.SboDataObject.dianaTrackerMayor
import net.sbo.mod.utils.data.SboDataObject.dianaTrackerSession
import net.sbo.mod.utils.data.SboDataObject.dianaTrackerTotal
import net.sbo.mod.utils.data.SboDataObject.saveTrackerData
import net.sbo.mod.utils.data.SboDataObject.sboData
import java.util.regex.Pattern

// todo: all the backtoback messages, achievements, diana loot prices

object DianaTracker {
    private val rareDrops = mapOf<String, String>("DWARF_TURTLE_SHELLMET" to "§9", "CROCHET_TIGER_PLUSHIE" to "§5", "ANTIQUE_REMEDIES" to "$5", "MINOS_RELIC" to "§5")
    private val otherDrops = listOf("ENCHANTED_ANCIENT_CLAW", "ANCIENT_CLAW", "ENCHANTED_GOLD", "ENCHANTED_IRON")
    private val sackDrops = listOf("Enchanted Gold", "Enchanted Iron", "Ancient Claw")
    private val forbiddenCoins = listOf(1L, 5L, 20L, 1000L, 2000L, 3000L, 4000L, 5000L, 7500L, 8000L, 10000L, 12000L, 15000L, 20000L, 25000L, 40000L, 50000L)

    private val lootAnnouncerBuffer: MutableList<String> = mutableListOf()
    private var lootAnnouncerBool: Boolean = false

    fun init() {
        Register.command("sboresetsession") {
            dianaTrackerSession = DianaTrackerSessionData()
            timerSession.reset()
            SboDataObject.save("DianaTrackerSessionData")
            Chat.chat("§6[SBO] §aDiana session tracker has been reset.")
        }

        Register.command("sboresetstatstracker") {
            sboData.mobsSinceInq = 0
            sboData.inqsSinceChim = 0
            sboData.minotaursSinceStick = 0
            sboData.champsSinceRelic = 0
            sboData.inqsSinceLsChim = 0
            SboDataObject.save("SboData")
        }

        Register.onChatMessageCancable(Pattern.compile("(.*?) (.*?) §r§efound a §r§cPhoenix §r§epet!(.*?)", Pattern.DOTALL)) { message, matchResult ->
            if (QOL.phoenixAnnouncer) {
                Chat.chat("§6[SBO] §cGG §eFound a Phoenix pet!")
                Helper.showTitle("§c§lPhoenix Pet!", "", 0, 25, 35)
            }
            if (Helper.getSecondsPassed(lastDianaMobDeath) > 2) return@onChatMessageCancable true
            val player = matchResult.group(2).removeFormatting()
            if (Player.getName() != Helper.getPlayerName(player)) return@onChatMessageCancable true
//            if (isInSkyblock() && checkDiana()) unlockAchievement(77); // phoenix pet
            true
        }

        trackBurrowsWithChat()
        trackMobsWithChat()
        trackCoinsWithChat()
        trackTreasuresWithChat()
        trackRngDropsWithChat()
    }

    fun trackWithPickuplog(item: Item) {
        val isLootShare = Helper.getSecondsPassed(lastLootShare) <= 2
        if (Helper.getSecondsPassed(item.creation) > 2) return
        if (Helper.getSecondsPassed(lastDianaMobDeath) > 2 && !isLootShare) return
        if (!checkDiana()) return
        if (item.itemId in rareDrops.keys) {
            val msg = Helper.toTitleCase(item.itemId.replace("_", " "))
            if (item.itemId == "MINOS_RELIC") {
                if (Diana.sendSinceMessage) Chat.chat("§6[SBO] §eTook §c${sboData.champsSinceRelic} §eChampions to get a Minos Relic!")
                if (sboData.champsSinceRelic == 1) {
                    Chat.chat("&6[SBO] &cb2b Minos Relic!")
//                    unlockAchievement(5) // b2b relic
                }
                if (isLootShare) {
                    Chat.chat("§6[SBO] §cLootshared a Minos Relic!")
//                    unlockAchievement(17) // relic ls
                }
                sboData.champsSinceRelic = 0

                if (Diana.lootAnnouncerScreen) {
                    val subTitle = if (Diana.lootAnnouncerPrice) "§6${Helper.formatNumber(100000)} coins" else "" // todo: get price from api
                    Helper.showTitle("§d§lMinos Relic!", subTitle, 0, 25, 35)
                }

                announceLootToParty(item.itemId)
                SboDataObject.save("SboData")
            }

            if (Diana.lootAnnouncerChat) {
                Chat.chat("§6[SBO] §6§lRARE DROP! ${rareDrops[item.itemId]}$msg")
            }

            if (Helper.getSecondsPassed(lastInqDeath) > 2 || item.itemId == "MINOS_RELIC") {
                trackItem(item.itemId, item.count)
            } else {
                announceLootToParty(item.itemId)
                if (!isLootShare)
                    trackItem(item.itemId, item.count, true)
                else
                    trackItem(item.itemId + "_LS", item.count, true)
            }
        }
    }

    fun trackWithPickuplogStackable(item: Item, amount: Int) {
        if (Helper.getSecondsPassed(lastDianaMobDeath) > 2 && Helper.getSecondsPassed(lastLootShare) > 2) return
        if (!checkDiana()) return
        if (item.itemId in otherDrops) {
            trackItem(item.itemId, amount)
            return
        }
    }

    fun trackWithSacksMessage(itemName: String, amount: Int) {
        if (!allowSackTracking) return
        if (!checkDiana()) return
        if (sackDrops.contains(itemName)) {
            trackItem(itemName, amount)
        }
    }

    fun trackScavengerCoins(amount: Long) {
        if (amount <= 0) return
        if (Helper.getSecondsPassed(lastDianaMobDeath) > 4 && Helper.getSecondsPassed(lastLootShare) > 4) return
        if (!checkDiana()) return
        if (amount in forbiddenCoins) return
        trackItem("SCAVENGER_COINS", amount.toInt())
        trackItem("COINS", amount.toInt())
    }

    fun trackMobsWithChat() {
        Register.onChatMessageCancable(Pattern.compile("(.*?) §r§eYou dug (.*?)§r§2(.*?)§r§e!(.*?)", Pattern.DOTALL)) { message, matchResult ->
            val mob = matchResult.group(3)
            when (mob) {
                "Minos Inquisitor" -> {
                    sboData.inqsSinceChim += 1
                    trackMob(mob, 1)

                    if (Diana.sendSinceMessage) {
                        val timeSinceInq = Helper.formatTime(dianaTrackerTotal.items.TIME - sboData.lastInqDate)
                        if (sboData.lastInqDate != 0L) {
                            Chat.chat("§6[SBO] §eTook §c${sboData.mobsSinceInq} §eMobs and §c$timeSinceInq §eto get an Inquis!")
                        } else {
                            Chat.chat("§6[SBO] §eTook §c${sboData.mobsSinceInq} §eMobs to get an Inquis!")
                        }
                    }
                    sboData.lastInqDate = dianaTrackerTotal.items.TIME

                    if (sboData.b2bInq && sboData.mobsSinceInq == 1) {
                        Chat.chat("§6[SBO] §cb2b2b Inquisitor!")
//                        unlockAchievement(7) // b2b2b inq
                    }
                    if (sboData.mobsSinceInq == 1 && !sboData.b2bInq) {
                        Chat.chat("§6[SBO] §cb2b Inquisitor!")
//                        unlockAchievement(6) // b2b inq
                        sboData.b2bInq = true
                    }
                    if (sboData.inqsSinceChim >= 2) sboData.b2bChim = false

                    sboData.mobsSinceInq = 0
                }
                "Minos Champion" -> {
                    sboData.champsSinceRelic += 1
                    trackMob(mob, 1)
                }
                "Minotaur" -> {
                    sboData.minotaursSinceStick += 1
                    if (sboData.minotaursSinceStick >= 2) sboData.b2bStick = false
                    trackMob(mob, 1)
                }
                "Gaia Construct" -> trackMob(mob, 1)
                "Siamese Lynxes" -> trackMob(mob, 1)
                "Minos Hunter" -> trackMob(mob, 1)
            }
            SboDataObject.save("SboData")
            true
        }
    }

    fun trackCoinsWithChat() {
        Register.onChatMessageCancable(Pattern.compile("§r§6§lWow! §r§eYou dug out §r§6(.*?) coins§r§e!", Pattern.DOTALL)) { message, matchResult ->
            val coins = matchResult.group(1).replace(",", "").toIntOrNull() ?: 0
            if (coins > 0) trackItem("COINS", coins)
            true
        }
    }

    fun trackTreasuresWithChat() {
        Register.onChatMessageCancable(Pattern.compile("§r§6§lRARE DROP! §r§eYou dug out a §r(.*?)§r§e!", Pattern.DOTALL)) { message, matchResult ->
            val drop = matchResult.group(1).drop(2)
            when (drop) {
                "Griffin Feather" -> trackItem(drop, 1)
                "Crown of Greed" -> trackItem(drop, 1)
                "Washed-Up Souvenir" -> trackItem(drop, 1)
            }
            true
        }
    }

    fun trackRngDropsWithChat() { // todo: play sound
        Register.onChatMessageCancable(Pattern.compile("§r§6§lRARE DROP! §r(.*?)", Pattern.DOTALL)) { message, matchResult ->
            if (!checkDiana()) return@onChatMessageCancable true
            var drop = matchResult.group(1)

            val magicfind = Helper.getMagicFind(drop)
            var mfPrefix = ""
            if (magicfind > 0) mfPrefix = " (+$magicfind ✯ Magic Find)"

            drop = drop.substring(2, 16)
            when (drop) {
                "Enchanted Book" -> {
                    if (Diana.lootAnnouncerScreen) {
                        val subTitle = if (Diana.lootAnnouncerPrice) "§6${Helper.formatNumber(100000)} coins" else "" // todo: get price from api
                        Helper.showTitle("§d§lChimera!", subTitle, 0, 25, 35)
                    }

                    val customChimMsg = Helper.checkCustomChimMessage(magicfind)
                    if (customChimMsg.first) {
                        Chat.chat(customChimMsg.second)
                        announceLootToParty("Chimera!", customChimMsg.second, true)
                    } else
                        announceLootToParty("Chimera!", "Chimera!$mfPrefix")

                    if (Helper.getSecondsPassed(lastDianaMobDeath) > 2) { // todo: track mf like in ct
                        // normal chim
                        if (Diana.sendSinceMessage) Chat.chat("§6[SBO] §eTook §c${sboData.inqsSinceChim} §eInquisitors to get a Chimera!")

                        trackItem("CHIMERA", 1)
                        if (sboData.b2bChim && sboData.inqsSinceChim == 1) {
                            Chat.chat("&6[SBO] &cb2b2b Chimera!")
//                            unlockAchievement(2) // b2b2b chim
                        }
                        if (sboData.inqsSinceChim == 1 && !sboData.b2bChim) {
                            Chat.chat("&6[SBO] &cb2b Chimera!")
                            sboData.b2bChim = true
//                            unlockAchievement(1) // b2b chim
                        }
//                        if (sboData.b2bChim && sboData.b2bInq) {
//                            unlockAchievement(75) // b2b chim from b2b inq
//                        }
                        sboData.inqsSinceChim = 0
                    } else {
                        // lootshare chim
                        if (Diana.sendSinceMessage) Chat.chat("§6[SBO] §eTook §c${sboData.inqsSinceLsChim} §eInquisitors to lootshare a Chimera!")

                        trackItem("CHIMERA_LS", 1)

                        sleep(200) {
                            if (sboData.b2bChimLs && sboData.inqsSinceLsChim == 1) {
                                Chat.chat("&6[SBO] &cb2b2b Lootshare Chimera!")
//                                unlockAchievement(67) // b2b2b chim ls
                            }
                            if (sboData.inqsSinceLsChim == 1 && !sboData.b2bChimLs) {
                                Chat.chat("&6[SBO] &cb2b Lootshare Chimera!")
                                sboData.b2bChimLs = true
//                                unlockAchievement(66) // b2b chim ls
                            }
                            sboData.inqsSinceLsChim = 0
                        }
                    }
                }
                "Daedalus Stick" -> {
                    if (Diana.lootAnnouncerScreen) {
                        val subTitle = if (Diana.lootAnnouncerPrice) "§6${Helper.formatNumber(100000)} coins" else "" // todo: get price from api
                        Helper.showTitle("§d§lDaedalus Stick!", subTitle, 0, 25, 35)
                    }

                    if (Diana.sendSinceMessage) Chat.chat("§6[SBO] §eTook §c${sboData.minotaursSinceStick} §eMinotaurs to get a Daedalus Stick!")
                    announceLootToParty("Daedalus Stick!", "Daedalus Stick!$mfPrefix")

                    if (Helper.getSecondsPassed(lastLootShare) <= 2) {
                        // lootshare stick
                        Chat.chat("§6[SBO] §cLootshared a Daedalus Stick!")
//                        unlockAchievement(15)
                    }

                    trackItem("DAEDALUS_STICK", 1)
                    if (sboData.b2bStick && sboData.minotaursSinceStick == 1) {
                        Chat.chat("&6[SBO] &cb2b2b Daedalus Stick!")
//                        unlockAchievement(4) // b2b2b stick
                    }
                    if (sboData.minotaursSinceStick == 1 && !sboData.b2bStick) {
                        Chat.chat("&6[SBO] &cb2b Daedalus Stick!")
                        sboData.b2bStick = true
//                        unlockAchievement(3) // b2b stick
                    }
                    sboData.minotaursSinceStick = 0
                }
            }
            SboDataObject.save("SboData")
            true
        }
    }

    fun trackBurrowsWithChat() {
        Register.onChatMessageCancable(Pattern.compile("§r§eYou dug out a Griffin Burrow! (.*?)", Pattern.DOTALL)) { message, matchResult ->
            trackItem("TOTAL_BURROWS", 1)
            val burrow = matchResult.group(1).trim().removeFormatting()
            if (Diana.fourEyedFish) {
                if (burrow.contains("(2/4)") || burrow.contains("(3/4)")) {
                    trackItem("FISH_COINS", 4000)
                    trackItem("COINS", 4000)
                } else {
                    trackItem("FISH_COINS", 2000)
                    trackItem("COINS", 2000)
                }
            }
            true
        }
        Register.onChatMessageCancable(Pattern.compile("§r§eYou finished the Griffin burrow chain!(.*?)", Pattern.DOTALL)) { message, matchResult ->
            trackItem("TOTAL_BURROWS", 1)
            if (Diana.fourEyedFish) {
                trackItem("FISH_COINS", 2000)
                trackItem("COINS", 2000)
            }
            true
        }
    }

    fun announceLootToParty(item: String, customMsg: String? = null, replaceChimMessage: Boolean = false) {
        if (!Diana.lootAnnouncerParty) return
        var msg = Helper.toTitleCase(item.replace("_LS", "").replace("_", " "))
        if (customMsg != null) msg = customMsg.removeFormatting()

        if (replaceChimMessage) {
            Chat.command("pc $msg")
        } else {
            lootAnnouncerBuffer.add(msg)
            if (!lootAnnouncerBool) {
                lootAnnouncerBool = true
                sleep(1500) {
                    sendLootAnnouncement()
                    lootAnnouncerBool = false
                }
            }
        }
    }

    fun sendLootAnnouncement() {
        if (lootAnnouncerBuffer.isEmpty()) return
        val msg = lootAnnouncerBuffer.joinToString(", ")
        lootAnnouncerBuffer.clear()
        Chat.command("pc [SBO] RARE DROP! $msg")
    }

    fun getB2BMessage(itemName: String, streak: Int): String? {
        if (streak <= 1) return null

        val prettyName = Helper.toTitleCase(itemName.replace("_", " "))
        val streakText = "b" + "2b".repeat(streak - 1)

        return "§6[SBO] §c$streakText $prettyName!"
    }

    fun trackMob(item: String, amount: Int) {
        trackItem(item, amount)
        trackItem("TOTAL_MOBS", amount)
        sboData.mobsSinceInq += amount
        if (sboData.mobsSinceInq >= 2) sboData.b2bInq = false
        SboDataObject.save("SboData")
    }

    fun trackItem(item: String, amount: Int, fromInq: Boolean = false) {
        val itemName = Helper.toUpperSnakeCase(item)
        if (itemName == "MINOS_INQUISITOR_LS") sboData.inqsSinceLsChim += 1

        trackOne(dianaTrackerMayor, itemName, amount, fromInq)
        trackOne(dianaTrackerSession, itemName, amount, fromInq)
        trackOne(dianaTrackerTotal, itemName, amount, fromInq)
        saveTrackerData()
    }

    fun trackOne(tracker: DianaTracker, item: String, amount: Int, fromInq: Boolean = false) {
        if (!fromInq) {
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
                "MINOS_INQUISITOR_LS" -> tracker.mobs.MINOS_INQUISITOR_LS += amount
                "MINOS_CHAMPION" -> tracker.mobs.MINOS_CHAMPION += amount
                "MINOTAUR" -> tracker.mobs.MINOTAUR += amount
                "GAIA_CONSTRUCT" -> tracker.mobs.GAIA_CONSTRUCT += amount
                "SIAMESE_LYNXES" -> tracker.mobs.SIAMESE_LYNXES += amount
                "MINOS_HUNTER" -> tracker.mobs.MINOS_HUNTER += amount
                "TOTAL_MOBS" -> tracker.mobs.TOTAL_MOBS += amount
                "DWARF_TURTLE_SHELMET_LS" -> tracker.inquis.DWARF_TURTLE_SHELMET_LS += amount
                "CROCHET_TIGER_PLUSHIE" -> tracker.inquis.CROCHET_TIGER_PLUSHIE += amount
                "CROCHET_TIGER_PLUSHIE_LS" -> tracker.inquis.CROCHET_TIGER_PLUSHIE_LS += amount
                "ANTIQUE_REMEDIES_LS" -> tracker.inquis.ANTIQUE_REMEDIES_LS += amount
                "SCAVENGER_COINS" -> tracker.items.SCAVENGER_COINS += amount
                "FISH_COINS" -> tracker.items.FISH_COINS += amount
                "TOTAL_BURROWS" -> tracker.items.TOTAL_BURROWS += amount
            }
        } else {
            when (item) {
                "DWARF_TURTLE_SHELMET" -> tracker.inquis.DWARF_TURTLE_SHELMET += amount
                "DWARF_TURTLE_SHELMET_LS" -> tracker.inquis.DWARF_TURTLE_SHELMET_LS += amount
                "CROCHET_TIGER_PLUSHIE" -> tracker.inquis.CROCHET_TIGER_PLUSHIE += amount
                "CROCHET_TIGER_PLUSHIE_LS" -> tracker.inquis.CROCHET_TIGER_PLUSHIE_LS += amount
                "ANTIQUE_REMEDIES" -> tracker.inquis.ANTIQUE_REMEDIES += amount
                "ANTIQUE_REMEDIES_LS" -> tracker.inquis.ANTIQUE_REMEDIES_LS += amount
            }
        }
    }
}