package net.sbo.mod.guis.partyfinder

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.PixelConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.addChild
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UKeyboard
import net.sbo.mod.utils.EventBus
import net.sbo.mod.SBOKotlin.mc
import net.minecraft.util.Util
import java.awt.Color

class PartyFinderGUI : WindowScreen(ElementaVersion.V10) {

    var openGui: Boolean = false
    val elementToHighlight: MutableList<Any> = mutableListOf()
    var selectedPage: String = "Home"
    val pages: MutableMap<String, () -> Unit> = mutableMapOf()
    val partyCache: MutableMap<String, Any> = mutableMapOf()
    var lastRefreshTime: Long = 0L
    var cpWindowOpened: Boolean = false
    var filterWindowOpened: Boolean = false
    var partyInfoOpened: Boolean = false
    var dequeued: Boolean = false

    private lateinit var filterBackground: UIComponent
    private lateinit var filterWindow : UIComponent
    private lateinit var partyInfoWindow : UIComponent
    private lateinit var cpWindow : UIComponent
    private lateinit var base : UIComponent
    private lateinit var onlineUserBlock: UIComponent
    private lateinit var onlineUserText: UIComponent
    private lateinit var titleBlock: UIComponent
    private lateinit var categoryBlock: UIComponent
    private lateinit var contentBlock: UIComponent
    private lateinit var playerNameBase: UIComponent
    private lateinit var partyListContainer: UIComponent
    private lateinit var noParties : UIComponent
    private lateinit var partyShowType : UIComponent



    init {
        registers()
        create()

        EventBus.on("refreshPartyList") {
            updateCurrentPartyList(true)
        }

        window.onKeyType { typedChar, keyCode ->
            if (keyCode == UKeyboard.KEY_ESCAPE) {
                mc.send {
                    displayScreen(null)
                }
            }
        }
    }

    private fun registers() {

    }

    private fun getTextScale(base: Float = 1f): PixelConstraint {
        return base.pixels() // todo: scaletext setting
    }

    private fun getIconScale(base: Int = 18): PixelConstraint {
        return base.pixels() // todo: scaleicon setting
    }

    private fun getMemberColor(members: Int, patySize: Int): Color {
        val ratio = members.toFloat() / patySize.toFloat()
        return if (ratio < 0.5f)  {
            Color(0,255,0,255)
        } else {
            Color(255,165,0,255)
        }
    }

    private fun create() {
        filterBackground = UIBlock().constrain {
            width = 100.percent()
            height = 100.percent()
            x = 0.percent()
            y = 0.percent()
        }.setColor(Color(0, 0, 0, 100)) childOf window
        filterBackground.hide()
        filterWindow = UIRoundedRectangle(10f) childOf window
        filterWindow.hide()
        partyInfoWindow = UIRoundedRectangle(10f) childOf window
        partyInfoWindow.hide()

        cpWindow = UIRoundedRectangle(10f).constrain {
            width = 30.percent()
            height = 40.percent()
            x = CenterConstraint()
            y = CenterConstraint()
        }.setColor(Color(30, 30, 30, 240))
            .addChild(UIBlock().constrain {
                width = 100.percent()
                height = 12.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIText("Create Party").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1.5f)
                }.setColor(Color(255, 255, 255, 255))))
            .addChild(GuiHandler.UILine(
                x = 0.percent(),
                y = SiblingConstraint(),
                width = 100.percent(),
                height = 1f.percent(),
                color = Color(0, 110, 250, 255)
            ).get())

        base = UIRoundedRectangle(10f).constrain {
            width = 60.percent()
            height = 65.percent()
            x = CenterConstraint()
            y = CenterConstraint()
        }.setColor(Color(30, 30, 30, 240)) childOf window
        //-----------------Title Block-----------------
        GuiHandler.UILine(
            x = 0.percent(),
            y = 5.percent(),
            width = 100.percent(),
            height = 0.3f.percent(),
            color = Color(0, 110, 250, 255),
            parent = base
        )
        onlineUserBlock = UIBlock().constrain {
            x = 10.percent()
            y = CenterConstraint()
            width = 40.percent()
            height = 80.percent()
        }.setColor(Color(0, 0, 0, 0))

        onlineUserText = UIText("Online: 0").constrain {
            x = 0.percent()
            y = CenterConstraint()
            textScale = getTextScale(1f)
        } childOf onlineUserBlock

        titleBlock = UIBlock().constrain {
            width = 100.percent()
            height = 5.percent()
        }.setColor(Color(0, 0, 0, 0))
            .setChildOf(base)
            .addChild(UIBlock().constrain {
                width = 25.percent()
                height = 100.percent()
                x = SiblingConstraint()
                y = CenterConstraint()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(onlineUserBlock))
            .addChild(UIBlock().constrain {
                width = 35.percent()
                height = 100.percent()
                x = CenterConstraint()
                y = CenterConstraint()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(
                UIText("SBO Party Finder").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1f)
                }.setColor(Color(255, 255, 255, 255)))
            )
        val discordBlock = UIBlock().constrain {
            width = 11.percent()
            height = 100.percent()
            x = SiblingConstraint()
        }.setColor(Color(0, 0, 0, 0)) childOf titleBlock

        val discord = GuiHandler.Button(
            text = "Discord",
            x = CenterConstraint(),
            y = CenterConstraint(),
            width = 80.percent(),
            height = 60.percent(),
            color = Color(0, 0, 0, 0),
            textColor = Color(255, 255, 255, 255),
            parent = discordBlock
        )
            .textHoverEffect(Color(255,255,255,255), Color(50, 50, 255, 200))
            .setTextOnClick() {
                Util.getOperatingSystem().open("https://discord.gg/QvM6b9jsJD")
            }
        discord.textObject.setTextScale(getTextScale())
        discord.uiObject.addChild(GuiHandler.UILine(
            x = CenterConstraint(),
            y = 100.percent(),
            (discord.textObject.getWidth() + 10).pixels(),
            10.percent(),
            Color(0, 110, 250, 255)
        ).get())

        val githubBlock = UIBlock().constrain {
            width = 11.percent()
            height = 100.percent()
            x = SiblingConstraint()
        }.setColor(Color(0, 0, 0, 0)) childOf titleBlock

        val github = GuiHandler.Button(
            text = "GitHub",
            x = CenterConstraint(),
            y = CenterConstraint(),
            width = 80.percent(),
            height = 60.percent(),
            color = Color(0, 0, 0, 0),
            textColor = Color(255, 255, 255, 255),
            parent = githubBlock
        )
            .textHoverEffect(Color(255,255,255,255), Color(50, 50, 255, 200))
            .setTextOnClick() {
                Util.getOperatingSystem().open("https://github.com/SkyblockOverhaul/SBO-Kotlin")
            }
        github.textObject.setTextScale(getTextScale())
        github.uiObject.addChild(GuiHandler.UILine(
            x = CenterConstraint(),
            y = 100.percent(),
            (github.textObject.getWidth() + 10).pixels(),
            10.percent(),
            Color(0, 110, 250, 255)
        ).get())

        val patreonBlock = UIBlock().constrain {
            width = 11.percent()
            height = 100.percent()
            x = SiblingConstraint()
        }.setColor(Color(0, 0, 0, 0)) childOf titleBlock

        val patreon = GuiHandler.Button(
            text = "Patreon",
            x = CenterConstraint(),
            y = CenterConstraint(),
            width = 80.percent(),
            height = 60.percent(),
            color = Color(0, 0, 0, 0),
            textColor = Color(255, 255, 255, 255),
            parent = patreonBlock
        )
            .textHoverEffect(Color(255,255,255,255), Color(50, 50, 255, 200))
            .setTextOnClick() {
                Util.getOperatingSystem().open("https://www.patreon.com/Skyblock_Overhaul")
            }
        patreon.textObject.setTextScale(getTextScale())
        patreon.uiObject.addChild(GuiHandler.UILine(
            x = CenterConstraint(),
            y = 100.percent(),
            (patreon.textObject.getWidth() + 10).pixels(),
            10.percent(),
            Color(0, 110, 250, 255)
        ).get())
        //-----------------End Title Block-----------------
        //-----------------Category Block-----------------
        GuiHandler.UILine(
            x = 15.percent(),
            y = 5.percent(),
            width = 0.2f.percent(),
            height = 95.percent(),
            color = Color(0, 110, 250, 255),
            parent = base
        )
        categoryBlock = UIBlock().constrain {
            width = 15.percent()
            height = 94.3f.percent()
            x = 0.percent()
            y = 5.7f.percent()
        }.setColor(Color(0, 0, 0, 0)) childOf base
        //-----------------End Category Block-----------------
        //-----------------Content Block-----------------
        contentBlock = UIBlock().constrain {
            width = 84.8f.percent()
            height = 94.7f.percent()
            x = 15.2f.percent()
            y = 5.3f.percent()
        }.setColor(Color(0, 0, 0, 0)) childOf base
        //-----------------End Content Block-----------------
        //-----------------Party Info-----------------
        playerNameBase = UIBlock().constrain {
            width = 50.percent()
            height = 100.percent()
            x = 0.percent()
            y = 0.percent()
        }.setColor(Color(0, 0, 0, 0))
        //-----------------End Party Info-----------------
        //-----------------Party List-----------------
        partyListContainer = ScrollComponent().constrain {
            width = 100.percent()
            height = 100.percent()
            x = 0.percent()
            y = 7.3f.percent()
        }.setColor(Color(0, 0, 0, 0))
        noParties = UIText("No parties found").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = getTextScale(1f)
        }.setColor(Color(255, 255, 255, 255)) childOf partyListContainer
        noParties.hide()
        partyShowType = UIBlock().constrain {
            width = 100.percent()
            height = 7.percent()
            x = 0.percent()
            y = 0.percent()
        }.setColor(Color(0, 0, 0, 150))
            .addChild(UIBlock().constrain {
                width = 20.percent()
                height = 100.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIText("Leader").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1f)
                }.setColor(Color(85, 255, 255, 255)))
            )
            .addChild(GuiHandler.UILine(
                x = SiblingConstraint(),
                y = CenterConstraint(),
                width = 0.3f.percent(),
                height = 80.percent(),
                color = Color(0, 110, 250, 255),
                rounded = true,
                ).get())
            .addChild(UIBlock().constrain {
                x = SiblingConstraint()
                y = CenterConstraint()
                width = 50.percent()
                height = 100.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIText("Reqs/Note").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1f)
                }.setColor(Color(85, 255, 255, 255))))
            .addChild(GuiHandler.UILine(
                x = SiblingConstraint(),
                y = CenterConstraint(),
                width = 0.3f.percent(),
                height = 80.percent(),
                color = Color(0, 110, 250, 255),
                rounded = true,
            ).get())
            .addChild(UIBlock().constrain {
                x = SiblingConstraint()
                y = CenterConstraint()
                width = 10.percent()
                height = 100.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIText("Members").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1f)
                }.setColor(Color(85, 255, 255, 255))))
            .addChild(GuiHandler.UILine(
                x = SiblingConstraint(),
                y = CenterConstraint(),
                width = 0.3f.percent(),
                height = 80.percent(),
                color = Color(0, 110, 250, 255),
                rounded = true,
            ).get())
            .addChild(UIBlock().constrain {
                x = SiblingConstraint()
                y = CenterConstraint()
                width = FillConstraint()
                height = 100.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIText("Button").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1f)
                }.setColor(Color(85, 255, 255, 255)))
            )
        //-----------------Pages-----------------
    }


    private fun updateCurrentPartyList(ignoreCooldown: Boolean) {

    }
}