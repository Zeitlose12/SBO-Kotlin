package net.sbo.mod.partyfinder

import net.sbo.mod.SBOKotlin.API_URL
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.data.PartyInfoByUuids
import net.sbo.mod.utils.data.PartyPlayerStats
import net.sbo.mod.utils.http.Http

object PartyPlayer {
    var stats: PartyPlayerStats = PartyPlayerStats()
    var lastUpdate: Long = 0
    var cooldown: Long = 0

    fun init() {
        getPartyPlayerStats(true) { stats ->
            Chat.chat("§6[SBO] §aParty player stats initialized: ${stats.name} (SB Level: ${stats.sbLvl})")
        }

        Register.command("sboreloadstats") {
            // if cooldown is 2 min
            if (System.currentTimeMillis() - cooldown < 2 * 60 * 1000) {
                Chat.chat("§6[SBO] §cPlease wait before reloading stats again.")
                return@command
            } else {
                cooldown = System.currentTimeMillis()
                getPartyPlayerStats(true) { stats ->
                    Chat.chat("§6[SBO] §aParty player stats reloaded: ${stats.name} (SB Level: ${stats.sbLvl})")
                }
            }
        }
    }

    fun getPartyPlayerStats(forceRefresh: Boolean = false, callback: (PartyPlayerStats) -> Unit) {
        if (forceRefresh || System.currentTimeMillis() - lastUpdate > 10 * 60 * 1000) { // 10 minutes
            Http.sendGetRequest("$API_URL/partyInfoByUuids?uuids=${Player.getUUIDString().replace("-", "")}&readcache=false")
                .toJson<PartyInfoByUuids> { response ->
                    if (response.success) {
                        lastUpdate = System.currentTimeMillis()
                        stats = response.partyInfo.firstOrNull() ?: PartyPlayerStats()
                        if (stats.sbLvl == -1) {
                            Chat.chat("§6[SBO] §cYour stats are not available, please try again later.")
                        }
                        callback(stats)
                    }
                }
                .error { error ->
                    println("[SBO] Failed to fetch party player stats: $error")
                    callback(stats)
                }

        } else {
            callback(stats)
            return
        }
    }
}