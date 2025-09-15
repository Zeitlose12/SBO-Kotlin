package net.sbo.mod.guis

import gg.essential.universal.UScreen
import net.sbo.mod.SBOKotlin
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.guis.partyfinder.PartyFinderGUI
import net.sbo.mod.utils.chat.Chat
import net.sbo.mod.utils.events.EventBus
import net.sbo.mod.utils.events.Register
import net.sbo.mod.utils.game.World
import net.sbo.mod.utils.http.Http

object Main {
    private var partyFinderGui: PartyFinderGUI? = null
    internal var achievementsGui: AchievementsGUI? = null
    private var updating = false
    private var lastUpdate = 0L
    private const val UPDATE_INTERVAL = 300_000L // 5 minutes in ms

    fun register() {
        Register.command("sbopf") {
            if (!World.isInSkyblock()) {
                Chat.chat("§6[SBO] §cYou can only use this command in Skyblock.")
                return@command
            }
            mc.send {
                if (partyFinderGui == null) {
                    partyFinderGui = PartyFinderGUI()
                }
                UScreen.displayScreen(partyFinderGui!!)
                EventBus.emit("gui_opened")
            }
        }

        Register.command("sboachievements") {
            mc.send {
                if (achievementsGui == null) {
                    achievementsGui = AchievementsGUI()
                }
                UScreen.displayScreen(achievementsGui!!)
            }
        }

        Register.onTick(20) {
            val now = System.currentTimeMillis()
            if (now - lastUpdate > UPDATE_INTERVAL && !updating && World.isInSkyblock()) {
                lastUpdate = now
                updating = true
                countActivePlayers()
            }
        }
    }

    private fun countActivePlayers() {
        Http.sendGetRequest("https://api.skyblockoverhaul.com/countActiveUsers")
            .result { response ->
                if (!response.isSuccessful) {
                    SBOKotlin.logger.error("Failed to count active players: ${response.code} ${response.message}")
                }
                updating = false
            }
            .error { exception ->
                SBOKotlin.logger.error("Error while counting active players", exception)
                updating = false
            }
    }
}