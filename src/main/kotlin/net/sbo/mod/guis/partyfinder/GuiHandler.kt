package net.sbo.mod.guis.partyfinder

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.PositionConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.SizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.effects.Effect
import net.sbo.mod.utils.data.SboDataObject
import java.awt.Color
import net.sbo.mod.utils.data.SboDataObject.pfConfigState
import java.awt.List

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

    class Checkbox(
        private val list: String,
        private val key: String,
        private val x: PositionConstraint,
        private val y: PositionConstraint,
        private val width: SizeConstraint,
        private val height: SizeConstraint,
        private val color: Color,
        private val checkedColor: Color,
        private val text: String = "",
        private val rounded: Boolean = false,
        private val roundness: Float = 10f,
        private val filter: Boolean = false
    ) {
        private lateinit var onClick: () -> Unit

        var checked: Boolean = if (filter) {
            when (list) {
                "diana" -> when (key) {
                    "eman9Filter" -> pfConfigState.filters.diana.eman9Filter
                    "looting5Filter" -> pfConfigState.filters.diana.looting5Filter
                    "canIjoinFilter" -> pfConfigState.filters.diana.canIjoinFilter
                    else -> false // Default case if key is not found
                }
                "custom" -> when (key) {
                    "eman9Filter" -> pfConfigState.filters.custom.eman9Filter
                    "canIjoinFilter" -> pfConfigState.filters.custom.canIjoinFilter
                    else -> false // Default case if key is not found
                }
                else -> false // Default case if list is not found
            }
        } else {
            when (list) {
                "diana" -> when (key) {
                    "eman9" -> pfConfigState.checkboxes.diana.eman9
                    "looting5" -> pfConfigState.checkboxes.diana.looting5
                    else -> false
                }
                "custom" -> when (key) {
                    "eman9" -> pfConfigState.checkboxes.custom.eman9
                    else -> false
                }
                else -> false
            }
        }

        private val bgbox = if (rounded) UIRoundedRectangle(roundness) else UIBlock()
        private val checkbox = if (rounded) UIRoundedRectangle(roundness) else UIBlock()
        private val outlineBlock = if (rounded) UIRoundedRectangle(roundness) else UIBlock()
        private lateinit var textObject: UIText

        fun setBgBoxColor(color: Color): Checkbox {
            bgbox.setColor(color)
            return this
        }

        fun setCheckBoxDimensions(width: SizeConstraint, height: SizeConstraint): Checkbox {
            checkbox.setWidth(width).setHeight(height)
            return this
        }

        fun setTextColor(color: Color): Checkbox {
            textObject.setColor(color)
            return this
        }

        fun setOnClick(callback: () -> Unit): Checkbox {
            onClick = callback
            return this
        }

        fun create(): UIComponent {
            bgbox.constrain {
                this.x = x
                this.y = y
                this.width = width
                this.height = height
            }.setColor(Color(0, 0, 0, 0))

            val groupContainer = UIBlock().constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                width = ChildBasedSizeConstraint()
                height = ChildBasedSizeConstraint()
            }.setColor(Color(0, 0, 0, 0)) childOf bgbox

            textObject = UIText(text).constrain {
                x = 0.pixels
                y = CenterConstraint()
            } childOf groupContainer
            textObject.setColor(Color(255, 255, 255, 255))

            checkbox.constrain {
                x = SiblingConstraint(5f)
                y = CenterConstraint()
                width = 16.pixels()
                height = 16.pixels()
            }.setColor(if (checked) checkedColor else color) childOf groupContainer

            checkbox.onMouseClick {
                checked = !checked
                checkbox.setColor(if (checked) checkedColor else color)
                SboDataObject.updatePfConfigState(if (filter) "filters" else "checkboxes", list, key, checked)
                if (this@Checkbox::onClick.isInitialized) {
                    this@Checkbox.onClick()
                }
            }

            return bgbox
        }
    }
}