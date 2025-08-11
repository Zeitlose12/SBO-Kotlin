package net.sbo.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import net.sbo.mod.event.ChatMessageEventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public class MixinMessageHandler {
    @Inject(method = "onGameMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
    public void onChat(CallbackInfo ci, @Local(argsOnly = true) Text message) {
        ChatMessageEventManager.INSTANCE.trigger(message);
    }
}
