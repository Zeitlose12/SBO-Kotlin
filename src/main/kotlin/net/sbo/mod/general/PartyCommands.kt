package net.sbo.mod.general

import net.sbo.mod.utils.Register
import net.sbo.mod.settings.categories.PartyCommands
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Chat.chat
import net.sbo.mod.SBOKotlin
import net.sbo.mod.utils.Helper.sleep
import net.sbo.mod.utils.Helper.getPlayerName
import net.sbo.mod.utils.Helper.calcPercentOne
import net.sbo.mod.utils.Helper.formatNumber
import net.sbo.mod.utils.Helper.formatTime
import java.util.regex.Pattern

object PartyCommands {
    val commandRegex = Regex("^§[0-9a-fk-or]Party §[0-9a-fk-or]> (.*?)§[0-9a-fk-or]*: ?(.*)\$")
    val data = SBOKotlin.SBOConfigBundle.sboData
    val dianaTrackerMayor = SBOKotlin.SBOConfigBundle.dianaTrackerMayorData
    val dianaTrackerTotal = SBOKotlin.SBOConfigBundle.dianaTrackerTotalData
    val settings = PartyCommands
    val carrot = listOf(
        "As I see it, Carrot",
        "It is Carrot",
        "It is decidedly Carrot",
        "Most likely Carrot",
        "Outlook Carrot",
        "Signs point to Carrot",
        "Without a Carrot",
        "Yes - Carrot",
        "Carrot - definitely",
        "You may rely on Carrot",
        "Ask Carrot later",
        "Carrot predict now",
        "Concentrate and ask Carrot ",
        "Don't count on it - Carrot 2024",
        "My reply is Carrot",
        "My sources say Carrot",
        "Outlook not so Carrot",
        "Very Carrot"
    )

    fun registerPartyChatListeners() {

        Register.onChatMessage(commandRegex) { message, matchResult ->
            val unformattedPlayerName = matchResult.groupValues[1]
            val fullMessage = matchResult.groupValues[2]
            val messageParts = fullMessage.trim().split(Regex("\\s+"))
            val command = messageParts.getOrNull(0)?.lowercase() ?: return@onChatMessage
            val secondArg = messageParts.getOrNull(1)
            val playerName = getPlayerName(unformattedPlayerName)
            val commandsWithArgs = setOf("!since", "!demote", "!promote", "!ptme", "!transfer", "!stats", "!totalstats")

            if (!settings.partyCommands) return@onChatMessage
            if (messageParts.size > 1 && command !in commandsWithArgs) return@onChatMessage

            when (command) {
                "!w", "!warp" -> {
                    if (!settings.warpCommand) return@onChatMessage
                    sleep(200) {
                        Chat.command("p warp")
                    }
                }
                "!allinv", "!allinvite" -> {
                    if (!settings.allinviteCommand) return@onChatMessage
                    sleep(200) {
                        Chat.command("p setting allinvite")
                    }
                }
                "!ptme", "!transfer" -> {
                    if (!settings.transferCommand) return@onChatMessage
                    sleep(200) {
                        Chat.command("p transfer $playerName")
                    }
                }
                "!demote" -> {
                    if (!settings.moteCommand) return@onChatMessage
                    val targetPlayer = secondArg ?: playerName
                    sleep(200) {
                        Chat.command("p demote $targetPlayer")
                    }
                }
                "!promote" -> {
                    if (!settings.moteCommand) return@onChatMessage
                    val targetPlayer = secondArg ?: playerName
                    sleep(200) {
                        Chat.command("p promote $targetPlayer")
                    }
                }
                "!c", "!carrot" -> {
                    if (!settings.carrotCommand) return@onChatMessage
                    val randomIndex = (carrot.indices).random()
                    val response = carrot[randomIndex]
                    sleep(200) {
                        Chat.command("pc $response")
                    }
                }
                "!time" -> {
                    if (!settings.timeCommand) return@onChatMessage
                    sleep(200) {
                        val currentTime = java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())
                        Chat.command("pc $currentTime")
                    }
                }
                "!tps" -> {
                    if (!settings.tpsCommand) return@onChatMessage
                    sleep(200) {
                        // todo: tpsCommand(player)
                    }
                }
                "!chim", "!chimera", "!chims", "!chimeras", "!book", "!books" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val chimeraCount = dianaTrackerMayor.items.Chimera
                    val chimeraLsCount = dianaTrackerMayor.items.ChimeraLs
                    val percent = calcPercentOne(dianaTrackerMayor.items, dianaTrackerMayor.mobs, "Chimera", "Minos Inquisitor")
                    sleep(200) {
                        val percentString = percent?.let { "%.2f".format(it) } ?: "0.00"
                        Chat.command("pc Chimera: $chimeraCount ($percentString%) +$chimeraLsCount LS")
                    }
                }
                "!inqsls", "!inquisitorls", "!inquisls", "!lsinq", "!lsinqs", "!lsinquisitor", "!lsinquis" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    sleep(200) {
                        Chat.command("pc Inquisitor LS: ${dianaTrackerMayor.mobs.`Minos Inquisitor Ls`}")
                    }
                }
                "!inq", "!inqs", "!inquisitor", "!inquis" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val inquisCount = dianaTrackerMayor.mobs.`Minos Inquisitor`
                    val percent = calcPercentOne(dianaTrackerMayor.items, dianaTrackerMayor.mobs, "Minos Inquisitor")
                    sleep(200) {
                        val percentString = percent?.let { "%.2f".format(it) } ?: "0.00"
                        Chat.command("pc Inquisitor: $inquisCount ($percentString%)")
                    }
                }
                "!burrows", "!burrow" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val burrows = dianaTrackerMayor.items.`Total Burrows`
                    // todo: implement burrows per hour calculation
                    val burrowsPerHrTxt = ""
                    sleep(200) {
                        Chat.command("pc Burrows: $burrows ($burrowsPerHrTxt/h)")
                    }
                }
                "!relic", "!relics" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val relicCount = dianaTrackerMayor.items.MINOS_RELIC
                    val percent = calcPercentOne(dianaTrackerMayor.items, dianaTrackerMayor.mobs, "MINOS_RELIC", "Minos Champion")
                    sleep(200) {
                        val percentString = percent?.let { "%.2f".format(it) } ?: "0.00"
                        Chat.command("pc Relics: $relicCount ($percentString%)")
                    }
                }
                "!chimls", "!chimerals", "!bookls", "!lschim", "!lsbook", "!lootsharechim", "!lschimera" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val chimsLs = dianaTrackerMayor.items.ChimeraLs
                    val inqLs = dianaTrackerMayor.mobs.`Minos Inquisitor Ls`
                    val percent = calcPercentOne(dianaTrackerMayor.items, dianaTrackerMayor.mobs, "ChimeraLs", "Minos Inquisitor Ls")
                    sleep(200) {
                        val percentString = percent?.let { "%.2f".format(it) } ?: "0.00"
                        Chat.command("pc Chimera LS: $chimsLs ($percentString%)")
                    }
                }
                "!sticks", "!stick" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val stickCount = dianaTrackerMayor.items.`Daedalus Stick`
                    val percent = calcPercentOne(dianaTrackerMayor.items, dianaTrackerMayor.mobs, "Daedalus Stick", "Minotaur")
                    sleep(200) {
                        val percentString = percent?.let { "%.2f".format(it) } ?: "0.00"
                        Chat.command("pc Sticks: $stickCount ($percentString%)")
                    }
                }
                "!feathers", "!feather" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val featherCount = dianaTrackerMayor.items.`Griffin Feather`
                    sleep(200) {
                        Chat.command("pc Feathers: $featherCount")
                    }
                }
                "!coins", "!coin" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    sleep(200) {
                        Chat.command("pc Coins: ${formatNumber(dianaTrackerMayor.items.coins, withCommas = true)}")
                    }
                }
                "!mobs", "!mob" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    val totalMobs = dianaTrackerMayor.mobs.TotalMobs
                    // todo: getMobsPerHour()
                    val mobsPerHrTxt = ""
                    sleep(200) {
                        Chat.command("pc Mobs: $totalMobs ($mobsPerHrTxt/h)")
                    }
                }
                "!mf", "!magicfind" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    sleep(200) {
                        Chat.command("pc Chims (${data.highestChimMagicFind}% ✯) Sticks (${data.highestStickMagicFind}% ✯)")
                    }
                }
                "!playtime" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    sleep(200) {
                        Chat.command("pc Playtime: ${formatTime(dianaTrackerMayor.items.mayorTime.toLong())}")
                    }
                }
                "!profits", "!profit" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    // todo: getDianaMayorTotalProfitAndOfferType()
                    sleep(200) {
                        val profit = 0
                        val offerType = "N/A"
                        val profitHour = 0
                        Chat.command("pc Profit: $profit ($offerType) $profitHour/h")
                    }
                }
                "!stats", "!stat" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    if (secondArg == playerName) {
                        // todo: sendPlayerStats()
                    }
                }
                "!totalstats", "!totalstat" -> {
                    if (!settings.dianaPartyCommands) return@onChatMessage
                    if (secondArg == playerName) {
                        // todo: sendPlayerStats(true)
                    }
                }
                "!since" -> {
                    val secondArg = messageParts.getOrNull(1)?.lowercase()
                    when (secondArg) {
                        "chimera", "chim", "chims", "chimeras", "book", "books" -> sleep(200) {
                            Chat.command("pc Inqs since chim: ${data.inqsSinceChim}")
                        }
                        "stick", "sticks" -> sleep(200) {
                            Chat.command("pc Minos since stick: ${data.minotaursSinceStick}")
                        }
                        "relic", "relics" -> sleep(200) {
                            Chat.command("pc Champs since relic: ${data.champsSinceRelic}")
                        }
                        "inq", "inqs", "inquisitor", "inquisitors", "inquis" -> sleep(200) {
                            Chat.command("pc Mobs since inq: ${data.mobsSinceInq}")
                        }
                        "lschim", "chimls", "lschimera", "chimerals", "lsbook", "bookls", "lootsharechim" -> sleep(200) {
                            Chat.command("pc Inqs since lootshare chim: ${data.inqsSinceLsChim}")
                        }
                        else -> sleep(200) {
                            Chat.command("pc Mobs since inq: ${data.mobsSinceInq}")
                        }
                    }
                }
            }
        }
    }
}