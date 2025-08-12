package net.sbo.mod.utils.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAllParties(
    @SerialName("Success")
    val success: Boolean,

    @SerialName("Parties")
    val parties: List<Party>
)

@Serializable
data class PartyInfoByUuids(
    @SerialName("Success")
    val success: Boolean,

    @SerialName("PartyInfo")
    val partyInfo: List<PartyPlayerStats>
)

@Serializable
data class Party(
    @SerialName("partyinfo")
    val partyInfo: List<PartyPlayerStats>,
    val reqs: Reqs,
    val leader: String,
    @SerialName("partymembers")
    val partyMembersCount: Int,
    val leaderName: String,
    val note: String,
    val partySize: Int
)


@Serializable
data class PartyPlayerStats(
    val name: String = "",
    val sbLvl: Int = -1,
    val eman9: Boolean = false,
    val looting5daxe: Boolean = false,
    val emanLvl: Int = 0,
    val warnings: List<String> = emptyList(),
    val uuid: String = "",
    val clover: Boolean = false,
    val daxeLootingLvl: Int = 0,
    val daxeChimLvl: Int = 0,
    val invApi: Boolean = false,
    val magicalPower: Int = 0,
    val enrichments: Int = 0,
    val missingEnrichments: Int = 0,
    val griffinRarity: String = "",
    val griffinItem: String = "",
    val killLeaderboard: Int = 999999,
    val mythosKills: Int = 0
)

@Serializable
data class Reqs(
    val lvl: Int,
    val kills: Int,
    val eman9: Boolean,
    val looting5: Boolean,
    val mp: Int
)