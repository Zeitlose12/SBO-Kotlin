package net.sbo.mod.guis.partyfinder

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.universal.UKeyboard
import net.sbo.mod.utils.EventBus
import net.sbo.mod.SBOKotlin.mc
import java.awt.Color

class PartyFinderGUI : WindowScreen(ElementaVersion.V10) {

    var openGui: Boolean = false
    val elementToHighlight: MutableList<Any> = mutableListOf()
    var selectedPage: String = "Home"
    val pages: MutableMap<String, () -> Unit> = mutableMapOf()
    val partyCache: MutableMap<String, Any> = mutableMapOf()
    var lastRefreshTime: Long = 0L
    var cpWindowOpened: Boolean = false
    var filterWindowOpened: Boolean = false
    var partyInfoOpened: Boolean = false
    var dequeued: Boolean = false


    init {
        _registers()
        _create()

        EventBus.on("refreshPartyList") {
            updateCurrentPartyList(true)
        }

        window.onKeyType { typedChar, keyCode ->
            if (keyCode == UKeyboard.KEY_ESCAPE) {
                mc.send {
                    displayScreen(null)
                }
            }
        }
    }

    private fun _registers() {

    }

    private fun _create() {
        var base = UIRoundedRectangle(10f).constrain {
            width = 60.percent()
            height = 65.percent()
            x = CenterConstraint()
            y = CenterConstraint()
        }.setColor(Color(30, 30, 30, 240)) childOf window
    }


    private fun updateCurrentPartyList(ignoreCooldown: Boolean) {

    }
}