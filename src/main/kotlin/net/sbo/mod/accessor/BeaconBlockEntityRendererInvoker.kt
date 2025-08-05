package net.sbo.mod.accessor

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker

/**
 * Ein Mixin-Interface, das einen Invoker für die private 'renderBeam'
 * Methode des BeaconBlockEntityRenderer bereitstellt.
 *
 * Die Verwendung eines Interfaces mit einer statischen `@Invoker` Methode
 * (durch das companion object mit @JvmStatic) ist eine spezielle Mixin-Syntax,
 * die für diese Art von Aufruf funktioniert.
 */
@Mixin(BeaconBlockEntityRenderer::class)
interface BeaconBlockEntityRendererInvoker {

    companion object {
        /**
         * `@JvmStatic` macht die Methode statisch, was für das `@Invoker`-Muster
         * innerhalb eines Interfaces erforderlich ist.
         * Der Methodenrumpf mit `throw UnsupportedOperationException()` ist ein
         * erforderlicher Platzhalter für den Kotlin-Compiler. Der Mixin-Prozessor
         * wird diesen Code zur Laufzeit durch den tatsächlichen Aufruf ersetzen.
         */
        @JvmStatic
        @Invoker("renderBeam")
        fun invokeRenderBeam(
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