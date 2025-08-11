package net.sbo.mod.guis.partyfinder

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.PixelConstraint
import gg.essential.elementa.constraints.PositionConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UKeyboard
import net.sbo.mod.utils.EventBus
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.settings.categories.PartyFinder
import net.minecraft.util.Util
import net.sbo.mod.SBOKotlin
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.data.SboDataObject.pfConfigState
import java.awt.Color

data class HighlightElement(
    val page: String,
    val obj: UIComponent,
    val type: String
)

class PartyFinderGUI : WindowScreen(ElementaVersion.V10) {

    val elementToHighlight: MutableList<HighlightElement> = mutableListOf()
    var selectedPage: String = "Home"
    val pages: MutableMap<String, () -> Unit> = mutableMapOf()
    var partyCache: MutableMap<String, Any> = mutableMapOf()
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
    private lateinit var onlineUserText: UIText
    private lateinit var titleBlock: UIComponent
    private lateinit var categoryBlock: UIComponent
    private lateinit var contentBlock: UIComponent
    private lateinit var playerNameBase: UIComponent
    private lateinit var partyListContainer: UIComponent
    private lateinit var noParties : UIComponent
    private lateinit var partyShowType : UIComponent
    private var guiScale: Int? = null




    init {
        registers()
        create()
        onScreenOpen()

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

    private fun onScreenOpen() {
        updateSelectedPage()
        updatePageHighlight()

        if (mc.options.guiScale.value == 2) return
        guiScale = mc.options.guiScale.value
        mc.options.guiScale.value = 2 // this is a workaround for text scaling
    }

    override fun onScreenClose() {
        super.onScreenClose()
        partyCache = mutableMapOf() // clear party cache on close
        if (mc.options.guiScale.value != 2 || guiScale == null) return
        mc.options.guiScale.value = guiScale // restore original gui scale
        guiScale = null
    }

    private fun registers() {

    }

    private fun getTextScale(base: Float = 1f): PixelConstraint {
        return if (base + PartyFinder.scaleText <= 0f) return 0.1f.pixels()
        else (base + PartyFinder.scaleText).pixels()
    }

    private fun getIconScale(base: Int = 18): PixelConstraint {
        return if (base + PartyFinder.scaleIcon <= 0) return 1.pixels()
        else (base + PartyFinder.scaleIcon).pixels()
    }

    private fun getMemberColor(members: Int, patySize: Int): Color {
        val ratio = members.toFloat() / patySize.toFloat()
        return if (ratio < 0.5f)  {
            Color(0,255,0,255)
        } else {
            Color(255,165,0,255)
        }
    }

    private fun openPartyInfoWindow() {
        base.hide()
        partyInfoWindow.unhide(false)
        partyInfoOpened = true
    }

    private fun closePartyInfoWindow() {
        partyInfoWindow.hide()
        checkWindows()
        base.unhide(true)
        partyInfoOpened = false
    }

    private fun openFilterWindow() {
        filterBackground.unhide(false)
        filterWindow.unhide(false)
        filterWindowOpened = true
    }

    private fun closeFilterWindow() {
        filterBackground.hide()
        filterWindow.hide()
        checkWindows()
        filterWindowOpened = false
    }

    private fun openCpWindow() {
        base.hide()
        cpWindow.unhide(true)
        cpWindowOpened = true
    }

    private fun closeCpWindow() {
        cpWindow.hide()
        checkWindows()
        base.unhide(true)
        cpWindowOpened = false
    }

    private fun checkWindows() {
        // todo: implement logic to check if any windows are open and update the UI accordingly
    }

    private fun getFilter(pageType: String) {
        // todo implement filter logic
    }

    private fun updateSelectedPage() {
        if (selectedPage.isNotEmpty() && pages.containsKey(selectedPage)) {
            contentBlock.clearChildren()
            contentBlock.addChild(partyListContainer)
            Helper.sleep(100) {
                pages[selectedPage]?.invoke()
            }
        }
    }

    private fun updatePageHighlight() {
        elementToHighlight.forEach { element ->
            if (element.obj is UIBlock) {
                if (element.page === selectedPage) {
                    element.obj.setColor(Color(50, 50, 50, 255))
                } else {
                    element.obj.setColor(Color(0, 0, 0, 0))
                }
            } else {
                if (element.page === selectedPage) {
                    element.obj.setColor(Color(50, 50, 255, 200))
                } else {
                    element.obj.setColor(Color(255, 255, 255, 255))
                }
            }
        }
    }

    private fun updateCurrentPartyList(ignoreCooldown: Boolean) {

    }

    private fun updateOnlineUser() {
        if (!::onlineUserText.isInitialized) return
        // todo: fetch online user count from server
    }

    private fun updatePartyCount(count: Int) {
        if (!::onlineUserText.isInitialized) return
        onlineUserText.setText("Online: $count")
    }

    private fun addFilterPage(listName: String, x: PositionConstraint, y: PositionConstraint) {
        if (filterWindowOpened) {
            filterWindowOpened = false
            return
        }
        else openFilterWindow()

        when (listName) {
            "Diana Party List" -> {
                // todo: dianaPage._addDianaFilter(x, y);
            }

            "Custom Party List" -> {
                // todo: customPage._addCustomFilter(x, y);
            }
            else -> return
        }
    }

    private fun addPage(pageTitle: String, pageContent: () -> Unit, isSubPage: Boolean = false, y1: PositionConstraint? = null, isClickable: Boolean = false) {
        pages[pageTitle] = pageContent
        val finalY = y1 ?: if (isSubPage) SiblingConstraint(0f, true) else SiblingConstraint()

        val block = UIBlock().constrain {
            x = CenterConstraint()
            y = finalY
            width = 75.percent()
            height = 5.percent()
        }.setColor(Color(0, 0, 0, 0))

        val text = UIText("・ $pageTitle").constrain {
            y = CenterConstraint()
            textScale = getTextScale(1f)
        }.setColor(Color(255, 255, 255, 255))

        block.onMouseClick {
            if (selectedPage === pageTitle) return@onMouseClick
            if (isClickable) return@onMouseClick pageContent()
            selectedPage = pageTitle
            contentBlock.clearChildren()
            if (selectedPage != "Home" && selectedPage != "Help" && selectedPage != "Settimgs") contentBlock.addChild(partyListContainer)
            updatePageHighlight()
            pageContent()
        }

        block.addChild(text)
            .onMouseEnter {
                if (selectedPage === pageTitle) return@onMouseEnter
                block.setColor(Color(50, 50, 50, 150))
            }
            .onMouseLeave {
                if (selectedPage === pageTitle) return@onMouseLeave
                block.setColor(Color(0, 0, 0, 0))
            }

        categoryBlock.addChild(block)
            .addChild(GuiHandler.UILine(
                x = CenterConstraint(),
                y = if (isSubPage) SiblingConstraint(0f, true) else SiblingConstraint(),
                width = 75.percent(),
                height = 0.3f.percent(),
                color = Color(0, 110, 250, 255)
            ).get())

        elementToHighlight.add(HighlightElement(pageTitle, text, "pageTitle"))
        elementToHighlight.add(HighlightElement(pageTitle, block, "pageBlock"))
    }

    private fun settings() {
        mc.send{
            mc.setScreen(ResourcefulConfigScreen.getFactory("sbo").apply(null))
        }
    }

    private fun home() {
        noParties.hide()
        contentBlock.addChild(ScrollComponent().constrain {
            x = 0.percent()
            y = 0.percent()
            width = 100.percent()
            height = 100.percent()
        }.setColor(Color(0, 0, 0, 0))
            .addChild(UIBlock().constrain {
                width = 100.percent()
                height = 9.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIWrappedText("Welcome to the SBO Party Finder!").constrain {
                    x = 2.percent()
                    y = CenterConstraint()
                    width = 100.percent()
                    textScale = getTextScale(1.5f)
                }.setColor(Color(255, 255, 255, 255)))
            )
            .addChild(UIWrappedText(
                "・ Find parties with custom requirements that Hypixel doesn't offer.\n\n" +
                        "・ Create your own party or join others.\n\n" +
                        "・ Set custom requirements and wait for players to join.\n\n" +
                        "・ Made and maintained by the Skyblock Overhaul team.\n\n" +
                        "・ We rely on a server and appreciate any support to keep it running.")
                .constrain {
                    x = 2.percent()
                    y = SiblingConstraint()
                    width = 100.percent()
                    textScale = getTextScale(1f)
                }.setColor(Color(255, 255, 255, 255))
            )
        )
    }

    private fun help() {
        noParties.hide()
        contentBlock.addChild(ScrollComponent().constrain {
            x = 0.percent()
            y = 0.percent()
            width = 100.percent()
            height = 100.percent()
        }.setColor(Color(0, 0, 0, 0))
            .addChild(UIBlock().constrain {
                width = 100.percent()
                height = 9.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIWrappedText("Help Page!").constrain {
                    x = 2.percent()
                    y = CenterConstraint()
                    width = 100.percent()
                    textScale = getTextScale(1.5f)
                }.setColor(Color(255, 255, 255, 255)))
            )
            .addChild(UIWrappedText(
                "・ Not Getting any Join Requests?\n\n" +
                        "   ・ Enable private Messages!\n\n" +
                        "   ・ /settings -> Social Settings.\n\n" +
                        "・ Requirements don't update?\n\n" +
                        "   ・ Wait 10mins and do /ct reload.\n\n" +
                        "・ Text or Icons too small or too big?\n\n" +
                        "   ・ Open party finder settings\n\n" +
                        "・ Not seeing your party in the list?\n\n" +
                        "   ・ Make sure you have the right filters set.\n\n" +
                        "・ Still having issues?\n\n" +
                        "   ・ Join our discord and ask for help."
            ).constrain {
                x = 2.percent()
                y = SiblingConstraint()
                width = 100.percent()
                textScale = getTextScale(1f)
            }.setColor(Color(255, 255, 255, 255)))
        )
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
        addPage("Home", ::home, isSubPage = true, y1 = 93.percent())
        addPage("Help", ::help, isSubPage = true)
        addPage("Settings", ::settings, isSubPage = true, y1 = null, isClickable = true)
    }
}