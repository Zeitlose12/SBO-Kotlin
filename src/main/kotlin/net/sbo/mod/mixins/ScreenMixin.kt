package net.sbo.mod.mixins

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.sbo.mod.utils.Register
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Screen::class)
abstract class ScreenMixin {

    @Inject(method = ["render"], at = [At("HEAD")])
    fun onRender(ci: CallbackInfo) {
        val client = MinecraftClient.getInstance()
        val currentScreen = client.currentScreen
        if (currentScreen != null) {
            Register.runGuiRenderActions(client, currentScreen)
        }
    }

    @Inject(method = ["render"], at = [At("RETURN")])
    fun onPostRender(ci: CallbackInfo) {
        val client = MinecraftClient.getInstance()
        val currentScreen = client.currentScreen
        if (currentScreen != null) {
            Register.runGuiPostRenderActions(client, currentScreen)
        }
    }

    @Inject(method = ["keyPressed"], at = [At("HEAD")], cancellable = true)
    fun onKeyPressed(keyCode: Int, scanCode: Int, modifiers: Int, cir: CallbackInfoReturnable<Boolean>) {
        val client = MinecraftClient.getInstance()
        val currentScreen = client.currentScreen
        if (currentScreen != null) {
            Register.runGuiKeyActions(client, currentScreen, keyCode)
        }
    }
}