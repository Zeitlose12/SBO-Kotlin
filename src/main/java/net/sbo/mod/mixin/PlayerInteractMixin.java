package net.sbo.mod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.sbo.mod.utils.data.PlayerInteractEvent;
import net.sbo.mod.utils.Register;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class PlayerInteractMixin {

    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void onInteractItem(CallbackInfoReturnable<ActionResult> cir) {
        if (client.player != null) {
            PlayerInteractEvent event = new PlayerInteractEvent(client.player, client.player.getWorld(), false);
            if (Register.INSTANCE.runPlayerInteractActions("use", null, event)) {
                cir.setReturnValue(ActionResult.FAIL);
                cir.cancel();
            }
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (hand == Hand.MAIN_HAND) {
            PlayerInteractEvent event = new PlayerInteractEvent(player, player.getWorld(), false);
            if (Register.INSTANCE.runPlayerInteractActions("use", hitResult.getBlockPos(), event)) {
                cir.setReturnValue(ActionResult.FAIL);
                cir.cancel();
            }
        }
    }
}