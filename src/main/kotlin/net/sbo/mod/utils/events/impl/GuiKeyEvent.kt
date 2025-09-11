package net.sbo.mod.utils.events.impl

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class GuiKeyEvent(
    val client: MinecraftClient,
    val screen: Screen,
    val key: Int,
    val cir: CallbackInfoReturnable<Boolean>
)