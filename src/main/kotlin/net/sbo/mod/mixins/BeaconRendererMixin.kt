package net.sbo.mod.mixins

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

@Mixin(BeaconBlockEntityRenderer::class)
abstract class BeaconRendererMixin {
    companion object {
        @JvmStatic
        @Invoker("renderBeam")
        fun renderBeam(
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            tickDelta: Float,
            scale: Float,
            worldTime: Long,
            yOffset: Int,
            maxY: Int,
            color: Int
        ) {
            throw UnsupportedOperationException()
        }
    }
}