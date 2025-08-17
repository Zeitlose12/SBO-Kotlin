package net.sbo.mod.utils.render

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

object RenderUtils2D {
    /**
     * Draws a string with a background, positioned at the mouse coordinates.
     * This is useful for creating tooltips.
     *
     * @param drawContext The DrawContext instance for rendering.
     * @param text The string to be drawn.
     * @param x The x-coordinate of the mouse.
     * @param y The y-coordinate of the mouse.
     * @param textRenderer The TextRenderer instance for getting text dimensions.
     * @param padding The padding around the text inside the background box.
     */
    fun drawHoveringString(
        drawContext: DrawContext,
        text: String,
        x: Double,
        y: Double,
        textRenderer: TextRenderer,
        padding: Int = 2
    ) {
        if (text.isEmpty()) return

        val textWidth = textRenderer.getWidth(text)
        val textHeight = textRenderer.fontHeight

        val rectX1 = x.toInt() - padding
        val rectY1 = y.toInt() - padding

        val rectX2 = rectX1 + textWidth + padding * 2
        val rectY2 = rectY1 + textHeight + padding * 2

        val backgroundColor = 0x80404040.toInt() // Semi-transparent gray
        val borderColor = 0x80202020.toInt() // Semi-transparent dark gray
        drawContext.matrices.push()
        drawContext.matrices.translate(0.0, 0.0, 400.0)
        // Draw the background rectangle.
        drawContext.fill(rectX1, rectY1, rectX2, rectY2, backgroundColor)

        drawContext.fill(rectX1, rectY1, rectX2, rectY1 + 1, borderColor) // Top border
        drawContext.fill(rectX1, rectY2 - 1, rectX2, rectY2, borderColor) // Bottom border
        drawContext.fill(rectX1, rectY1, rectX1 + 1, rectY2, borderColor) // Left border
        drawContext.fill(rectX2 - 1, rectY1, rectX2, rectY2, borderColor) // Right border

        drawContext.drawText(textRenderer, text, x.toInt(), y.toInt(), 0xFFFFFF, true)
        drawContext.matrices.pop()
    }
}