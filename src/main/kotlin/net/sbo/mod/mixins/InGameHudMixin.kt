package net.sbo.mod.mixins

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.gui.DrawContext // <-- Wichtiger Import
import net.minecraft.client.render.RenderTickCounter // <-- Wichtiger Import
import net.sbo.mod.utils.Register
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(InGameHud::class)
abstract class InGameHudMixin {

    @Inject(method = ["render"], at = [At("HEAD")])
    private fun onRenderHud(context: DrawContext, tickCounter: RenderTickCounter, ci: CallbackInfo) {
        val client = MinecraftClient.getInstance()
            val tickDelta = tickCounter.getDynamicDeltaTicks()
            Register.runRenderOverlayActions(context, tickDelta)
    }
}