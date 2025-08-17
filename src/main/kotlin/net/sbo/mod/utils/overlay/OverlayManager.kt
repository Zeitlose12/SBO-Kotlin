package net.sbo.mod.utils.overlay

import net.minecraft.util.Identifier
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.overlays.Bobber
import net.sbo.mod.overlays.Legion
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.render.RenderUtils2D
import java.awt.Color

object OverlayManager {
    val overlays = mutableListOf<Overlay>()

    fun init() {
        val textline = OverlayTextLine("Enjoy using SBO-Kotlin!")
        textline.onHover { drawContext, textRenderer ->
            val scaleFactor = mc.window.scaleFactor
            val mouseX = mc.mouse.x / (scaleFactor * 2)
            val mouseY = mc.mouse.y / (scaleFactor * 2)
            RenderUtils2D.drawHoveringString(drawContext, "this is hovered text", mouseX, mouseY, textRenderer)
        }
        val testOverlay = Overlay("test1",50.0f, 10.0f, 2.0f).apply {
            addLine(OverlayTextLine("Hello, this is a test overlay!"))
            addLine(OverlayTextLine("You can add more lines."))
            addLine(OverlayTextLine("This is a simple overlay example."))
            addLine(OverlayTextLine("You can customize the text, position, and more."))
            addLine(OverlayTextLine("§3§lEnjoy using SBO-Kotlin!"))
            addLine(OverlayTextLine("§r§1§mEnjoy using SBO-Kotlin!"))
            addLine(OverlayTextLine("§3§nEnjoy using SBO-Kotlin!"))
            addLine(OverlayTextLine("§nEnjoy using SBO-Kotlin!"))
            addLine(OverlayTextLine("§n§lEnjoy using SBO-Kotlin!"))
            addLine(textline)
        }
        testOverlay.setCondition {
            Diana.dianaBurrowGuess
        }
        val testOverlay2 = Overlay("test2", 50.0f, 100.0f, 1.0f).apply {
            addLine(OverlayTextLine("This is another overlay!"))
            addLine(OverlayTextLine("You can have multiple overlays."))
            addLine(OverlayTextLine("Each overlay can have its own lines."))
        }
        add(testOverlay)
//        add(testOverlay2)
        registerRenderer()
        registerMouseLeftClick()

        Register.command("sboguis", "sbomoveguis", "sbomove") {
            mc.send {
                mc.setScreen(OverlayEditScreen())
            }
        }
    }

    fun add(overlay: Overlay) {
        if (!overlays.contains(overlay)) {
            overlays.add(overlay)
        }
    }

    fun render(drawContext: DrawContext) {
        val scaleFactor = mc.window.scaleFactor
        val mouseX = mc.mouse.x / scaleFactor
        val mouseY = mc.mouse.y / scaleFactor
        for (overlay in overlays.toList()) {
            overlay.render(drawContext, mouseX, mouseY)
        }
    }

    fun registerRenderer() {
        HudLayerRegistrationCallback.EVENT.register(HudLayerRegistrationCallback { layeredDrawer ->
            layeredDrawer.attachLayerAfter(IdentifiedLayer.MISC_OVERLAYS, Identifier.of("sbo-kotlin", "overlay_renderer")) { context, tickCounter ->
                if (mc.currentScreen is OverlayEditScreen) return@attachLayerAfter
                render(context)
            }
        })
    }

    fun registerMouseLeftClick() {
        ScreenEvents.AFTER_INIT.register { client, screen, scaledWidth, scaledHeight ->
            ScreenMouseEvents.afterMouseClick(screen).register { clickedScreen, mouseX, mouseY, button ->
                if (clickedScreen !is OverlayEditScreen && button == 0) {
                    for (overlay in overlays) {
                        overlay.overlayClicked(mouseX, mouseY)
                    }
                }
            }
        }
    }
}