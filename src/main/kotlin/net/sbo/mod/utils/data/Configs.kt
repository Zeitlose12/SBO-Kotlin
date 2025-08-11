package net.sbo.mod.utils.data

interface DianaTracker {
    var items: DianaItemsData
    var mobs: DianaMobsData
    var inquis: DianaInquisData
}

data class SboConfigBundle(
    var sboData: SboData,
    var achievementsData: AchievementsData,
    var pastDianaEventsData: PastDianaEventsData,
    var dianaTrackerTotalData: DianaTrackerTotalData,
    var dianaTrackerSessionData: DianaTrackerSessionData,
    var dianaTrackerMayorData: DianaTrackerMayorData,
    var partyFinderConfigState: PartyFinderConfigState,
    var partyFinderData: PartyFinderData
)

// ------ Data Classes ------

data class Effect(
    var name: String,
    var duration: Double,
    var timeStamp: Long,
    var loggedOff: Boolean
)

data class DianaEvent(
    var year: Any,
    var items: Map<String, Any>,
    var mobs: Map<String, Any>,
    var inquis: Map<String, Any>? = null
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
    var year: Int = 0,
    override var items: DianaItemsData = DianaItemsData(),
    override var mobs: DianaMobsData = DianaMobsData(),
    override var inquis: DianaInquisData = DianaInquisData()
) : DianaTracker

data class PartyFinderConfigState(
    var checkboxes: Checkboxes = Checkboxes(),
    var inputs: Inputs = Inputs(),
    var filters: Filters = Filters()
)

data class PartyFinderData(
    var playerStatsUpdated: Long = 0,
    var sboKey: String = "",
    var playerStats: Map<String, PlayerStats> = emptyMap(),
)

// ------ Diana Data ------
data class DianaItemsData(
    var coins: Long = 0,
    var `Griffin Feather`: Int = 0,
    var `Crown of Greed`: Int = 0,
    var `Washed-up Souvenir`: Int = 0,
    var Chimera: Int = 0,
    var ChimeraLs: Int = 0,
    var `Daedalus Stick`: Int = 0,
    var DWARF_TURTLE_SHELMET: Int = 0,
    var ANTIQUE_REMEDIES: Int = 0,
    var ENCHANTED_ANCIENT_CLAW: Int = 0,
    var ANCIENT_CLAW: Int = 0,
    var MINOS_RELIC: Int = 0,
    var ENCHANTED_GOLD: Int = 0,
    var ENCHANTED_IRON: Int = 0,
    var `Total Burrows`: Int = 0,
    var scavengerCoins: Long  = 0,
    var fishCoins: Long  = 0,
    var totalTime: Long  = 0,
    var sessionTime: Long  = 0,
    var mayorTime : Long  = 0,
)

data class DianaMobsData(
    var `Minos Inquisitor`: Int = 0,
    var `Minos Champion`: Int = 0,
    var Minotaur: Int = 0,
    var `Gaia Construct`: Int = 0,
    var `Siamese Lynxes`: Int = 0,
    var `Minos Hunter`: Int = 0,
    var TotalMobs: Int = 0,
    var `Minos Inquisitor Ls`: Int = 0
)

data class DianaInquisData(
    var DWARF_TURTLE_SHELMET: Int = 0,
    var CROCHET_TIGER_PLUSHIE: Int = 0,
    var ANTIQUE_REMEDIES: Int = 0,
    var DWARF_TURTLE_SHELMET_LS: Int = 0,
    var CROCHET_TIGER_PLUSHIE_LS: Int = 0,
    var ANTIQUE_REMEDIES_LS: Int = 0
)

// ------ Party Finder ------

data class Checkboxes(
    var custom: CustomCheckboxes = CustomCheckboxes(),
    var diana: DianaCheckboxes = DianaCheckboxes()
)

data class CustomCheckboxes(
    var eman9: Boolean = false
)

data class DianaCheckboxes(
    var eman9: Boolean = false,
    var looting5: Boolean = false
)

data class Inputs(
    var custom: CustomInputs = CustomInputs(),
    var diana: DianaInputs = DianaInputs()
)

data class CustomInputs(
    var lvl: Int = 0,
    var mp: Int = 0,
    var partySize: Int = 0,
    var note: String = "..."
)

data class DianaInputs(
    var kills: Int = 0,
    var lvl: Int = 0,
    var note: String = "..."
)

data class Filters(
    var custom: CustomFilters = CustomFilters(),
    var diana: DianaFilters = DianaFilters()
)

data class CustomFilters(
    var eman9Filter: Boolean = false,
    var noteFilter: String = "."
)

data class DianaFilters(
    var eman9Filter: Boolean = false,
    var looting5Filter: Boolean = false,
    var canIjoinFilter: Boolean = false
)

data class PlayerStats(
    var name: String = "",
    var sbLvl: Int = 0,
    var eman9: Boolean = false,
    var looting5daxe: Boolean = false,
    var emanLvl: Int = 0,
    var warnings: List<String> = emptyList(),
    var uuid: String = "",
    var clover: Boolean = false,
    var daxeLootingLvl: Int = 0,
    var daxeChimLvl: Int = 0,
    var invApi: Boolean = false,
    var magicalPower: Int = 0,
    var enrichments: Int = 0,
    var missingEnrichments: Int = 0,
    var griffinRarity: String = "",
    var griffinItem: String? = null,
    var killLeaderboard: Int = 0,
    var mythosKills: Long = 0L
)