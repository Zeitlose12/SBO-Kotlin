package net.sbo.mod.data

interface DianaTracker {
    val items: DianaItemsData
    val mobs: DianaMobsData
    val inquis: DianaInquisData
}

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
    val year: Any,
    val items: Map<String, Any>,
    val mobs: Map<String, Any>,
    val inquis: Map<String, Any>? = null
)

// ------ Main Data Class ------

data class SboData(
    var effects: List<Effect> = emptyList(),
    var resetVersion: String = "0.1.3",
    var changelogVersion: String = "1.0.0",
    var downloadMsg: Boolean = true,
    var mobsSinceInq: Int = 0,
    var inqsSinceChim: Int = 0,
    var minotaursSinceStick: Int = 0,
    var champsSinceRelic: Int = 0,
    var inqsSinceLsChim: Int = 0,
    var trackerMigration: Boolean = true,
    var trackerMigration2: Boolean = true,
    var highestChimMagicFind: Int = 0,
    var highestStickMagicFind: Int = 0,
    var hideTrackerLines: List<String> = emptyList(),
    var partyBlacklist: List<String> = emptyList(),
    var crownTimer: Long = 0,
    var totalCrownCoins: Long = 0,
    var lastCrownCoins: Long = 0,
    var totalCrownCoinsGained: Long = 0,
    var totalCrownCoinsSession: Long = 0,
    var cronwTimerSession: Long = 0,
    var ghostKills: Int = 0,
    var sorrowDrops: Int = 0,
    var crownOneMilCoins: Int = 0,
    var achievementFilter: String = "Locked",
    var achievementFix1: Boolean = true,
    var dianaStatsUpdated: Long = 0,
    var lastInqDate: Long = 0,
    var b2bStick: Boolean = false,
    var b2bChim: Boolean = false,
    var b2bChimLs: Boolean = false,
    var b2bInq: Boolean = false,
    var avgChimMagicFind: Int = 0,
    var avgStickMagicFind: Int = 0,
    var last10ChimMagicFind: List<Int> = emptyList(),
    var last10StickMagicFind: List<Int> = emptyList(),
    var backTrack: Boolean = true,
    var version: String = "0.1.3",
    var b2bChimLsInq: Boolean = false,
    var sboKey: String = ""
)

data class AchievementsData(
    var achievements: Map<String, Boolean> = emptyMap(),
    var unlocked: List<Int> = emptyList()
)

data class PastDianaEventsData(
    var events: List<DianaEvent> = emptyList()
)

// Korrigierte Datenklassen mit 'override'
data class DianaTrackerTotalData(
    override var items: DianaItemsData = DianaItemsData(),
    override var mobs: DianaMobsData = DianaMobsData(),
    override var inquis: DianaInquisData = DianaInquisData()
) : DianaTracker

data class DianaTrackerSessionData(
    override var items: DianaItemsData = DianaItemsData(),
    override var mobs: DianaMobsData = DianaMobsData(),
    override var inquis: DianaInquisData = DianaInquisData()
) : DianaTracker

data class DianaTrackerMayorData(
    val year: Int = 0,
    override var items: DianaItemsData = DianaItemsData(),
    override var mobs: DianaMobsData = DianaMobsData(),
    override var inquis: DianaInquisData = DianaInquisData()
) : DianaTracker

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