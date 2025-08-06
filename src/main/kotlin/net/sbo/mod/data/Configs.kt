package net.sbo.mod.data

data class SboConfigBundle(
    val sboData: SboData,
    val achievementsData: AchievementsData,
    val pastDianaEventsData: PastDianaEventsData
)

// ------ Data Classes ------

data class Effect(
    val name: String,
    val duration: Double,
    val timeStamp: Long,
    val loggedOff: Boolean
)

data class DianaEvent(
    val year: Any, // Can be String or Int
    val items: Map<String, Any>,
    val mobs: Map<String, Any>,
    val inquis: Map<String, Any>? = null // Optional, can be null if not applicable
)

// ------ Main Data Class ------

data class SboData(
    val effects: List<Effect> = emptyList(),
    val resetVersion: String = "0.1.3",
    val changelogVersion: String = "1.0.0",
    val downloadMsg: Boolean = true,
    val mobsSinceInq: Int = 0,
    val inqsSinceChim: Int = 0,
    val minotaursSinceStick: Int = 0,
    val champsSinceRelic: Int = 0,
    val inqsSinceLsChim: Int = 0,
    val trackerMigration: Boolean = true,
    val trackerMigration2: Boolean = true,
    val highestChimMagicFind: Int = 0,
    val highestStickMagicFind: Int = 0,
    val hideTrackerLines: List<String> = emptyList(),
    val partyBlacklist: List<String> = emptyList(),
    val crownTimer: Long = 0,
    val totalCrownCoins: Long = 0,
    val lastCrownCoins: Long = 0,
    val totalCrownCoinsGained: Long = 0,
    val totalCrownCoinsSession: Long = 0,
    val cronwTimerSession: Long = 0,
    val ghostKills: Int = 0,
    val sorrowDrops: Int = 0,
    val crownOneMilCoins: Int = 0,
    val achievementFilter: String = "Locked",
    val achievementFix1: Boolean = true,
    val dianaStatsUpdated: Long = 0,
    val lastInqDate: Long = 0,
    val b2bStick: Boolean = false,
    val b2bChim: Boolean = false,
    val b2bChimLs: Boolean = false,
    val b2bInq: Boolean = false,
    val avgChimMagicFind: Int = 0,
    val avgStickMagicFind: Int = 0,
    val last10ChimMagicFind: List<Int> = emptyList(),
    val last10StickMagicFind: List<Int> = emptyList(),
    val backTrack: Boolean = true,
    val version: String = "0.1.3",
    val b2bChimLsInq: Boolean = false
)

data class AchievementsData(
    val unlocked: List<Int> = emptyList()
)

data class PastDianaEventsData(
    val events: List<DianaEvent> = emptyList()
)