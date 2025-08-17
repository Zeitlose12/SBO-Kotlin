package net.sbo.mod.utils.overlay

import net.minecraft.client.gui.DrawContext
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.data.OverlayValues
import net.sbo.mod.utils.data.SboDataObject.overlayData
import java.awt.Color

/**
 * Represents an overlay that can display text lines on the screen.
 * Overlays can be customized with position, scale, and render conditions.
 * They can also be clicked to trigger actions on the text lines.
 * @property name The name of the overlay.
 * @property x The x-coordinate of the overlay.
 * @property y The y-coordinate of the overlay.
 * @property scale The scale of the overlay, default is 1.0f.
 * @property renderType The type of rendering for the overlay, like "render", "postRender", "both".
 * @property allowedGuis The list of GUI names where the overlay is allowed to render.
 */
class Overlay(
    var name: String,
    var x: Float,
    var y: Float,
    var scale: Float = 1.0f,
    var renderType: String = "render",
    var allowedGuis: List<String> = listOf("Chat screen", "Crafting")
) {
    private val lines = mutableListOf<OverlayTextLine>()
    private var renderGui: Boolean = true
    private var condition: () -> Boolean = { true }

    var selected: Boolean = false

    init {
        if (overlayData.overlays.containsKey(name)) {
            val data = overlayData.overlays[name]!!
            x = data.x
            y = data.y
            scale = data.scale
        } else {
            overlayData.overlays[name] = OverlayValues(x, y, scale)
        }
        OverlayManager.overlays.add(this)
    }

    fun setCondition(condition: () -> Boolean): Overlay {
        this.condition = condition
        return this
    }

    fun addLine(line: OverlayTextLine) {
        lines.add(line)
    }

    fun addLines(newLines: List<OverlayTextLine>) {
        lines.addAll(newLines)
    }

    fun setLines(newLines: List<OverlayTextLine>) {
        lines.clear()
        lines.addAll(newLines)
    }

    fun removeLine(line: OverlayTextLine) {
        lines.remove(line)
    }

    fun clearLines() {
        lines.clear()
    }

    fun overlayClicked(mouseX: Double, mouseY: Double) {
        if (!renderGui) return
        if (!condition()) return
        if (Helper.getGuiName() !in allowedGuis) return
        val textRenderer = mc.textRenderer ?: return
        if (!isOverOverlay(mouseX, mouseY)) return
        var currentY = y/this.scale
        for (line in lines) {
            line.lineClicked(mouseX, mouseY, x, currentY * this.scale, textRenderer, this.scale)
            currentY += textRenderer.fontHeight + 1
        }
    }

    fun getTotalHeight(): Int {
        val textRenderer = mc.textRenderer ?: return 0
        return lines.sumOf { textRenderer.fontHeight + 1 }
    }

    fun getTotalWidth(): Int {
        val textRenderer = mc.textRenderer ?: return 0
        return lines.maxOfOrNull { textRenderer.getWidth(it.text) } ?: 0
    }

    fun isOverOverlay(mouseX: Double, mouseY: Double): Boolean {
        val textRenderer = mc.textRenderer ?: return false
        val totalWidth = getTotalWidth() * this.scale
        val totalHeight = getTotalHeight() * this.scale

        return mouseX >= x && mouseX <= x + totalWidth && mouseY >= y && mouseY <= y + totalHeight
    }

    fun render(drawContext: DrawContext, mouseX: Double, mouseY: Double) {
        if (!renderGui) return
        if (!condition()) return
        val textRenderer = mc.textRenderer ?: return

        drawContext.matrices.push()
        drawContext.matrices.scale(this.scale, this.scale, 1.0f)

        var currentY = (y / this.scale)
        val currentX = (x / this.scale)

        val totalWidth = getTotalWidth()
        val totalHeight = getTotalHeight()

        if (selected) {
            drawDebugBox(drawContext, currentX.toInt(), currentY.toInt(), totalWidth, totalHeight)
            drawContext.drawText(textRenderer, "X: ${x.toInt()} Y: ${y.toInt()} Scale: ${String.format("%.1f", scale)}", (currentX).toInt(), (currentY - textRenderer.fontHeight - 1).toInt(), Color(255, 255, 255, 200).rgb, true)
        }

        for (line in lines) {
            if (Helper.getGuiName() in allowedGuis) line.updateMouseInteraction(mouseX, mouseY, x , currentY*this.scale, textRenderer, this.scale, drawContext)

            line.draw(drawContext, currentX.toInt(), currentY.toInt(), textRenderer)
            currentY += textRenderer.fontHeight + 1
        }

        drawContext.matrices.pop()
    }

    private fun drawDebugBox(drawContext: DrawContext, x: Int, y: Int, width: Int, height: Int) {
        drawContext.drawBorder(x, y, width, height, Color(255, 0, 0, 170).rgb)
    }
}