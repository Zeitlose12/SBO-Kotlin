package net.sbo.mod.guis.partyfinder

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.PositionConstraint
import gg.essential.elementa.constraints.SizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.effects.Effect
import java.awt.Color

object GuiHandler {

    fun addHoverEffect(
        uiObject: UIComponent,
        baseColor: Color,
        hoverColor: Color = Color(50,50,50,200)
    ) {
        uiObject.onMouseEnter {
            uiObject.setColor(hoverColor)
        }.onMouseLeave {
            uiObject.setColor(baseColor)
        }
    }

    fun addTextHoverEffect(
        textObject: UIComponent,
        baseColor: Color,
        hoverColor: Color = Color(50,50,50,200)
    ) {
        textObject.onMouseEnter {
            textObject.setColor(hoverColor)
        }.onMouseLeave {
            textObject.setColor(baseColor)
        }
    }

    class UILine(
        private val x: PositionConstraint,
        private val y: PositionConstraint,
        private val width: SizeConstraint,
        private val height: SizeConstraint,
        private val color: Color,
        private val parent: UIComponent? = null,
        private val rounded: Boolean = false,
        private val roundness: Float = 5f
    ) {
        val uiObject: UIComponent = if (rounded) UIRoundedRectangle(roundness) else UIBlock()

        fun get(): UIComponent = uiObject

        init {
            uiObject.constrain {
                this.x = this@UILine.x
                this.y = this@UILine.y
                this.width = this@UILine.width
                this.height = this@UILine.height
            }.setColor(color)

            parent?.let { uiObject.childOf(it) }
        }
    }

    class Button(
        private val text: String,
        private val x: PositionConstraint,
        private val y: PositionConstraint,
        private val width: SizeConstraint,
        private val height: SizeConstraint,
        private val color: Color,
        private val textColor: Color? = null,
        private val outline: Effect? = null,
        private val parent: UIComponent? = null,
        private val rounded: Boolean = false,
        private val wrapped: Boolean = false,
    ) {
        val uiObject: UIComponent = if (rounded) UIRoundedRectangle(10f) else UIBlock()
        val textObject: UIComponent = if (wrapped) UIWrappedText(text) else UIText(text)

        fun get(): UIComponent = uiObject

        fun hoverEffect(baseColor: Color = color, hoverColor: Color = Color(50, 50, 50, 200)): Button {
            addHoverEffect(uiObject, baseColor, hoverColor)
            return this
        }

        fun textHoverEffect(
            baseColor: Color = textColor ?: Color.WHITE,
            hoverColor: Color = Color(50, 50, 50, 200),
            comp: UIComponent = textObject
        ): Button {
            comp.onMouseEnter { textObject.setColor(hoverColor) }
                .onMouseLeave { textObject.setColor(baseColor) }
            return this
        }

        fun setOnClick(action: () -> Unit): Button {
            uiObject.onMouseClick { event ->
                event.stopPropagation()
                action()
            }
            return this
        }

        fun setTextOnClick(action: () -> Unit): Button {
            textObject.onMouseClick { event ->
                event.stopPropagation()
                action()
            }
            return this
        }

        init {
            uiObject.constrain {
                this.x = this@Button.x
                this.y = this@Button.y
                this.width = this@Button.width
                this.height = this@Button.height
            }.setColor(color)

            outline?.let { uiObject.enableEffect(it) }
            parent?.let { uiObject.childOf(it) }
            textObject.constrain {
                this.x = CenterConstraint()
                this.y = CenterConstraint()
            }.childOf(uiObject)

            textColor?.let { textObject.setColor(it) }
        }
    }

}