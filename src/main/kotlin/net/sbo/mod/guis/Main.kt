package net.sbo.mod.guis

import gg.essential.universal.UScreen
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.guis.partyfinder.PartyFinderGUI
import net.sbo.mod.utils.Register

object Main {
    fun register() {
        Register.command("sbopf") {
            mc.send {
                UScreen.displayScreen(PartyFinderGUI())
            }
        }
    }
}