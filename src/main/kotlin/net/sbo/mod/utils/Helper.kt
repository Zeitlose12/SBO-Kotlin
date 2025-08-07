package net.sbo.mod.utils

import kotlin.concurrent.thread
import net.sbo.mod.data.DianaItemsData
import net.sbo.mod.data.DianaMobsData
import kotlin.reflect.full.memberProperties
import java.text.DecimalFormat

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

    fun calcPercentOne(items: DianaItemsData, mobs: DianaMobsData, itemName: String, mobName: String? = null): Double? {
        if (mobName != null) {
            val itemCount = items::class.memberProperties.firstOrNull { it.name == itemName }
                ?.call(items) as? Int ?: 0
            val mobCount = mobs::class.memberProperties.firstOrNull { it.name == mobName }
                ?.call(mobs) as? Int ?: 0

            if (mobCount <= 0) return 0.0
            return (itemCount.toDouble() / mobCount.toDouble() * 100)
        } else {
            val mobCount = mobs::class.memberProperties.firstOrNull { it.name == itemName }
                ?.call(mobs) as? Int ?: 0
            val totalMobsCount = mobs.TotalMobs

            if (totalMobsCount <= 0) return 0.0
            return (mobCount.toDouble() / totalMobsCount.toDouble() * 100)
        }
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
}