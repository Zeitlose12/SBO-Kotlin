package net.sbo.mod.diana

import net.sbo.mod.SBOKotlin
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.data.DianaTracker
import java.util.concurrent.TimeUnit
import net.sbo.mod.settings.categories.Diana
import java.util.regex.Pattern
import java.util.Locale

data class PlayerStats(
    val playtime: String,
    val profit: List<String>,
    val burrows: String,
    val burrowsPerHour: String,
    val totalMobs: String,
    val mobsPerHour: String,
    val inquisitors: Int,
    val inqPercentage: String,
    val lsInqs: String,
    val chimeraDrops: Int,
    val chimeraDropRate: String,
    val chimeraLSDrops: Int,
    val chimeraLSDropRate: String,
    val sticksDropped: Int,
    val stickDropRate: String,
    val relicsDropped: Int,
    val relicDropRate: String
)

object DianaStats {
    val STATS_PATTERN = Pattern.compile(
        "^§r§9Party §8> (.*?)§f: §rPlaytime: (.*?) - Profit: (.*?) - (.*?) - Burrows: (.*?) \\((.*?)/h\\) - Mobs: (.*?) \\((.*?)/h\\) - Inquisitors: (.*?) \\((.*?)\\) - LS Inqs: (.*?) - Chimeras: (.*?) \\((.*?)\\) - LS: (.*?) \\((.*?)\\) - Sticks: (.*?) (.*?) - Relics: (.*?) (.*?)§r$"
    )
    // todo: replace send stats message with formatted one and send it with Chat.chat

    fun getPlayerStats(total: Boolean = false): PlayerStats {
        val tracker: DianaTracker = if (total) SBOKotlin.SBOConfigBundle.dianaTrackerTotalData else SBOKotlin.SBOConfigBundle.dianaTrackerMayorData

        val playtime = if (total) tracker.items.totalTime else tracker.items.mayorTime
        val playTimeHrs = playtime.toDouble() / TimeUnit.HOURS.toMillis(1)

        val burrowsPerHour = if (playTimeHrs > 0) tracker.items.`Total Burrows`.toDouble() / playTimeHrs else 0.0
        val mobsPerHour = if (playTimeHrs > 0) tracker.mobs.TotalMobs.toDouble() / playTimeHrs else 0.0

        val totalValue = 0.0 // todo: getTotalValue(tracker)
        val profit = listOf(
            Helper.formatNumber(totalValue),
            Diana.bazaarSettingDiana.toString(),
            Helper.formatNumber(totalValue / playTimeHrs)
        )
        val stats = PlayerStats(
            playtime = Helper.formatTime(playtime),
            profit = profit,
            burrows = Helper.formatNumber(tracker.items.`Total Burrows`),
            burrowsPerHour = "%.2f".format(Locale.US, burrowsPerHour),
            totalMobs = Helper.formatNumber(tracker.mobs.TotalMobs),
            mobsPerHour = "%.2f".format(Locale.US, mobsPerHour),
            inquisitors = tracker.mobs.`Minos Inquisitor`,
            inqPercentage = "${Helper.calcPercentOne(tracker.items, tracker.mobs, "Minos Inquisitor")}%",
            lsInqs = Helper.formatNumber(tracker.mobs.`Minos Inquisitor Ls`, withCommas = true),
            chimeraDrops = tracker.items.Chimera,
            chimeraDropRate = "${Helper.calcPercentOne(tracker.items, tracker.mobs, "Chimera", "Minos Inquisitor")}%",
            chimeraLSDrops = tracker.items.ChimeraLs,
            chimeraLSDropRate = "${"%.2f".format(Locale.US, if (tracker.mobs.`Minos Inquisitor Ls` > 0) tracker.items.ChimeraLs.toDouble() / tracker.mobs.`Minos Inquisitor Ls`.toDouble() * 100.0 else 0.0)}%",
            sticksDropped = tracker.items.`Daedalus Stick`,
            stickDropRate = "${Helper.calcPercentOne(tracker.items, tracker.mobs, "Daedalus Stick", "Minotaur")}%",
            relicsDropped = tracker.items.MINOS_RELIC,
            relicDropRate = "${Helper.calcPercentOne(tracker.items, tracker.mobs, "MINOS_RELIC", "Minos Champion")}%"
        )
        return stats
    }

    fun sendPlayerStats(total: Boolean = false) {
        val stats = getPlayerStats(total)
        val statsMessage = buildString {
            append("Playtime: ${stats.playtime} - ")
            append("Profit: ${stats.profit[0]} (${stats.profit[2]}/h) - ")
            append("Burrows: ${stats.burrows} (${stats.burrowsPerHour}/h)")
            append(" - Mobs: ${stats.totalMobs} (${stats.mobsPerHour}/h) - ")
            append("Inqs: ${stats.inquisitors} (${stats.inqPercentage}) - ")
            append("LS Inqs: ${stats.lsInqs} - ")
            append("Chims: ${stats.chimeraDrops} (${stats.chimeraDropRate}) - LS: ${stats.chimeraLSDrops} (${stats.chimeraLSDropRate}) - ")
            append("Sticks: ${stats.sticksDropped} (${stats.stickDropRate}) - ")
            append("Relics: ${stats.relicsDropped} (${stats.relicDropRate})")
        }

        Chat.command("pc $statsMessage")
    }
}