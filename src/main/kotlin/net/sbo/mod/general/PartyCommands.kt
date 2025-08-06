package net.sbo.mod.general

import net.sbo.mod.utils.Register
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.sbo.mod.utils.Chat.chat
import net.sbo.mod.SBOKotlin.logger

object PartyCommands {
    fun registerPartyChatListeners() {
        val commandRegex = Regex("^§[0-9a-fk-or]Party §[0-9a-fk-or]> (.*?)§[0-9a-fk-or]*: ?(.*)\$")

        Register.onChatMessage(commandRegex) { message, matchResult ->
            val playername = matchResult.groupValues[1]
            val fullMessage = matchResult.groupValues[2]
            val messageParts = fullMessage.trim().split(Regex("\\s+"))
            val command = messageParts.getOrNull(0)?.lowercase() ?: return@onChatMessage

            if (command != "!since" && messageParts.size != 1) return@onChatMessage

            when (command) {
                "!w", "!warp" -> {
                    chat("Received warp command from $playername: $fullMessage")
                }
                "!allinv", "!allinvite" -> {
                    chat("Received all invite command from $playername: $fullMessage")
                }
                "!ptme", "!transfer" -> {
                    chat("Received party me command from $playername: $fullMessage")
                }
                "!demote" -> {
                    chat("Received demote command from $playername: $fullMessage")
                }
                "!promote" -> {
                    chat("Received promote command from $playername: $fullMessage")
                }
                "!c", "!carrot" -> {
                    chat("Received carrot command from $playername: $fullMessage")
                }
                "!time" -> {
                    chat("Received time command from $playername: $fullMessage")
                }
                "!tps" -> {
                    chat("Received TPS command from $playername: $fullMessage")
                }
                "!chim", "!chimera", "!chims", "!chimeras", "!book", "!books" -> {
                    chat("Received Chimera command from $playername: $fullMessage")
                }
                "!inqsls", "!inquisitorls", "!inquisls", "!lsinq", "!lsinqs", "!lsinquisitor", "!lsinquis" -> {
                    chat("Received Inquisitor command from $playername: $fullMessage")
                }
                "!inq", "!inqs", "!inquisitor", "!inquis" -> {
                    chat("Received Inquisitor command from $playername: $fullMessage")
                }
                "!burrows", "!burrow" -> {
                    chat("Received Burrows command from $playername: $fullMessage")
                }
                "!relic", "!relics" -> {
                    chat("Received Relic command from $playername: $fullMessage")
                }
                "!chimls", "!chimerals", "!bookls", "!lschim", "!lsbook", "!lootsharechim", "!lschimera" -> {
                    chat("Received Chimera LS command from $playername: $fullMessage")
                }
                "!sticks", "!stick" -> {
                    chat("Received Stick command from $playername: $fullMessage")
                }
                "!feathers", "!feather" -> {
                    chat("Received Feather command from $playername: $fullMessage")
                }
                "!coins", "!coin" -> {
                    chat("Received Coin command from $playername: $fullMessage")
                }
                "!mobs", "!mob" -> {
                    chat("Received Mob command from $playername: $fullMessage")
                }
                "!mf", "!magicfind" -> {
                    chat("Received Magic Find command from $playername: $fullMessage")
                }
                "!playtime" -> {
                    chat("Received Playtime command from $playername: $fullMessage")
                }
                "!profits", "!profit" -> {
                    chat("Received Profit command from $playername: $fullMessage")
                }
                "!stats", "!stat" -> {
                    chat("Received Stats command from $playername: $fullMessage")
                }
                "!totalstats", "!totalstat" -> {
                    chat("Received Total Stats command from $playername: $fullMessage")
                }

                "!since" -> {
                    val secondArg = messageParts.getOrNull(1)?.lowercase()
                    when (secondArg) {
                        "inqs", "inq", "inquis", "inquisitor" -> chat("Received Since Inquisitor command from $playername: $fullMessage")
                        "chim", "chims", "chimera", "chimeras", "book", "books" -> chat("Received Since Chimera command from $playername: $fullMessage")
                        else -> chat("Received generic Since command from $playername: $fullMessage")
                    }
                }
            }
        }
    }
}