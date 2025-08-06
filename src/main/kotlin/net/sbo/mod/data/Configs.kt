package net.sbo.mod.data

data class SboConfigBundle(
    val sboData: SboData,
    val achievementsData: AchievementsData,
    val pastDianaEventsData: PastDianaEventsData,
    val dianaTrackerTotalData: DianaTrackerTotalData,
    val dianaTrackerSessionData: DianaTrackerSessionData,
    val dianaTrackerMayorData: DianaTrackerMayorData
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

data class DianaTrackerTotalData(
    val items: DianaItemsData = DianaItemsData(),
    val mobs: DianaMobsData = DianaMobsData(),
    val inquis: DianaInquisData = DianaInquisData()
)

data class DianaTrackerSessionData(
    val items: DianaItemsData = DianaItemsData(),
    val mobs: DianaMobsData = DianaMobsData(),
    val inquis: DianaInquisData = DianaInquisData()
)

data class DianaTrackerMayorData(
    val year: Int = 0,
    val items: DianaItemsData = DianaItemsData(),
    val mobs: DianaMobsData = DianaMobsData(),
    val inquis: DianaInquisData = DianaInquisData()
)

// ------ Diana Data ------
data class DianaItemsData(
    val coins: Long = 0,
    val `Griffin Feather`: Int = 0,
    val `Crown of Greed`: Int = 0,
    val `Washed-up Souvenir`: Int = 0,
    val Chimera: Int = 0,
    val ChimeraLs: Int = 0,
    val `Daedalus Stick`: Int = 0,
    val DWARF_TURTLE_SHELMET: Int = 0,
    val ANTIQUE_REMEDIES: Int = 0,
    val ENCHANTED_ANCIENT_CLAW: Int = 0,
    val ANCIENT_CLAW: Int = 0,
    val MINOS_RELIC: Int = 0,
    val ENCHANTED_GOLD: Int = 0,
    val ENCHANTED_IRON: Int = 0,
    val `Total Burrows`: Int = 0,
    val scavengerCoins: Long  = 0,
    val fishCoins: Long  = 0,
    val totalTime: Long  = 0,
    val sessionTime: Long  = 0,
    val mayorTime : Long  = 0,
)

data class DianaMobsData(
    val `Minos Inquisitor`: Int = 0,
    val `Minos Champion`: Int = 0,
    val Minotaur: Int = 0,
    val `Gaia Construct`: Int = 0,
    val `Siamese Lynxes`: Int = 0,
    val `Minos Hunter`: Int = 0,
    val TotalMobs: Int = 0,
    val `Minos Inquisitor Ls`: Int = 0
)

data class DianaInquisData(
    val DWARF_TURTLE_SHELMET: Int = 0,
    val CROCHET_TIGER_PLUSHIE: Int = 0,
    val ANTIQUE_REMEDIES: Int = 0,
    val DWARF_TURTLE_SHELMET_LS: Int = 0,
    val CROCHET_TIGER_PLUSHIE_LS: Int = 0,
    val ANTIQUE_REMEDIES_LS: Int = 0
)