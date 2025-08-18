package net.sbo.mod.overlays

import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.overlay.Overlay
import net.sbo.mod.utils.overlay.OverlayTextLine
import net.minecraft.util.Formatting.*
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Helper.calcPercentOne
import net.sbo.mod.utils.Helper.removeFormatting
import net.sbo.mod.utils.data.DianaTracker
import net.sbo.mod.utils.data.SboDataObject.SBOConfigBundle
import net.sbo.mod.utils.events.Register
import net.sbo.mod.utils.render.RenderUtils2D
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

object DianaLoot {
    val overlay = Overlay("Diana Loot", 10f, 10f, 1f, listOf("Chat screen", "Crafting")).setCondition { Diana.lootTracker != Diana.Tracker.OFF }
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

    fun init() {
        overlay.init()
        updateLines()
        Register.onGuiOpen { screen, ci ->
            if (screen.title.string == "Crafting") {
                updateLines("CraftingOpen")
            }
        }
        Register.onGuiClose { screen ->
            if (screen.title.string == "Crafting") {
                overlay.removeLine(changeView)
            }
        }
    }

    fun createLine(name: String, formattedText: String) : OverlayTextLine {
        val line = OverlayTextLine(formattedText).onClick {
            if (SBOConfigBundle.sboData.hideTrackerLines.contains(name)) {
                SBOConfigBundle.sboData.hideTrackerLines.remove(name)
            } else {
                SBOConfigBundle.sboData.hideTrackerLines.add(name)
            }
            updateLines()
        }
            .setCondition { !(mc.currentScreen?.title?.string != "Crafting" && SBOConfigBundle.sboData.hideTrackerLines.contains(name)) }
        if (SBOConfigBundle.sboData.hideTrackerLines.contains(name)) {
            line.text = "$GRAY$STRIKETHROUGH${formattedText.removeFormatting()}"
        }
        return line
    }

    fun updateLines(screen: String = "") {
        val lines = mutableListOf<OverlayTextLine>()
        val type = Diana.lootTracker
        val totalEvents: Int = SBOConfigBundle.pastDianaEventsData.events.size
        val tracker = when (type) {
            Diana.Tracker.TOTAL -> SBOConfigBundle.dianaTrackerTotalData
            Diana.Tracker.EVENT -> SBOConfigBundle.dianaTrackerMayorData
            Diana.Tracker.SESSION -> SBOConfigBundle.dianaTrackerSessionData
            Diana.Tracker.OFF -> {
                overlay.setLines(emptyList())
                return
            }
        }

        val chimPercent = calcPercentOne(tracker.items, tracker.mobs, "CHIMERA", "MINOS_INQUISITOR")
        val chimLsPercent = calcPercentOne(tracker.items, tracker.mobs, "CHIMERA_LS", "MINOS_INQUISITOR_LS")
        val relicPercent = calcPercentOne(tracker.items, tracker.mobs, "MINOS_RELIC", "MINOS_CHAMPION")
        val stickPercent = calcPercentOne(tracker.items, tracker.mobs, "DAEDALUS_STICK", "MINOTAUR")
        val playTimeHrs = tracker.items.TIME.toDouble() / TimeUnit.HOURS.toMillis(1)
        val burrowsPerHr = if (tracker.items.TOTAL_BURROWS > 0) {
            BigDecimal(tracker.items.TOTAL_BURROWS.toDouble() / playTimeHrs).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else 0.0

        val chimPrice = Helper.getItemPriceFormatted("CHIMERA", tracker.items.CHIMERA)
        val chimLsPrice = Helper.getItemPriceFormatted("CHIMERA", tracker.items.CHIMERA_LS)
        val relicPrice = Helper.getItemPriceFormatted("MINOS_RELIC", tracker.items.MINOS_RELIC)
        val stickPrice = Helper.getItemPriceFormatted("DAEDALUS_STICK", tracker.items.DAEDALUS_STICK)
        val crownPrice = Helper.getItemPriceFormatted("CROWN_OF_GREED", tracker.items.CROWN_OF_GREED)
        val sovenirPrice = Helper.getItemPriceFormatted("WASHED_UP_SOUVENIR", tracker.items.WASHED_UP_SOUVENIR)
        val featherPrice = Helper.getItemPriceFormatted("GRIFFIN_FEATHER", tracker.items.GRIFFIN_FEATHER)
        val shelmetPrice = Helper.getItemPriceFormatted("DWARF_TURTLE_SHELMET", tracker.items.DWARF_TURTLE_SHELMET)
        val plushiePrice = Helper.getItemPriceFormatted("CROCHET_TIGER_PLUSHIE", tracker.items.CROCHET_TIGER_PLUSHIE)
        val remediesPrice = Helper.getItemPriceFormatted("ANTIQUE_REMEDIES", tracker.items.ANTIQUE_REMEDIES)
        val clawPrice= Helper.getItemPriceFormatted("ANCIENT_CLAW", tracker.items.ANCIENT_CLAW)
        val echClawPrice = Helper.getItemPriceFormatted("ENCHANTED_ANCIENT_CLAW", tracker.items.ENCHANTED_ANCIENT_CLAW)
        val echGoldPrice = Helper.getItemPriceFormatted("ENCHANTED_GOLD", tracker.items.ENCHANTED_GOLD)
        val echIronPrice = Helper.getItemPriceFormatted("ENCHANTED_IRON", tracker.items.ENCHANTED_IRON)
        val profitPerHr = if (playTimeHrs > 0) {
            BigDecimal(totalProfit(tracker) / playTimeHrs).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else 0.0
        val poriftPerBurrow = if (tracker.items.TOTAL_BURROWS > 0) {
            BigDecimal(totalProfit(tracker) / tracker.items.TOTAL_BURROWS).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else 0.0

        if (screen == "CraftingOpen" || mc.currentScreen?.title?.string == "Crafting") {
            lines.add(changeView)
        }

        lines.add(OverlayTextLine("$YELLOW${BOLD}Diana Loot $GRAY($YELLOW$type$GRAY)", linebreak = true))

        lines.addAll(
            listOf(
                createLine("CHIMERA", "$GOLD$chimPrice $GRAY|$LIGHT_PURPLE Chimera: $AQUA${Helper.formatNumber(tracker.items.CHIMERA, true)} $GRAY($AQUA${chimPercent}%$GRAY)"),
                createLine("CHIMERA_LS", "$GOLD$chimLsPrice $GRAY|$LIGHT_PURPLE Chimera $GRAY[${AQUA}LS$GRAY]: $AQUA${Helper.formatNumber(tracker.items.CHIMERA_LS, true)} $GRAY($AQUA${chimLsPercent}%$GRAY)"),
                createLine("MINOS_RELIC", "$GOLD$relicPrice $GRAY|$DARK_PURPLE Minos Relic: $AQUA${Helper.formatNumber(tracker.items.MINOS_RELIC, true)} $GRAY($AQUA${relicPercent}%$GRAY)"),
                createLine("DAEDALUS_STICK", "$GOLD$stickPrice $GRAY|$GOLD Daedalus Stick: $AQUA${Helper.formatNumber(tracker.items.DAEDALUS_STICK, true)} $GRAY($AQUA${stickPercent}%$GRAY)"),
                createLine("CROWN_OF_GREED", "$GOLD$crownPrice $GRAY|$GOLD Crown of Greed: $AQUA${Helper.formatNumber(tracker.items.CROWN_OF_GREED, true)}"),
                createLine("WASHED_UP_SOUVENIR", "$GOLD$sovenirPrice $GRAY|$GOLD Washed-up Souvenir: $AQUA${Helper.formatNumber(tracker.items.WASHED_UP_SOUVENIR, true)}"),
                createLine("GRIFFIN_FEATHER", "$GOLD$featherPrice $GRAY|$GOLD Griffin Feather: $AQUA${Helper.formatNumber(tracker.items.GRIFFIN_FEATHER, true)}"),
                createLine("DWARF_TURTLE_SHELMET", "$GOLD$shelmetPrice $GRAY|$DARK_GREEN Dwarf Turtle Helmet: $AQUA${Helper.formatNumber(tracker.items.DWARF_TURTLE_SHELMET, true)}"),
                createLine("CROCHET_TIGER_PLUSHIE", "$GOLD$plushiePrice $GRAY|$DARK_GREEN Crochet Tiger Plushie: $AQUA${Helper.formatNumber(tracker.items.CROCHET_TIGER_PLUSHIE, true)}"),
                createLine("ANTIQUE_REMEDIES", "$GOLD$remediesPrice $GRAY|$DARK_GREEN Antique Remedies: $AQUA${Helper.formatNumber(tracker.items.ANTIQUE_REMEDIES, true)}"),
                createLine("ANCIENT_CLAW", "$GOLD$clawPrice $GRAY|$BLUE Ancient Claw: $AQUA${Helper.formatNumber(tracker.items.ANCIENT_CLAW)}"),
                createLine("ENCHANTED_ANCIENT_CLAW", "$GOLD$echClawPrice $GRAY|$BLUE Enchanted Ancient Claw: $AQUA${Helper.formatNumber(tracker.items.ANCIENT_CLAW,)}"),
                createLine("ENCHANTED_GOLD", "$GOLD$echGoldPrice $GRAY|$BLUE Enchanted Gold: $AQUA${Helper.formatNumber(tracker.items.ENCHANTED_GOLD)}"),
                createLine("ENCHANTED_IRON", "$GOLD$echIronPrice $GRAY|$BLUE Enchanted Iron: $AQUA${Helper.formatNumber(tracker.items.ENCHANTED_IRON)}"),
                OverlayTextLine("${GRAY}Total Burrows: $AQUA${Helper.formatNumber(tracker.items.TOTAL_BURROWS, true)} $GRAY[$AQUA$burrowsPerHr$GRAY/${AQUA}hr$GRAY]"),
                OverlayTextLine("${GOLD}Total Coins: $AQUA${Helper.formatNumber(tracker.items.COINS)}")
                    .onHover { drawContext, textRenderer ->
                        val scaleFactor = mc.window.scaleFactor
                        val mouseX = mc.mouse.x / scaleFactor
                        val mouseY = mc.mouse.y / scaleFactor
                        RenderUtils2D.drawHoveringString(drawContext,
                                "$YELLOW${BOLD}Coin Break Down:\n" +
                                "${GOLD}Treasure: $AQUA${Helper.formatNumber(tracker.items.COINS - tracker.items.FISH_COINS - tracker.items.SCAVENGER_COINS)}\n" +
                                "${GOLD}Four-Eyed Fish: $AQUA${Helper.formatNumber(tracker.items.FISH_COINS)}\n" +
                                "${GOLD}Scavenger: $AQUA${Helper.formatNumber(tracker.items.SCAVENGER_COINS)}",
                            mouseX, mouseY, textRenderer, overlay.scale)
                    },
                OverlayTextLine("${YELLOW}Total Profit: $AQUA${Helper.formatNumber(totalProfit(tracker))} coins")
                    .onHover { drawContext, textRenderer ->
                        val scaleFactor = mc.window.scaleFactor
                        val mouseX = mc.mouse.x / scaleFactor
                        val mouseY = mc.mouse.y / scaleFactor
                        RenderUtils2D.drawHoveringString(drawContext,
                            "$GOLD$profitPerHr coins/hr\n" +
                                "$poriftPerBurrow coins/burrow",
                            mouseX, mouseY, textRenderer, overlay.scale)
                    },
                OverlayTextLine("${YELLOW}Playtime: $AQUA${Helper.formatTime(tracker.items.TIME)}"),
            )
        )
        if (type == Diana.Tracker.TOTAL) lines.add(OverlayTextLine("${YELLOW}Total Events: $AQUA$totalEvents"))
        overlay.setLines(lines)
    }

    fun totalProfit(tracker: DianaTracker): Long {
        var totalProfit = 0L
        for (item in tracker.items::class.java.declaredFields) {
            item.isAccessible = true
            val itemName = item.name
            if (itemName == "TIME" || itemName == "TOTAL_BURROWS" || itemName == "COINS" || itemName == "SCAVENGER_COINS" || itemName == "FISH_COINS" || itemName == "CHIMERA_LS") continue
            val itemValue = item.get(tracker.items) as Int
            if (itemValue <= 0) continue
            val itemPrice = Helper.getItemPrice(itemName)
            if (itemPrice > 0) {
                totalProfit += itemPrice * itemValue
            }
        }
        return totalProfit + tracker.items.COINS + Helper.getItemPrice("CHIMERA", tracker.items.CHIMERA_LS)
    }
}