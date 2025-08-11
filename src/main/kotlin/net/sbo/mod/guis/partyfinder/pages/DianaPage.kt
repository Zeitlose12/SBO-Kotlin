package net.sbo.mod.guis.partyfinder.pages

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import net.sbo.mod.guis.partyfinder.PartyFinderGUI
import net.sbo.mod.guis.partyfinder.PartyInfo
import net.sbo.mod.utils.Helper
import java.awt.Color

class DianaPage(private val parent: PartyFinderGUI) {
    internal fun getPartyInfo(info: PartyInfo): String {
        var formattedInfoString = ""
        val formattedInfo = listOf(
            Pair("&9Name: &b", info.name),
            Pair("&9Skyblock Level: ", Helper.matchLvlToColor(info.sblvl)),
            Pair("&9Uuid: &7", info.uuid),
            Pair("&9Eman9: ", Helper.getNumberColor(info.emanLvl, 9)),
            Pair("&9Clover: ", if (info.clover) "&a✔" else "&c✘"),
            Pair("&9Looting 5: ", Helper.getNumberColor(info.daxeLootingLvl, 5)),
            Pair("&9Chimera: ", Helper.getNumberColor(info.daxeChimLvl, 5)),
            Pair("&9Griffin Item: ", Helper.getGriffinItemColor(info.griffinItem)),
            Pair("&9Griffin Rarity: ", Helper.getRarity(info.griffinRarity)),
            Pair("&9Diana Kills: ", Helper.matchDianaKillsToColor(info.mythosKills)),
            Pair("&9Leaderboard: &b#", info.killLeaderboard),
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

    internal fun render() {

    }

    private fun createParty() {
        parent.openCpWindow()
        parent.cpWindow.setWidth(20.percent())
        parent.cpWindow.setHeight(40.percent())
        parent.reqsBox = UIBlock().constrain {
            x = 0.percent()
            y = SiblingConstraint()
            width = 100.percent()
            height = 68.percent()
        }.setColor(Color(0, 0, 0, 0)) childOf parent.cpWindow
        val lvlbox = UIBlock().constrain {
            x = 0.percent()
            y = 5.percent()
            width = 100.percent()
            height = 23.percent()
        }.setColor(Color(0, 0, 0, 0)) childOf parent.reqsBox
        val lvlText = UIText("SbLvL").constrain {
            x = 5.percent()
            y = SiblingConstraint(5f)
            textScale = parent.getTextScale()
        }.setColor(Color(255, 255, 255, 255)) childOf lvlbox
    }
}