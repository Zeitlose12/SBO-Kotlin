package net.sbo.mod.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.sbo.mod.utils.Register;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderWorld(RenderTickCounter tickCounter, boolean renderLevel, CallbackInfo ci) {
        float tickDelta = tickCounter.getDynamicDeltaTicks();
        Register.INSTANCE.runRenderWorldActions(tickDelta);
    }
}