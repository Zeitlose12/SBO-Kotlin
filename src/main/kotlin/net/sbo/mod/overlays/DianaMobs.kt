package net.sbo.mod.overlays

import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.overlay.Overlay
import net.sbo.mod.utils.overlay.OverlayTextLine
import net.minecraft.util.Formatting.*
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Helper.calcPercentOne
import net.sbo.mod.utils.data.SboDataObject.SBOConfigBundle
import net.sbo.mod.utils.events.Register

object DianaMobs {
    val overlay = Overlay("Diana Mobs", 10f, 10f, 1f).setCondition { Diana.mobTracker != Diana.Tracker.OFF }
    val changeView: OverlayTextLine = OverlayTextLine("$YELLOW${BOLD}Change View")
        .onClick {
            Diana.mobTracker = Diana.mobTracker.next()
            updateLines()
        }
        .onMouseEnter {
            changeView.text = "$UNDERLINE$YELLOW${BOLD}Change View"
        }
        .onMouseLeave {
            changeView.text = "$YELLOW${BOLD}Change View"
        }

    fun updateLines() {
        val lines = mutableListOf<OverlayTextLine>()
        val type = Diana.mobTracker
        val tracker = when (type) {
            Diana.Tracker.TOTAL -> SBOConfigBundle.dianaTrackerTotalData
            Diana.Tracker.EVENT -> SBOConfigBundle.dianaTrackerMayorData
            Diana.Tracker.SESSION -> SBOConfigBundle.dianaTrackerSessionData
            Diana.Tracker.OFF -> return
        }
        val inqPercent = calcPercentOne(tracker.items, tracker.mobs, "MINOS_INQUISITOR")

        if (mc.currentScreen?.title?.string == "Crafting") {
            lines.add(changeView)
        }

        lines.addAll(
            listOf(
                OverlayTextLine("$YELLOW${BOLD}Diana Mobs $GRAY($YELLOW$type$GRAY)"),
                OverlayTextLine("$GRAY - ${LIGHT_PURPLE}Inquisitor: $AQUA${Helper.formatNumber(tracker.mobs.MINOS_INQUISITOR, true)} $GRAY($AQUA${inqPercent}%$GRAY) [${AQUA}LS$GRAY:$AQUA${Helper.formatNumber(tracker.mobs.MINOS_INQUISITOR_LS, true)}$GRAY]")
            )
        )
        overlay.setLines(lines)
    }

    fun init() {
        updateLines()
        Register.onGuiOpen { screen, ci ->
            if (screen.title.string == "Crafting") {
                overlay.addLineAt(0, changeView)
            }
        }
        Register.onGuiClose { screen ->
            if (screen.title.string == "Crafting") {
                updateLines()
                overlay.removeLine(changeView)
            }
        }
    }
}