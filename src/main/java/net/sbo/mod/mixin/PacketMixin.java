package net.sbo.mod.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.sbo.mod.utils.events.Register;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class PacketMixin {
    // recived S2C packets
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        Register.INSTANCE.runPacketReceivedActions(packet);
    }

    // sended C2S packets
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        Register.INSTANCE.runPacketSentActions(packet);
    }
}