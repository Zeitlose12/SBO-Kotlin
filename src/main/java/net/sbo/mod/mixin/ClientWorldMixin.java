package net.sbo.mod.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.sbo.mod.utils.events.Register;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void onRemoveEntity(int entityId, Entity.RemovalReason reason, CallbackInfo ci) {
        ClientWorld world = (ClientWorld) (Object) this;
        Entity entity = world.getEntityById(entityId);

        if (entity != null) {
//            System.out.println("Entity removed: " + entity + " Reason: " + reason);
            Register.INSTANCE.runEntityDeathActions(entity);
        }
    }
}