package net.sbo.mod.guis.partyfinder

import gg.essential.elementa.UIComponent

data class HighlightElement(
    val page: String,
    val obj: UIComponent,
    val type: String
)

data class Party(
    val leaderName: String,
    val reqs: List<*>,
    val note: String,
    val partymembers: Int,
    val partySize: Int,
    val partyInfo: List<PartyInfo>
)

data class PartyInfo(
    val name: String,
    val sblvl: Int,
    val uuid: String,
    val emanLvl: Int,
    val clover: Boolean,
    val daxeLootingLvl: Int,
    val daxeChimLvl: Int,
    val griffinItem: String,
    val griffinRarity: String,
    val mythosKills: Long,
    val killLeaderboard: Int,
    val magicalPower: Int,
    val enrichments: Int,
    val missingEnrichments: Int,
    val warnings: List<String>
)
