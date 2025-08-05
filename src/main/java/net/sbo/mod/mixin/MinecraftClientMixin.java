package net.sbo.mod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.sbo.mod.utils.Register;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void onSetScreen(Screen screen, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen oldScreen = client.currentScreen;

        if (oldScreen == null && screen != null) {
            Register.INSTANCE.runGuiOpenActions(client, screen);
        } else if (oldScreen != null && screen == null) {
            Register.INSTANCE.runGuiCloseActions(client, oldScreen);
        }
    }
}