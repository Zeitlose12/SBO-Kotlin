package net.sbo.mod.guis.partyfinder.pages

import net.sbo.mod.guis.partyfinder.PartyFinderGUI
import net.sbo.mod.guis.partyfinder.PartyInfo
import net.sbo.mod.utils.Helper

class CustomPage(private val parent: PartyFinderGUI) {
    internal fun getPartyInfo(info: PartyInfo): String {
        var formattedInfoString = ""
        val formattedInfo = listOf(
            Pair("&9Name: &b", info.name),
            Pair("&9Skyblock Level: ", Helper.matchLvlToColor(info.sblvl)),
            Pair("&9Uuid: &7", info.uuid),
            Pair("&9Eman9: ", Helper.getNumberColor(info.emanLvl, 9)),
            Pair("&9Clover: ", if (info.clover) "&a✔" else "&c✘"),
            Pair("&9Magical Power: &b", info.magicalPower),
            Pair("&9Enrichments: &b", info.enrichments),
            Pair("&9Missing Enrichments: &b", info.missingEnrichments),
            Pair("&9Warnings: &7", info.warnings.joinToString(", "))
        )
        formattedInfo.forEach { (key, value) ->
            formattedInfoString += "$key$value\n\n"
        }
        return formattedInfoString
    }

    internal fun getReqsString(reqs: List<*>): String {
        return ""
    }
}