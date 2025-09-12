package net.sbo.mod.utils.events.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext

class GuiPostRenderEvent(
    val client: MinecraftClient,
    val screen: Screen,
    val context: DrawContext,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
)