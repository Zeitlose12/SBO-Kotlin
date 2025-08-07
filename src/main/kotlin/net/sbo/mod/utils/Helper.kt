package net.sbo.mod.utils

import kotlin.concurrent.thread
import net.sbo.mod.data.DianaItemsData
import net.sbo.mod.data.DianaMobsData

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

//    fun calcPercentWithMob(items: DianaItemsData, mobs: DianaMobsData, mobName: String): Double? {
//        val itemCount = items
//        val mobCount = when (mobName) {
//            "Minos Inquisitor" -> mobs.`Minos Inquisitor`
//            else -> return null
//        }
//        if (mobCount <= 0) return 0.0
//        return (itemCount.toDouble() / mobCount.toDouble() * 100)
//    }
//
//    fun calcPercentTotal(items: DianaItemsData, mobs: DianaMobsData, itemName: String): Double? {
//        val itemCount = when (itemName) {
//            "Chimera" -> items.Chimera
//            "Daedalus Stick" -> items.`Daedalus Stick`
//            else -> return null
//        }
//        val totalMobsCount = mobs.TotalMobs
//        if (totalMobsCount <= 0) return 0.0
//        return (itemCount.toDouble() / totalMobsCount.toDouble() * 100)
//    }
}