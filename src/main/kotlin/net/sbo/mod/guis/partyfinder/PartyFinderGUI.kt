package net.sbo.mod.guis.partyfinder

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.SVGComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.PixelConstraint
import gg.essential.elementa.constraints.PositionConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UKeyboard
import net.sbo.mod.utils.EventBus
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.settings.categories.PartyFinder
import net.minecraft.util.Util
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper
import net.sbo.mod.partyfinder.PartyFinderManager.createParty
import net.sbo.mod.guis.partyfinder.pages.DianaPage
import net.sbo.mod.guis.partyfinder.pages.CustomPage
import net.sbo.mod.guis.partyfinder.pages.Help
import net.sbo.mod.guis.partyfinder.pages.Home
import java.awt.Color
class PartyFinderGUI : WindowScreen(ElementaVersion.V10) {

    internal val elementToHighlight: MutableList<HighlightElement> = mutableListOf()
    internal var selectedPage: String = "Home"
    internal val pages: MutableMap<String, () -> Unit> = mutableMapOf()
    internal var partyCache: MutableMap<String, List<Party>> = mutableMapOf()
    internal var lastRefreshTime: Long = 0L
    internal var cpWindowOpened: Boolean = false
    internal var filterWindowOpened: Boolean = false
    internal var partyInfoOpened: Boolean = false
    internal var dequeued: Boolean = false

    private val dianaPage = DianaPage(this)
    private val customPage = CustomPage(this)
    private val homePage = Home(this)
    private val helpPage = Help(this)

    internal lateinit var filterBackground: UIComponent
    internal lateinit var filterWindow : UIComponent
    internal lateinit var partyInfoWindow : UIComponent
    internal lateinit var cpWindow : UIComponent
    internal lateinit var base : UIComponent
    internal lateinit var onlineUserBlock: UIComponent
    internal lateinit var onlineUserText: UIText
    internal lateinit var titleBlock: UIComponent
    internal lateinit var categoryBlock: UIComponent
    internal lateinit var contentBlock: UIComponent
    internal lateinit var playerNameBase: UIComponent
    internal lateinit var partyListContainer: UIComponent
    internal lateinit var noParties : UIComponent
    internal lateinit var partyShowType : UIComponent
    internal lateinit var reqsBox: UIComponent
    internal lateinit var createBox: UIComponent
    internal lateinit var filterBox: UIComponent
    internal lateinit var infobase: UIComponent
    internal lateinit var partyCount: UIText
    internal var guiScale: Int? = null


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
        updateOnlineUser()
        updatePageHighlight()
        //for the unlucky event that someone spams opening and closing the cp winodw/filter window
        closeCpWindow()
        closeFilterWindow()

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

    internal fun getTextScale(base: Float = 1f): PixelConstraint {
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

    private fun getFilter(pageType: String) {
        // todo implement filter logic
    }

    private fun getPartyInfo(type: String, list: PartyInfo) : String {
        return when (type) {
            "Diana" -> dianaPage.getPartyInfo(list)
            "Custom" -> customPage.getPartyInfo(list)
            else -> "No party info available."
        }
    }

    private fun joinParty(leader: String, reqs: List<*>) {
        //todo implement logic below
//        if (!getInQueue() && !isInParty()) {
//            sendJoinRequest(leader, reqs)
//        }
//        else {
//            let leaderCheck = leader === Player.getName()
//            if (getInQueue() && !isInParty() && !leaderCheck) ChatLib.chat("&6[SBO] &eYou are already in queue.")
//            if (isInParty() && !getInQueue() && !leaderCheck) ChatLib.chat("&6[SBO] &eYou are already in a party.")
//            if (leaderCheck) ChatLib.chat("&6[SBO] &eYou can't join your own party.")
//        }
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

    internal fun openCpWindow() {
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
        if (this::reqsBox.isInitialized) cpWindow.removeChild(reqsBox)
        if (this::createBox.isInitialized) cpWindow.removeChild(createBox)
        if (this::filterBox.isInitialized) window.removeChild(filterBox)
        if (this::infobase.isInitialized) partyInfoWindow.removeChild(infobase)
    }

    private fun unqueueParty() {
        // todo: implement logic below
//        if (getInQueue()) {
//            removePartyFromQueue(true, (response) => {
//                this.dequeued = response
//                if (this.dequeued) this.updateCurrentPartyList(true)
//                else ChatLib.chat("&6[SBO] &eFailed to unqueue party.")
//            });
//        }
    }

    private fun partyCreate(reqs: String, note: String, type: String, size: Int = 6) {
        createParty(
            reqs = reqs,
            note = note,
            type = type,
            size = size
        )
    }

    private fun filterPartyList(filtrpredicate: Any? = null) {
        val partyList = partyCache[selectedPage]
        if (partyList == null) {
            updateCurrentPartyList(true)
            return
        }
        val resultList = if (filtrpredicate != null) {
            partyList.filter { it.reqs.contains(filtrpredicate) }
        } else {
            partyList
        }
        addPartyList(resultList, true)
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

    private fun updateCurrentPartyList(ignoreCooldown: Boolean = false) {
        var now = System.currentTimeMillis()
        if (!ignoreCooldown && (now - this.lastRefreshTime) < 1000) {
            Chat.chat("§6[SBO] §ePlease wait before refreshing the party list again.")
            return
        }
        lastRefreshTime = now
        // todo: implement logic below
//        getAllParties((partyList) => {
//            this.partyCache[this.selectedPage] = partyList;
//            const compositeFilter = this.getFilter(this.selectedPage);
//            if (compositeFilter) {
//                this.filterPartyList(compositeFilter);
//            } else {
//                this.addPartyList(partyList);
//            }
//        }, this.selectedPage);
    }

    private fun updateOnlineUser() {
        if (!::onlineUserText.isInitialized) return
        // todo: fetch online user count from server
//        getActiveUsers(true, (activeUsers) => {
//            this.Onlineusers.setText("Online: " + activeUsers)
//        })
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

    private fun addPartyList(partyList: List<Party>, ignoreCache: Boolean = false) {
        var list: List<Party> = partyList
        if (!ignoreCache && partyCache[selectedPage] != null) {
            list = partyCache[selectedPage]!!
        }
        updatePartyCount(list.size)
        renderPartyList(list)
    }

    private fun renderPartyList(list: List<Party>) {
        if (selectedPage !== "Diana" && selectedPage !== "Custom") return
        if (list.isEmpty()) {
            noParties.unhide(true)
            partyListContainer.clearChildren()
            return
        }
        val partyBlocks = mutableListOf<UIComponent>()
        list.forEach { party ->
            val reqsString = when (selectedPage) {
                "Diana" -> {
                    "" // todo: this.dianaPage.getReqsString(party.reqs);
                }
                "Custom" -> {
                    "" // todo: this.customPage.getReqsString(party.reqs);
                }
                else -> "No requirements"
            }

            val partyBlock = UIBlock().constrain {
                y = SiblingConstraint()
                width = 100.percent()
                height = 22.percent()
            }.setColor(Color(0, 0, 0, 150))
                .enableEffect(OutlineEffect(Color(0, 110, 250, 255), 1f))
                .addChild(UIBlock().constrain {
                    width = 20.percent()
                    height = 100.percent()
                }.setColor(Color(0, 0, 0, 0))
                    .addChild(UIText(party.leaderName).constrain {
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
                    rounded = true
                ).get())
            val reqsNote = UIBlock().constrain {
                x = SiblingConstraint()
                y = CenterConstraint()
                width = 50.percent()
                height = 100.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIBlock().constrain {
                    x = CenterConstraint()
                    y = 0.pixels
                    width = 100.percent()
                    height = 50.percent()
                }.setColor(Color(0, 0, 0, 0))
                    .addChild(UIBlock().constrain {
                        x = CenterConstraint()
                        y = SiblingConstraint()
                        width = 90.percent()
                        height = 100.percent()
                    }.setColor(Color(0, 0, 0, 0))
                        .addChild(UIWrappedText(reqsString).constrain {
                            x = 0.pixels
                            y = CenterConstraint()
                            width = 100.percent()
                            textScale = getTextScale(1f)
                        }.setColor(Color(255, 255, 255, 255)))
                    )
                )
                .addChild(UIBlock().constrain {
                    x = CenterConstraint()
                    y = SiblingConstraint()
                    width = 100.percent()
                    height = 50.percent()
                }.setColor(Color(0, 0, 0, 0))
                    .addChild(UIBlock().constrain {
                        x = CenterConstraint()
                        y = CenterConstraint()
                        width = 90.percent()
                        height = 100.percent()
                    }.setColor(Color(0, 0, 0, 0))
                        .addChild(UIWrappedText("&bNote: &7" + party.note).constrain {
                            x = 0.pixels
                            y = CenterConstraint()
                            width = 100.percent()
                            textScale = getTextScale(1f)
                        }.setColor(Color(255, 255, 255, 255)))
                    )
                )
            partyBlock.addChild(reqsNote)
                .addChild(GuiHandler.UILine(
                    x = SiblingConstraint(),
                    y = CenterConstraint(),
                    width = 0.3f.percent(),
                    height = 80.percent(),
                    color = Color(0, 110, 250, 255),
                    rounded = true
                ).get())
                .addChild(UIBlock().constrain {
                    x = SiblingConstraint()
                    y = CenterConstraint()
                    width = 10.percent()
                    height = 100.percent()
                }.setColor(Color(0, 0, 0, 0))
                    .addChild(UIText("${party.partymembers}/${party.partySize}").constrain {
                        x = CenterConstraint()
                        y = CenterConstraint()
                        color = getMemberColor(party.partymembers, party.partySize) as ColorConstraint
                        textScale = getTextScale(1f)
                    })
                )
                .addChild(GuiHandler.UILine(
                    x = SiblingConstraint(),
                    y = CenterConstraint(),
                    width = 0.3f.percent(),
                    height = 80.percent(),
                    color = Color(0, 110, 250, 255),
                    rounded = true
                ).get())
            val joinBlock = UIBlock().constrain {
                x = SiblingConstraint()
                y = CenterConstraint()
                width = FillConstraint()
                height = 100.percent()
            }.setColor(Color(50, 50, 50, 0))

            val joinButton = GuiHandler.Button(
                text ="Join",
                x =CenterConstraint(),
                y = CenterConstraint(),
                width = 70.percent(),
                height = 40.percent(),
                color = Color(30, 30, 30, 255),
                textColor = Color(0, 255, 0, 255),
                rounded = true
            )
            joinBlock.addChild(joinButton.uiObject)
            partyBlock.addChild(joinBlock)
            joinButton.textObject.setTextScale(getTextScale(1f))
            joinButton.setOnClick {
                joinParty(party.leaderName, party.reqs)
            }
            joinButton.uiObject.onMouseEnter {
                if (filterWindowOpened) return@onMouseEnter
                this.setColor(Color(70, 70, 70, 200))
                partyBlock.setColor(Color(0, 0, 0, 150))
            }
            joinButton.uiObject.onMouseLeave {
                if (filterWindowOpened) return@onMouseLeave
                this.setColor(Color(30, 30, 30, 255))
                partyBlock.setColor(Color(0, 0, 0, 220))
            }
            partyBlock.onMouseEnter {
                if (filterWindowOpened) return@onMouseEnter
                partyBlock.setColor(Color(0, 0, 0, 220))
            }
            partyBlock.onMouseLeave {
                if (filterWindowOpened) return@onMouseLeave
                partyBlock.setColor(Color(0, 0, 0, 150))
            }
            partyBlock.onMouseClick {
                renderPartyInfo(party.partyInfo)
            }
            partyBlocks.add(partyBlock)
        }
        if (partyBlocks.isEmpty()) return
        partyListContainer.clearChildren()
        Helper.sleep(100) {
            partyListContainer.addChild(partyShowType)
            partyBlocks.forEach { block ->
                partyListContainer.addChild(block)
            }
        }
    }

    private fun renderPartyInfo(partyInfoList: List<PartyInfo>) {
        playerNameBase.clearChildren()
        openPartyInfoWindow()
        partyInfoWindow.constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 60.percent()
            height = 65.percent()
        }.setColor(Color(0, 0, 0, 0))
        infobase = UIRoundedRectangle(10f).constrain {
            x = 0.percent()
            y = 0.percent()
            width = 100.percent()
            height = 100.percent()
        }.setColor(Color(30, 30, 30, 240)) childOf partyInfoWindow
        val infoDisplay = UIRoundedRectangle(10f).constrain {
            x = SiblingConstraint()
            y = CenterConstraint()
            width = 48.percent()
            height = 95.percent()
        }.setColor(Color(0, 0, 0, 150))
        val infoScroll = ScrollComponent().constrain {
            x = 0.percent()
            y = 0.percent()
            width = 100.percent()
            height = 100.percent()
        }.setColor(Color(0, 0, 0, 0))
        infobase.addChild(playerNameBase)
        infoDisplay.addChild(infoScroll)
        infobase.addChild(infoDisplay)
        partyInfoList.forEach { party ->
            val objheight = infobase.getHeight() / 6
            val infoString = getPartyInfo(selectedPage, party)
            val playerBlock = UIRoundedRectangle(10f).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                width = 60.percent()
                height = 70.percent()
            }.setColor(Color(0, 0, 0, 150))
                .addChild(UIText(party.name).constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1f)
                }.setColor(Color(255, 255, 255, 255)))
            playerBlock.onMouseEnter {
                playerBlock.setColor(Color(50, 50, 50, 255))
                infoScroll.clearChildren()
                infoScroll.addChild(UIWrappedText(infoString).constrain {
                    x = 4.percent()
                    y = 4.percent()
                    width = 96.percent()
                    textScale = getTextScale(1f)
                })
            }
            playerBlock.onMouseLeave {
                playerBlock.setColor(Color(0, 0, 0, 200))
            }
            playerNameBase.addChild(UIBlock().constrain {
                x = 0.percent()
                y = SiblingConstraint(0f)
                width = 100.percent()
                height = objheight.pixels()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(playerBlock)
            )
        }

    }

    internal fun addPartyListFunctions(listName: String, createParty: () -> Unit) {
        val line = GuiHandler.UILine(
            x = 0.percent(),
            y = 7.percent(),
            width = 100.percent(),
            height = 0.3f.percent(),
            color = Color(0, 110, 250, 255)
        ).get()
        partyCount = UIText("").constrain {
            x = SiblingConstraint()
            y = CenterConstraint()
            textScale = getTextScale(1f)
        }
        partyCount.setColor(Color(255, 255, 255, 255))
        val filterSvgComp = SVGComponent.ofResource("sbo-kotlin:svgs/filter.svg").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = getIconScale()
            height = getIconScale()
        }.setColor(Color(0, 110, 250, 255))
        val filterBlock = UIBlock().constrain {
            x = SiblingConstraint()
            y = CenterConstraint()
            width = 4.percent()
            height = 80.percent()
        }.setColor(Color(0, 0, 0, 0))
        filterBlock.addChild(filterSvgComp)
        filterBlock.onMouseClick {
            val x = filterBlock.getLeft() + (filterBlock.getWidth() / 2f)
            val y = line.getBottom()
            addFilterPage(listName, x.pixels(), y.pixels())
        }
        filterBlock.onMouseEnter {
            filterSvgComp.setColor(Color(50, 50, 255, 200))
        }
        filterBlock.onMouseLeave {
            filterSvgComp.setColor(Color(0, 110, 250, 255))
        }
        val refreshSvgComp = SVGComponent.ofResource("sbo-kotlin:svgs/refresh.svg").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = getIconScale()
            height = getIconScale()
        }.setColor(Color(0, 110, 250, 255))
        val refreshBlock = UIBlock().constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            width = 4.percent()
            height = 80.percent()
        }.setColor(Color(0, 0, 0, 0))
        refreshBlock.addChild(refreshSvgComp)
        refreshBlock.onMouseClick {
            updateCurrentPartyList()
        }
        refreshBlock.onMouseEnter {
            refreshSvgComp.setColor(Color(50, 50, 255, 200))
        }
        refreshBlock.onMouseLeave {
            refreshSvgComp.setColor(Color(0, 110, 250, 255))
        }
        val unqueuePartySvgComp = SVGComponent.ofResource("sbo-kotlin:svgs/user-minus.svg").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = getIconScale()
            height = getIconScale()
        }.setColor(Color(255, 0, 0, 255))
        val unqueuePartyBlock = UIBlock().constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            width = 4.percent()
            height = 80.percent()
        }.setColor(Color(0, 0, 0, 0))
        unqueuePartyBlock.addChild(unqueuePartySvgComp)
        unqueuePartyBlock.onMouseClick {
            unqueueParty()
        }
        unqueuePartyBlock.onMouseEnter {
            unqueuePartySvgComp.setColor(Color(50, 50, 255, 200))
        }
        unqueuePartyBlock.onMouseLeave {
            unqueuePartySvgComp.setColor(Color(255, 0, 0, 255))
        }
        val createPartySvgComp = SVGComponent.ofResource("sbo-kotlin:svgs/user-plus.svg").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = getIconScale()
            height = getIconScale()
        }.setColor(Color(0, 255, 0, 255))
        val createPartyBlock = UIBlock().constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            width = 4.percent()
            height = 80.percent()
        }.setColor(Color(0, 0, 0, 0))
        createPartyBlock.addChild(createPartySvgComp)
        createPartyBlock.onMouseClick {
            createParty()
        }
        createPartyBlock.onMouseEnter {
            createPartySvgComp.setColor(Color(50, 50, 255, 200))
        }
        createPartyBlock.onMouseLeave {
            createPartySvgComp.setColor(Color(0, 255, 0, 255))
        }
        // todo: maybe add svgfix if needed
        contentBlock.addChild(line)
        contentBlock.addChild(UIBlock().constrain {
            width = 100.percent()
            height = 7.percent()
        }.setColor(Color(0, 0, 0, 0))
            .addChild(UIBlock().constrain {
                x = 1.percent()
                y = CenterConstraint()
                width = 4.percent()
                height = 70.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(SVGComponent.ofResource("sbo-kotlin:svgs/users-group.svg").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    width = getIconScale()
                    height = getIconScale()
                }.setColor(Color(0, 110, 250, 255)))
            )
            .addChild(partyCount)
            .addChild(UIBlock().constrain {
                x = SiblingConstraint()
                y = CenterConstraint()
                width = 70.percent()
                height = 100.percent()
            }.setColor(Color(0, 0, 0, 0))
                .addChild(UIText(listName).constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    textScale = getTextScale(1.5f)
                }.setColor(Color(255, 255, 255, 255)))
            )
            .addChild(filterBlock)
            .addChild(refreshBlock)
            .addChild(unqueuePartyBlock)
            .addChild(createPartyBlock)
            // todo: add svgfix here if needed
        )
    }

    private fun settings() {
        mc.send{
            displayScreen(ResourcefulConfigScreen.getFactory("sbo").apply(null))
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
        addPage("Home", homePage::render, isSubPage = true, y1 = 93.percent())
        addPage("Help", helpPage::render, isSubPage = true)
        addPage("Settings", ::settings, isSubPage = true, y1 = null, isClickable = true)
    }
}