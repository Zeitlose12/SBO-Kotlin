package net.sbo.mod.mixins

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.sbo.mod.utils.Register
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MinecraftClient::class)
abstract class MinecraftClientMixin {

    @Inject(method = ["setScreen"], at = [At("HEAD")])
    fun onSetScreen(screen: Screen?, ci: CallbackInfo) {
        val client = MinecraftClient.getInstance()
        val oldScreen = client.currentScreen

        if (oldScreen == null && screen != null) {
            Register.runGuiOpenActions(client, screen)
        } else if (oldScreen != null && screen == null) {
            Register.runGuiCloseActions(client, oldScreen)
        }
    }
}