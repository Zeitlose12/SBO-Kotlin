package net.sbo.mod.overlays

import net.minecraft.util.Formatting
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.overlay.Overlay
import net.sbo.mod.utils.overlay.OverlayTextLine
import net.minecraft.util.Formatting.*
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Helper.calcPercentOne
import net.sbo.mod.utils.data.DianaTracker
import net.sbo.mod.utils.data.PastDianaEventsData
import net.sbo.mod.utils.data.SboConfigBundle
import net.sbo.mod.utils.data.SboDataObject.SBOConfigBundle
import net.sbo.mod.utils.events.Register
import net.sbo.mod.utils.render.RenderUtils2D
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

object DianaLoot {
    val overlay = Overlay("Diana Loot", 10f, 10f, 1f).setCondition { Diana.lootTracker != Diana.Tracker.OFF }
    val changeView: OverlayTextLine = OverlayTextLine("$YELLOW${BOLD}Change View")
        .onClick {
            Diana.lootTracker = Diana.lootTracker.next()
            updateLines()
        }
        .onMouseEnter {
            changeView.text = "$YELLOW$UNDERLINE${BOLD}Change View"
        }
        .onMouseLeave {
            changeView.text = "$YELLOW${BOLD}Change View"
        }


    fun updateLines() {
        val lines = mutableListOf<OverlayTextLine>()
        val type = Diana.lootTracker
        val totalEvents: Int = SBOConfigBundle.pastDianaEventsData.events.size
        val tracker = when (type) {
            Diana.Tracker.TOTAL -> SBOConfigBundle.dianaTrackerTotalData
            Diana.Tracker.EVENT -> SBOConfigBundle.dianaTrackerMayorData
            Diana.Tracker.SESSION -> SBOConfigBundle.dianaTrackerSessionData
            Diana.Tracker.OFF -> return
        }

        val chimPercent = calcPercentOne(tracker.items, tracker.mobs, "CHIMERA_BOOK", "MINOS_INQUISITOR")
        val chimLsPercent = calcPercentOne(tracker.items, tracker.mobs, "CHIMERA_BOOK_LS", "MINOS_INQUISITOR_LS")
        val relicPercent = calcPercentOne(tracker.items, tracker.mobs, "RELIC", "MINOS_CHAMPION")
        val stickPercent = calcPercentOne(tracker.items, tracker.mobs, "DAEDALUS_STICK", "MINOTAUR")
        val playTimeHrs = tracker.items.TIME / TimeUnit.HOURS.toMillis(1)
        val burrowsPerHr = if (playTimeHrs > 0) {
            val result = tracker.mobs.TOTAL_MOBS.toDouble() / playTimeHrs
            BigDecimal(result).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else 0.0

        val chimPrice = Helper.getItemPrice("CHIMERA", tracker.items.CHIMERA)
        val chimLsPrice = Helper.getItemPrice("CHIMERA", tracker.items.CHIMERA_LS)
        val relicPrice = Helper.getItemPrice("MINOS_RELIC", tracker.items.MINOS_RELIC)
        val stickPrice = Helper.getItemPrice("DAEDALUS_STICK", tracker.items.DAEDALUS_STICK)
        val crownPrice = Helper.getItemPrice("CROWN_OF_GREED", tracker.items.CROWN_OF_GREED)
        val sovenirPrice = Helper.getItemPrice("WASHED_UP_SOUVENIR", tracker.items.WASHED_UP_SOUVENIR)
        val featherPrice = Helper.getItemPrice("GRIFFIN_FEATHER", tracker.items.GRIFFIN_FEATHER)
        val shelmetPrice = Helper.getItemPrice("DWARF_TURTLE_SHELMET", tracker.items.DWARF_TURTLE_SHELMET)
        val plushiePrice = Helper.getItemPrice("CROCHET_TIGER_PLUSHIE", tracker.items.CROCHET_TIGER_PLUSHIE)
        val remediesPrice = Helper.getItemPrice("ANTIQUE_REMEDIES", tracker.items.ANTIQUE_REMEDIES)
        val clawPrice= Helper.getItemPrice("ANCIENT_CLAW", tracker.items.ANCIENT_CLAW)
        val echClawPrice = Helper.getItemPrice("ENCHANTED_ANCIENT_CLAW", tracker.items.ENCHANTED_ANCIENT_CLAW)
        val echGoldPrice = Helper.getItemPrice("ENCHANTED_GOLD", tracker.items.ENCHANTED_GOLD)
        val echIronPrice = Helper.getItemPrice("ENCHANTED_IRON", tracker.items.ENCHANTED_IRON)

        val totalCoins = tracker.items.COINS + tracker.items.SCAVENGER_COINS + tracker.items.FISH_COINS
        val totalProfit = chimPrice + chimLsPrice + relicPrice + stickPrice + crownPrice +
                sovenirPrice + featherPrice + shelmetPrice + plushiePrice + remediesPrice +
                clawPrice + echClawPrice + echGoldPrice + echIronPrice + totalCoins
        val profitPerHr = if (playTimeHrs > 0) {
            BigDecimal(totalProfit.toDouble() / playTimeHrs).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else 0.0
        val poriftPerBurrow = if (tracker.items.TOTAL_BURROWS > 0) {
            BigDecimal(totalProfit.toDouble() / tracker.items.TOTAL_BURROWS).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else 0.0

        lines.addAll(
            listOf(
                OverlayTextLine("$YELLOW${BOLD}Diana Loot $GRAY($YELLOW$type$GRAY)"),
                OverlayTextLine("$GOLD$chimPrice $GRAY|$LIGHT_PURPLE Chimera: $AQUA${Helper.formatNumber(tracker.items.CHIMERA, true)} $GRAY($AQUA${chimPercent}%$GRAY)"),
                OverlayTextLine("$GOLD$chimLsPrice $GRAY|$LIGHT_PURPLE Chimera $GRAY[$AQUA LS$GRAY]: $AQUA${Helper.formatNumber(tracker.items.CHIMERA_LS, true)} $GRAY($AQUA${chimLsPercent}%$GRAY)"),
                OverlayTextLine("$GOLD$relicPrice $GRAY|$DARK_PURPLE Minos Relic: $AQUA${Helper.formatNumber(tracker.items.MINOS_RELIC, true)} $GRAY($AQUA${relicPercent}%$GRAY)"),
                OverlayTextLine("$GOLD$stickPrice $GRAY|$GOLD Daedalus Stick: $AQUA${Helper.formatNumber(tracker.items.DAEDALUS_STICK, true)} $GRAY($AQUA${stickPercent}%$GRAY)"),
                OverlayTextLine("$GOLD$crownPrice $GRAY|$GOLD Crown of Greed: $AQUA${Helper.formatNumber(tracker.items.CROWN_OF_GREED, true)}"),
                OverlayTextLine("$GOLD$sovenirPrice $GRAY|$GOLD Washed-up Souvenir: $AQUA${Helper.formatNumber(tracker.items.WASHED_UP_SOUVENIR, true)}"),
                OverlayTextLine("$GOLD$featherPrice $GRAY|$GOLD Griffin Feather: $AQUA${Helper.formatNumber(tracker.items.GRIFFIN_FEATHER, true)}"),
                OverlayTextLine("$GOLD$shelmetPrice $GRAY|$DARK_GREEN Dwarf Turtle Helmet: $AQUA${Helper.formatNumber(tracker.items.DWARF_TURTLE_SHELMET, true)}"),
                OverlayTextLine("$GOLD$plushiePrice $GRAY|$DARK_GREEN Crochet Tiger Plushie: $AQUA${Helper.formatNumber(tracker.items.CROCHET_TIGER_PLUSHIE, true)}"),
                OverlayTextLine("$GOLD$remediesPrice $GRAY|$DARK_GREEN Antique Remedies: $AQUA${Helper.formatNumber(tracker.items.ANTIQUE_REMEDIES, true)}"),
                OverlayTextLine("$GOLD$clawPrice $GRAY|$BLUE Ancient Claw: $AQUA${Helper.formatNumber(tracker.items.ANCIENT_CLAW)}"),
                OverlayTextLine("$GOLD$echClawPrice $GRAY|$BLUE Enchanted Ancient Claw: $AQUA${Helper.formatNumber(tracker.items.ANCIENT_CLAW,)}"),
                OverlayTextLine("$GOLD$echGoldPrice $GRAY|$BLUE Enchanted Gold: $AQUA${Helper.formatNumber(tracker.items.ENCHANTED_GOLD)}"),
                OverlayTextLine("$GOLD$echIronPrice $GRAY|$BLUE Enchanted Iron: $AQUA${Helper.formatNumber(tracker.items.ENCHANTED_IRON)}"),
                OverlayTextLine("${GRAY}Total Burrows: $AQUA${Helper.formatNumber(tracker.items.TOTAL_BURROWS, true)} $GRAY[$AQUA$burrowsPerHr$GRAY/${AQUA}hr$GRAY]"),
                OverlayTextLine("${GOLD}Total Coins: $AQUA${Helper.formatNumber(totalCoins)}")
                    .onHover { drawContext, textRenderer ->
                        val scaleFactor = mc.window.scaleFactor
                        val mouseX = mc.mouse.x / scaleFactor
                        val mouseY = mc.mouse.y / scaleFactor
                        RenderUtils2D.drawHoveringString(drawContext,
                                "$YELLOW${BOLD}Coin Break Down:\n" +
                                "${GOLD}Treasure: $AQUA${Helper.formatNumber(tracker.items.COINS)}\n" +
                                "${GOLD}Four-Eyed Fish: $AQUA${Helper.formatNumber(tracker.items.FISH_COINS)}\n" +
                                "${GOLD}Scavenger: $AQUA${Helper.formatNumber(tracker.items.SCAVENGER_COINS)}",
                            mouseX, mouseY, textRenderer, overlay.scale)
                    },
                OverlayTextLine("${YELLOW}Total Profit: $AQUA${Helper.formatNumber(totalProfit)}")
                    .onHover { drawContext, textRenderer ->
                        val scaleFactor = mc.window.scaleFactor
                        val mouseX = mc.mouse.x / scaleFactor
                        val mouseY = mc.mouse.y / scaleFactor
                        RenderUtils2D.drawHoveringString(drawContext,
                            "$GOLD$profitPerHr coins/hr\n $poriftPerBurrow coins/burrow",
                            mouseX, mouseY, textRenderer, overlay.scale)
                    },
                OverlayTextLine("${YELLOW}Playtime: $AQUA${Helper.formatTime(tracker.items.TIME)}"),
            )
        )
        if (type == Diana.Tracker.TOTAL) lines.add(OverlayTextLine("${YELLOW}Total Events: $AQUA$totalEvents"))
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
                DianaMobs.updateLines()
                overlay.removeLine(changeView)
            }
        }
    }
}