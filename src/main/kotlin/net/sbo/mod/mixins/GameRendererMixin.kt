package net.sbo.mod.mixins

import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderTickCounter
import net.sbo.mod.utils.Register
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(GameRenderer::class)
abstract class GameRendererMixin {
    @Inject(method = ["render"], at = [At("HEAD")])
    private fun onRenderWorld(tickCounter: RenderTickCounter, renderLevel: Boolean, ci: CallbackInfo) {
        val tickDelta = tickCounter.getDynamicDeltaTicks()
        Register.runRenderWorldActions(tickDelta)
    }
}