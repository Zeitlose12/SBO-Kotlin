package net.sbo.mod.utils

import kotlin.concurrent.thread
import net.sbo.mod.utils.data.DianaItemsData
import net.sbo.mod.utils.data.DianaMobsData
import kotlin.reflect.full.memberProperties
import java.text.DecimalFormat
import java.util.Locale

object Helper {
    /**
     * Sleeps for the specified number of milliseconds and then executes the callback.
     * This is done in a separate thread to avoid blocking the main thread.
     *
     * @param milliseconds The number of milliseconds to sleep.
     * @param callback The function to execute after sleeping.
     */
    fun sleep(milliseconds: Long, callback: () -> Unit) {
        thread(isDaemon = true) {
            Thread.sleep(milliseconds)
            callback()
        }
    }

    fun getPlayerName(player: String): String {
        var name = player
        val num = name.indexOf(']')
        if (num != -1) {
            name = name.substring(num + 2)
        }
        name = name.replace(Regex("§[0-9a-fk-or]"), "")
        name = name.replace(Regex("[^a-zA-Z0-9_]"), "")
        return name.trim()
    }

    fun calcPercentOne(items: DianaItemsData, mobs: DianaMobsData, itemName: String, mobName: String? = null): String {
        val result: Double = if (mobName != null) {
            val itemCount = items::class.memberProperties.firstOrNull { it.name == itemName }
                ?.call(items) as? Int ?: 0
            val mobCount = mobs::class.memberProperties.firstOrNull { it.name == mobName }
                ?.call(mobs) as? Int ?: 0

            if (mobCount <= 0) 0.0
            else (itemCount.toDouble() / mobCount.toDouble() * 100)
        } else {
            val mobCount = mobs::class.memberProperties.firstOrNull { it.name == itemName }
                ?.call(mobs) as? Int ?: 0
            val totalMobsCount = mobs.TotalMobs

            if (totalMobsCount <= 0) 0.0
            else (mobCount.toDouble() / totalMobsCount.toDouble() * 100)
        }
        return "%.2f".format(Locale.US, result)
    }

    fun formatNumber(number: Number?, withCommas: Boolean = false): String {
        val num = number?.toDouble() ?: 0.0

        if (withCommas) {
            // Format with commas
            val formatter = DecimalFormat("#,###")
            return formatter.format(num)
        } else {
            // Format with suffixes (k, m, b)
            return when {
                num >= 1_000_000_000 -> "%.2fb".format(num / 1_000_000_000)
                num >= 1_000_000 -> "%.1fm".format(num / 1_000_000)
                num >= 1_000 -> "%.1fk".format(num / 1_000)
                else -> "%.0f".format(num)
            }
        }
    }

    fun formatTime(milliseconds: Long): String {
        if (milliseconds <= 0) {
            return "0s"
        }

        val totalSeconds = (milliseconds / 1000).toInt()
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val days = totalHours / 24
        val hours = totalHours % 24
        val minutes = totalMinutes % 60
        val seconds = totalSeconds % 60

        val builder = StringBuilder()

        if (days > 0) {
            builder.append("${days}d ")
        }
        if (hours > 0 || days > 0) {
            builder.append("${hours}h ")
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            builder.append("${minutes}m ")
        }
        if (builder.isEmpty()) {
            builder.append("${seconds}s")
        }

        return builder.toString().trim()
    }

    fun String.removeFormatting(): String {
        return this.replace(Regex("§."), "")
    }

    fun matchLvlToColor(lvl: Int): String {
        return when {
            lvl >= 480 -> "§4$lvl"
            lvl >= 440 -> "§c$lvl"
            lvl >= 400 -> "§6$lvl"
            lvl >= 360 -> "§5$lvl"
            lvl >= 320 -> "§d$lvl"
            lvl >= 280 -> "§9$lvl"
            lvl >= 240 -> "§3$lvl"
            lvl >= 200 -> "§b$lvl"
            else -> "§7$lvl"
        }
    }

    fun getNumberColor(number: Int, range: Int): String {
        return when (number) {
            range -> "§c$number"
            range - 1 -> "§6$number"
            else -> "§9$number"
        }
    }

    fun getGriffinItemColor(item: String): String {
        if (item.isEmpty()) return ""
        val name = item.replace("PET_ITEM_", "").replace("_", " ").replaceFirstChar { it.uppercase() }
        return when (name) {
            "Four Eyed Fish" -> "§5$name"
            "Dwarf Turtle Shelmet" -> "§a$name"
            "Crochet Tiger Plushie" -> "§5$name"
            "Antique Remedies" -> "§5$name"
            "Lucky Clover" -> "§a$name"
            "Minos Relic" -> "§5$name"
            else -> "§7$name"
        }
    }

    fun getRarity(item: String): String {
        return when (item.lowercase().trim()) {
            "common" -> "§f$item"
            "uncommon" -> "§a$item"
            "rare" -> "§9$item"
            "epic" -> "§5$item"
            "legendary" -> "§6$item"
            "mythic" -> "§d$item"
            else -> "§7$item"
        }
    }

    fun matchDianaKillsToColor(kills: Int): String {
        return when {
            kills >= 200_000 -> "§6${formatNumber(kills, true)}"
            kills >= 150_000 -> "§e${formatNumber(kills, true)}"
            kills >= 100_000 -> "§c${formatNumber(kills, true)}"
            kills >= 75_000 -> "§d${formatNumber(kills, true)}"
            kills >= 50_000 -> "§9${formatNumber(kills, true)}"
            kills >= 25_000 -> "§a${formatNumber(kills, true)}"
            kills >= 10_000 -> "§2${formatNumber(kills, true)}"
            else -> "§7${formatNumber(kills, true)}"
        }
    }

    fun getPurse(): Long {
        val lines = ScoreBoard.getLines()
        if (lines.isEmpty()) return 0L
        val purseLine = lines.find { it.contains("Purse: ") }
        return if (purseLine != null) {
            val purseValue = purseLine.substringAfter("Purse: ")
            val numericValue = purseValue.split(" ")[0]
            numericValue.replace(",", "").toLongOrNull() ?: 0L
        } else {
            0L
        }
    }
}