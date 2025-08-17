package net.sbo.mod.utils.overlay

import net.minecraft.client.gui.DrawContext
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.data.OverlayValues
import net.sbo.mod.utils.data.SboDataObject.overlayData
import java.awt.Color

class Overlay(
    var name: String,
    var x: Float,
    var y: Float,
    var scale: Float = 1.0f
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
    }

    fun setCondition(condition: () -> Boolean) {
        this.condition = condition
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

        val isOver = mouseX >= x && mouseX <= x + totalWidth && mouseY >= y && mouseY <= y + totalHeight

        return isOver
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
        }

        for (line in lines) {
            line.updateMouseInteraction(mouseX, mouseY, x , currentY*this.scale, textRenderer, this.scale, drawContext)

            line.draw(drawContext, currentX.toInt(), currentY.toInt(), textRenderer)
            currentY += textRenderer.fontHeight + 1
        }

        drawContext.matrices.pop()
    }

    private fun drawDebugBox(drawContext: DrawContext, x: Int, y: Int, width: Int, height: Int) {
        drawContext.drawBorder(x, y, width, height, Color(255, 0, 0, 170).rgb)
    }
}