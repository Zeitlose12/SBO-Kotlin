package net.sbo.mod.utils

import net.minecraft.item.ItemStack
import java.nio.FloatBuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Quaternionf
//#endif

/**
 * Utility methods for rendering using the modern DrawContext and MatrixStack.
 * This object is designed to be stateless and should receive the DrawContext instance as a parameter.
 */
object DrawContextUtils {
    private var _drawContext: DrawContext? = null
    private var renderDepth = 0

    val drawContext: DrawContext
        get() = _drawContext ?: run {
            throw IllegalStateException("drawContext is null")
        }

    fun drawItem(item: ItemStack, x: Int, y: Int) = drawContext.drawItem(item, x, y)

    fun setContext(context: DrawContext) {
        renderDepth++
        if (_drawContext != null) {
            return
        }
        _drawContext = context
    }

    fun clearContext() {
        if (renderDepth == 1) {
            _drawContext = null
            renderDepth = 0
        } else if (renderDepth > 1) {
            renderDepth--
        } else {
            throw IllegalStateException("renderDepth is less than 1, cannot clear context")
        }
    }

    fun translate(x: Double, y: Double, z: Double) {
        drawContext.matrices.translate(x, y, z)
    }

    fun translate(x: Float, y: Float, z: Float) {
        drawContext.matrices.translate(x, y, z)
    }

    fun translate(vec: Vec3d) {
        drawContext.matrices.translate(vec)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        val (xf, yf, zf) = listOf(x, y, z)
        drawContext.matrices.multiply(Quaternionf().rotationAxis(angle, xf, yf, zf))
    }

    fun multMatrix(matrix: Matrix4f) = drawContext.matrices.multiplyPositionMatrix(matrix)

    fun mulMatrix(buffer: FloatBuffer) {
        multMatrix(Matrix4f(buffer))
    }

    fun scale(x: Float, y: Float, z: Float) {
        drawContext.matrices.scale(x, y, z)
    }

    fun pushMatrix() {
        drawContext.matrices.push()
    }

    fun popMatrix() {
        drawContext.matrices.pop()
    }

    inline fun pushPop(action: () -> Unit) {
        pushMatrix()
        try {
            action()
        } finally {
            popMatrix()
        }
    }

    inline fun translated(x: Number = 0, y: Number = 0, z: Number = 0, action: () -> Unit) {
        pushMatrix()
        try {
            translate(x.toDouble(), y.toDouble(), z.toDouble())
            action()
        } finally {
            popMatrix()
        }
    }

    inline fun scaled(x: Number = 1, y: Number = 1, z: Number = 1, action: () -> Unit) {
        pushMatrix()
        try {
            scale(x.toFloat(), y.toFloat(), z.toFloat())
            action()
            scale(1 / x.toFloat(), 1 / y.toFloat(), 1 / z.toFloat())
        } finally {
            popMatrix()
        }
    }

    fun loadIdentity() {
        drawContext.matrices.loadIdentity()
    }
}