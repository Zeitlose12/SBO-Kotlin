package net.sbo.mod.guis

import gg.essential.universal.UScreen
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.guis.partyfinder.PartyFinderGUI
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.data.SboDataObject

object Main {
    fun register() {
        Register.command("sbopf") {
            mc.send {
                UScreen.displayScreen(PartyFinderGUI())
            }
        }
    }
}