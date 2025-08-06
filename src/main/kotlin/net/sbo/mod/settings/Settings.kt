package net.sbo.mod.settings

import gg.essential.universal.UDesktop
import gg.essential.universal.UChat
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.awt.Color
import java.io.File

object Settings : Vigilant(
    file = File("./config/sbo/config.toml"),
    guiTitle = "SBO Settings",
    sortingBehavior = Sorting // <--- Hier den Sorter einfügen
) {

    // Diana Burrows
    @Property(
        type = PropertyType.SWITCH,
        name = "Diana Burrow Guess",
        description = "Guess the burrow location. Needs Driping Lava Partciles and set /particlequality to Extreme for more accuracy",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var dianaBurrowGuess = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Burrow Guess Alternative",
        description = "[WIP] Makes a guess based on the arrow that spawns when you finished a burrow and the color it shows",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var dianaAdvancedBurrowGuess = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Diana Burrow Warp",
        description = "Warp close to the guess. set your keybind in controls",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var dianaBurrowWarp = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Dont warp if a burrow is nearby",
        description = "Doesnt warp you if a burrow is nearby",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var dontWarpIfBurrowNearby = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Diana Burrow Detect",
        description = "Detects Diana burrows | to reset waypoints /sboclearburrows",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var dianaBurrowDetect = true

    @Property(
        type = PropertyType.TEXT,
        name = "Warp Block Difference",
        description = "Increase it to set the difference when player warps (inq/burrow warp)",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var warpDiff = "10"

    @Property(
        type = PropertyType.SWITCH,
        name = "Warp only after <X>ms",
        description = "Waits before warping so the guess is more precise set it to ur own preference",
        category = "Diana",
        subcategory = "Diana Burrows"
    )
    var warpDelay = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Warp Delay",
        description = "Set the delay in ms",
        category = "Diana",
        subcategory = "Diana Burrows",
        min = 0,
        max = 1200
    )
    var warpDelayTime = 800

    // Diana Tracker
    @Property(
        type = PropertyType.SWITCH,
        name = "Diana Tracker",
        description = "Tracks your Diana loot and mob kills (you need to have Settings -> Personal -> Chat -> Sacks Notifications enabled for Gold and Iron to work)",
        category = "Diana",
        subcategory = "Diana Tracker",
    )
    var dianaTracker = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "Mob View",
        description = "Shows your Diana mob kills /sboguis to move the counter",
        category = "Diana",
        subcategory = "Diana Tracker",
        options = ["OFF", "Total", "Event", "Session"]
    )
    var dianaMobTrackerView = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Loot View",
        description = "Shows your Diana loot /sboguis to move the counter",
        category = "Diana",
        subcategory = "Diana Tracker",
        options = ["OFF", "Total", "Event", "Session"]
    )
    var dianaLootTrackerView = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Inquis Loot Tracker",
        description = "Shows your Inquisitor Loot so you see how lucky/unlucky you are (Shelmet/Plushie/Remedies)",
        category = "Diana",
        subcategory = "Diana Tracker",
        options = ["OFF", "Total", "Event", "Session"]
    )
    var inquisTracker = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Four-Eyed Fish",
        description = "Set if you have a Four-Eyed Fish on your griffin pet",
        category = "Diana",
        subcategory = "Diana Tracker",
    )
    var fourEyedFish = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Diana Stats",
        description = "Shows stats like Mobs since Inquisitor, Inquisitors since Chimera",
        category = "Diana",
        subcategory = "Diana Tracker",
    )
    var dianaStatsTracker = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Stats Message",
        description = "Sends the chat Message with stat: [SBO] Took 120 Mobs to get a Inquis!",
        category = "Diana",
        subcategory = "Diana Tracker",
    )
    var sendSinceMessage = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Avg Magic Find Tracker",
        description = "Shows your avg magic find for sticks and chimeras",
        category = "Diana",
        subcategory = "Diana Tracker",
    )
    var dianaAvgMagicFind = false

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Session Tracker",
        description = "Resets the session tracker For mobs and items (/sboresetsession)",
        placeholder = "Reset Session",
        category = "Diana",
        subcategory = "Diana Tracker",
    )
    fun resetTrackerSession() {
        UChat.chat("/sboresetsession")
    }

    @Property(
        type = PropertyType.SELECTOR,
        name = "Bazaar Setting Diana",
        description = "Bazaar setting to set the price for loot",
        category = "Diana",
        subcategory = "Diana Tracker",
        options = ["Instasell", "Sell Offer"],
    )
    var bazaarSettingDiana = 1

    // Diana Waypoints
    @Property(
        type = PropertyType.SWITCH,
        name = "Detect Inq Cords",
        description = "Create inquisitor waypoints",
        category = "Diana",
        subcategory = "Diana Waypoints",
    )
    var inqWaypoints = true

    @Property(
        type = PropertyType.SWITCH,
        name = "All Waypoints Are Inqs",
        description = "all the waypoints are inquisitor waypoints in hub during Diana",
        category = "Diana",
        subcategory = "Diana Waypoints",
    )
    var allWaypointsAreInqs = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Inq Warp Key",
        description = "Enable inquisitor warp key, set your keybind in controls.",
        category = "Diana",
        subcategory = "Diana Waypoints",
    )
    var inqWarpKey = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Guess When Burrow",
        description = "Removes guess when a burrow spawns on the guess",
        category = "Diana",
        subcategory = "Diana Waypoints",
    )
    var removeGuessWhenBurrow = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Remove Guess",
        description = "Removes guess when getting close to it",
        category = "Diana",
        subcategory = "Diana Waypoints",
    )
    var removeGuess = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Distance For Remove",
        description = "Input distance for guess removal",
        category = "Diana",
        subcategory = "Diana Waypoints",
        min = 1,
        max = 30
    )
    var removeGuessDistance = 10

    // Diana lines
    @Property(
        type = PropertyType.SWITCH,
        name = "Inquis Line",
        description = "Draws lines for Inquisitor, Disable View Bobbing in controls if its buggy",
        category = "Diana",
        subcategory = "Diana Waypoint Lines",
    )
    var inqLine = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Burrow Line",
        description = "Draws lines for burrows, Disable View Bobbing in controls if its buggy",
        category = "Diana",
        subcategory = "Diana Waypoint Lines",
    )
    var burrowLine = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Guess Line",
        description = "Draws line for guess, Disable View Bobbing in controls if its buggy",
        category = "Diana",
        subcategory = "Diana Waypoint Lines",
    )
    var guessLine = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Line Width For Diana",
        description = "Set the width of the lines for Diana waypoints",
        category = "Diana",
        subcategory = "Diana Waypoint Lines",
        min = 1,
        max = 10
    )
    var lineWidth = 3

    // Diana Other
    @Property(
        type = PropertyType.SWITCH,
        name = "Mythos HP",
        description = "Displays HP of mythological mobs near you. /sboguis to move it",
        category = "Diana",
        subcategory = "Other",
    )
    var mythosMobHp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Share Inquisitor",
        description = "Sends coords for inquisitor in party message (patcher format).",
        category = "Diana",
        subcategory = "Other",
    )
    var inquisDetect = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Copy Inquisitor Coords",
        description = "Copy coords for inquisitors (patcher format) mainly for muted player.",
        category = "Diana",
        subcategory = "Other",
    )
    var inquisDetectCopy = false

    @Property(
        type = PropertyType.TEXT,
        name = "Send Text On Inq Spawn",
        description = "Sends a text on inq spawn 5 seconds after spawn, use {since} for mobs since inq, {chance} for inq chance",
        category = "Diana",
        subcategory = "InqMessage",
    )
    var announceKilltext = ""

    @Property(
        type = PropertyType.BUTTON,
        name = "Send Test Inq Message",
        description = "Sends a test inq message in chat",
        category = "Diana",
        subcategory = "InqMessage",
    )
    fun testInqMessage() {
        UChat.chat("/sboinqmsgtest")
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "Highlight Inquis",
        description = "Highlights inquisitor.",
        category = "Diana",
        subcategory = "Other",
    )
    var inqHighlight = false

    @Property(
        type = PropertyType.COLOR,
        name = "Inquis/Lootshare Color",
        description = "Pick a color for inquisitor highlighting and lootshare circle",
        category = "Diana",
        subcategory = "Other"
    )
    var inqColor = Color(0, (0.9 * 255).toInt(), (1.0 * 255).toInt(), (0.6 * 255).toInt())

    @Property(
        type = PropertyType.SWITCH,
        name = "Inquis Lootshare Circle",
        description = "Draws a circle around inquisitor which shows the lootshare range",
        category = "Diana",
        subcategory = "Other",
    )
    var inqCircle = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Inquis Lootshare Cylinder",
        description = "Draws a Cylinder instead of a circle around inquisitor which shows the lootshare range",
        category = "Diana",
        subcategory = "Other",
    )
    var inqCylinder = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Add Wizard Warp",
        description = "Adds a warp point /warp wizard",
        category = "Diana",
        subcategory = "Other",
    )
    var wizardWarp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Add Crypt Warp",
        description = "Adds a warp point /warp crypt",
        category = "Diana",
        subcategory = "Other",
    )
    var cryptWarp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Add Stonks Warp",
        description = "Adds a warp point /warp stonks",
        category = "Diana",
        subcategory = "Other",
    )
    var stonksWarp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Add Dark Auction Warp",
        description = "Adds a warp point /warp da",
        category = "Diana",
        subcategory = "Other",
    )
    var darkAuctionWarp = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Add Castle Warp",
        description = "Adds a warp point /warp castle",
        category = "Diana",
        subcategory = "Other",
    )
    var castleWarp = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Chim Message",
        description = "Enables custom chim message",
        category = "Diana",
        subcategory = "Other",
    )
    var chimMessageBool = false

    @Property(
        type = PropertyType.TEXT,
        name = "Custom Chim Message Text",
        description = "use: {mf} for MagicFind, {amount} for drop Amount this event and {percentage} for chimera/inquis ratio.",
        category = "Diana",
        subcategory = "Other",
    )
    var customChimMessage = "&6[SBO] &6&lRARE DROP! &d&lChimera! &b{mf} &b#{amount}"

    @Property(
        type = PropertyType.BUTTON,
        name = "Chim Message Test",
        description = "Sends a test chim message in chat",
        category = "Diana",
        subcategory = "Other",
    )
    fun chimMessageTest() {
        UChat.chat("/sbochimtest")
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Custom Chim Message",
        description = "Resets the custom chim message to default, reopen settings to see the change",
        category = "Diana",
        subcategory = "Other",
    )
    fun resetCustomChimMessage() {
        customChimMessage = "&6[SBO] &6&lRARE DROP! &d&lChimera! &b{mf} &b#{amount}}"
    }

    // Loot Announcer
    @Property(
        type = PropertyType.SWITCH,
        name = "Rare Drop Announcer",
        description = "Announce loot in chat",
        category = "Diana",
        subcategory = "Loot Announcer",
    )
    var lootAnnouncerChat = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Loot Screen Announcer",
        description = "Announce chimera/stick/relic on screen",
        category = "Diana",
        subcategory = "Loot Announcer",
    )
    var lootAnnouncerScreen = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Price Title",
        description = "Announce chimera/stick/relic Price as a subtiltle on screen",
        category = "Diana",
        subcategory = "Loot Announcer",
    )
    var lootAnnouncerPrice = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Loot Party Announcer",
        description = "Announce (Chimera/Stick/Relic) and inquisitor (Shelmet/Plushie/Remedies) to party",
        category = "Diana",
        subcategory = "Loot Announcer",
    )
    var lootAnnouncerParty = false

    // Party Commands
    @Property(
        type = PropertyType.SWITCH,
        name = "Party Commands",
        description = "Enable party commands",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var partyCommands = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Warp Party",
        description = "!w, !warp",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var warpCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Allinvite",
        description = "!allinv, !allinvite",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var allinviteCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Party Transfer",
        description = "!transfer [Player] (if no player is defined it transfers the party to the command writer)",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var transferCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Promote/Demote",
        description = "!promote/demote [Player] (if no player is defined it pro/demotes the command writer)",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var moteCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Ask Carrot",
        description = "Enable !carrot Command",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var carrotCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Time Check",
        description = "Sends your time in party chat (!time)",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var timeCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Check Tps",
        description = "Sends the server tps in party chat (!tps)",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var tpsCommand = false

    // Diana Party Commands
    @Property(
        type = PropertyType.SWITCH,
        name = "Diana Party Commands",
        description = "Enable Diana party commands (!chim, !inq, !relic, !stick, !since, !burrow, !mob) (note: you need to have Diana tracker enabled)",
        category = "Party Commands",
        subcategory = "Party Commands",
    )
    var dianaPartyCommands = true

    // Quality of Life - Blaze
    @Property(
        type = PropertyType.SWITCH,
        name = "Effects For Blaze",
        description = "Displays effects for blaze slayer. /sboguis to move the overlay",
        category = "Quality of Life",
        subcategory = "Blaze",
    )
    var effectsGui = false

    @Property(
        type = PropertyType.TEXT,
        name = "Parrot Level",
        description = "Enter parrot level for effect duration (0 = off/no parrot)",
        category = "Quality of Life",
        subcategory = "Blaze",
    )
    var parrotLevel = "0"

    // Quality of Life
    @Property(
        type = PropertyType.SWITCH,
        name = "Formatted Bridge Bot",
        description = "Format bridge bot messages (that are like this \"Guild > bridgeBot: player: message\")",
        category = "Quality of Life",
    )
    var formatBridgeBot = false

    @Property(
        type = PropertyType.TEXT,
        name = "Bridge Bot Name",
        description = "Set the name of the bridge bot",
        category = "Quality of Life",
    )
    var bridgeBotName = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Copy Rare Drop",
        description = "Copy rare drop Message to clipboard",
        category = "Quality of Life",
    )
    var copyRareDrop = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Find Dragon Lair",
        description = "Find Dragon's Lair in crystal hollows (requires hostile mob sounds enabled)",
        category = "Quality of Life",
    )
    var findDragonNest = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Jacob Message Hider",
        description = "Hide Messages from jacob NPC in the chat",
        category = "Quality of Life",
    )
    var jacobHider = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Radio Weak Message",
        description = "Hides the radio weak message",
        category = "Quality of Life",
    )
    var hideRadioWeak = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Clean Diana Chat",
        description = "Hides all spammy Diana messages",
        category = "Quality of Life",
    )
    var cleanDianaChat = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide AutoPet Messages",
        description = "Hides all autopet messages",
        category = "Quality of Life",
    )
    var hideAutoPetMSG = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Sacks Message",
        description = "Hides all sacks messages",
        category = "Quality of Life",
    )
    var hideSackMessage = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Clickable Messages To Invite a Player",
        description = "Click on a message to invite a player to your party when he sends you a \"!inv\" per msg",
        category = "Quality of Life",
    )
    var clickableInvite = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Pickup Log Overlay",
        description = "Displays your pickup log in an overlay like sba, /sboguis to move the overlay (WIP)",
        category = "Quality of Life",
    )
    var pickuplogOverlay = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Tipped Players",
        description = "Hides the players you tipped in the chat",
        category = "Quality of Life",
    )
    var hideTippedPlayers = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Carnival Redstone Lamp Helper",
        description = "Highlights the redstone lamps and draws a line to it",
        category = "Quality of Life",
    )
    var carnivalLamp = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Carnival Zombie Helper",
        description = "Highlights the best zombie to shoot",
        category = "Quality of Life",
    )
    var carnivalZombie = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Carnival Zombie Line",
        description = "Draws a line to the best zombie to shoot",
        category = "Quality of Life",
    )
    var carnivalZombieLine = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Golden Fish Timer",
        description = "Shows a overlay with the timer until the next golden fish can spawn",
        category = "Quality of Life",
    )
    var goldenFishTimer = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Golden Fish Notification",
        description = "Notifies you when you have not thrown your Lava Rod in over 2 minutes and 30 seconds",
        category = "Quality of Life",
    )
    var goldenFishNotification = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Copy Chat Message",
        description = "[WIP] Copy chat message to clipboard (Like SBE)",
        category = "Quality of Life",
    )
    var copyChatMessage = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Flare Tracker",
        description = "Tracks the flare you placed (works with all flares)",
        category = "Quality of Life",
    )
    var flareTimer = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Flare Expire Soon Alert",
        description = "Alerts you when your flare expires soon with a title",
        category = "Quality of Life",
    )
    var flareExpireAlert = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Hide Own Flare When Not In Range",
        description = "Hides your own flare when your not in range of it",
        category = "Quality of Life",
    )
    var notInRangeSetting = false

    // General
    @Property(
        type = PropertyType.BUTTON,
        name = "Move GUIs",
        description = "Opens Gui Move Menu you can use /sboguis too",
        placeholder = "Move GUIs",
        category = "General",
    )
    fun sbomoveguis() {
        UChat.chat("/sboguis")
    }

    // GUIs
    @Property(
        type = PropertyType.SWITCH,
        name = "Bobber Overlay",
        description = "Tracks the number of bobbers near you /sboguis to move the counter",
        category = "General",
        subcategory = "GUIs",
    )
    var bobberOverlay = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Legion Overlay",
        description = "Tracks the players near you for legion buff /sboguis to move the counter",
        category = "General",
        subcategory = "GUIs",
    )
    var legionOverlay = false

    // General Waypoints
    @Property(
        type = PropertyType.SWITCH,
        name = "Detect Patcher Cords",
        description = "Create patcher waypoints",
        category = "General",
        subcategory = "Waypoints",
    )
    var patcherWaypoints = false

    @Property(
        type = PropertyType.SELECTOR,
        name = "Hide Own Waypoints",
        description = "Hide your own patcher/inquisitor waypoints",
        category = "General",
        subcategory = "Waypoints",
        options = ["OFF", "Inq Waypoints", "Patcher Waypoints", "Both Waypoints"]
    )
    var hideOwnWaypoints = 0

    // Mining
    @Property(
        type = PropertyType.SWITCH,
        name = "Fossil Solver",
        description = "Enables the fossil solver",
        category = "Mining",
    )
    var fossilSolver = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Fossil Overlay",
        description = "Tells you the fossil you excavate /sboguis to move the overlay",
        category = "Mining",
    )
    var fossilOverlay = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Highlight All Possible Fossils",
        description = "Highlights all potential fossil locations with equal probability (if this is off only one location will be highlighted)",
        category = "Mining",
    )
    var highlightAllSlots = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Create Exit Waypoint",
        description = "Creates a waypoint at the exit of the mineshaft",
        category = "Mining",
    )
    var exitWaypoint = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Speed Boost Title",
        description = "Shows a title when you get a speed boost",
        category = "Mining",
    )
    var mineSpeedBoost = false

    // Color Settings
    @Property(
        type = PropertyType.COLOR,
        name = "Start Burrow Color",
        description = "Pick a color for start burrows",
        category = "Customization",
        subcategory = "Colors"
    )
    var startColor = Color((0.333 * 255).toInt(), (1.0 * 255).toInt(), (0.333 * 255).toInt())

    @Property(
        type = PropertyType.COLOR,
        name = "Mob Burrow Color",
        description = "Pick a color for mob burrows",
        category = "Customization",
        subcategory = "Colors"
    )
    var mobColor = Color((1.0 * 255).toInt(), (0.333 * 255).toInt(), (0.333 * 255).toInt())

    @Property(
        type = PropertyType.COLOR,
        name = "Treasure Burrow Color",
        description = "Pick a color for treasure burrows",
        category = "Customization",
        subcategory = "Colors"
    )
    var treasureColor = Color((1.0 * 255).toInt(), (0.667 * 255).toInt(), 0)

    @Property(
        type = PropertyType.COLOR,
        name = "Guess Color",
        description = "Pick a color for your guess",
        category = "Customization",
        subcategory = "Colors"
    )
    var guessColor = Color(255, 255, 255)

    @Property(
        type = PropertyType.COLOR,
        name = "Slot Highlighting Color",
        description = "Pick a color for slot highlighting",
        category = "Customization",
        subcategory = "Colors"
    )
    var slotColor = Color.RED

    // Custom
    @Property(
        type = PropertyType.SLIDER,
        name = "Waypoint Text Size",
        description = "Set the size of the waypoint text",
        category = "Customization",
        subcategory = "Waypoint",
        min = 5,
        max = 20
    )
    var waypointTextScale = 7

    @Property(
        type = PropertyType.SWITCH,
        name = "Waypoint Text Shadow",
        description = "Enables a shadow for the waypoint text",
        category = "Customization",
        subcategory = "Waypoint"
    )
    var waypointTextShadow = true

    // Crown Tracker
    @Property(
        type = PropertyType.SWITCH,
        name = "Crown Tracker",
        description = "Tracks your crown of avarice coins",
        category = "Quality of Life",
        subcategory = "Crown Tracker"
    )
    var crownTracker = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Crown Ghost Mode",
        description = "Displays Session stats for ghosts: Ghosts/Tier (how many ghosts for next tier (not perfetly accurate))",
        category = "Quality of Life",
        subcategory = "Crown Tracker"
    )
    var crownGhostMode = false

    @Property(
        type = PropertyType.BUTTON,
        name = "Reset Crown Tracker",
        description = "Resets the crown tracker",
        placeholder = "Reset Crown Tracker",
        category = "Quality of Life",
        subcategory = "Crown Tracker"
    )
    fun resetCrownTracker() {
        UChat.chat("/sboresetcrowntracker")
    }

    // Sound Settings
    @Property(
        type = PropertyType.BUTTON,
        name = "Open Sound Folder",
        description = "Custom sounds go in here (sound must be a .ogg) (do \"/ct load\" after adding sounds else they wont work)",
        category = "Customization",
        subcategory = "Sound Config"
    )
    fun openSoundFolder() {
        // Need to implement file utilities for Kotlin. Assuming FileUtilities is available.
        // val soundFolder = File(Config.modulesFolder, "SBO/assets")
        // UDesktop.browse(soundFolder.toURI())
        UChat.chat("Sound folder open action needs to be implemented with appropriate file access utilities.")
    }

    @Property(
        type = PropertyType.TEXT,
        name = "Inquisitor Spawn Sound",
        description = "Set the sound for inquisitor spawn (enter filename)",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    var inqSound = "expOrb"

    @Property(
        type = PropertyType.SLIDER,
        name = "Inquisitor Spawn Volume",
        description = "Set the volume for inquisitor spawn sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var inqVolume = 100

    @Property(
        type = PropertyType.TEXT,
        name = "Burrow Spawn Sound",
        description = "Set the sound for burrow spawn (enter filename)",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    var burrowSound = ""

    @Property(
        type = PropertyType.SLIDER,
        name = "Burrow Spawn Volume",
        description = "Set the volume for burrow spawn sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var burrowVolume = 50

    @Property(
        type = PropertyType.TEXT,
        name = "Chimera Drop Sound",
        description = "Set the sound for chimera drop (enter filename)",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    var chimSound = ""

    @Property(
        type = PropertyType.SLIDER,
        name = "Chimera Drop Volume",
        description = "Set the volume for chimera drop sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var chimVolume = 50

    @Property(
        type = PropertyType.TEXT,
        name = "Relic Drop Sound",
        description = "Set the sound for relic drop (enter filename)",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    var relicSound = ""

    @Property(
        type = PropertyType.SLIDER,
        name = "Relic Drop Volume",
        description = "Set the volume for relic drop sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var relicVolume = 50

    @Property(
        type = PropertyType.TEXT,
        name = "Daedalus Stick Drop Sound",
        description = "Set the sound for stick drop (enter filename)",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    var stickSound = ""

    @Property(
        type = PropertyType.SLIDER,
        name = "Daedalus Stick Drop Volume",
        description = "Set the volume for stick drop sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var stickVolume = 50

    @Property(
        type = PropertyType.TEXT,
        name = "Shelmet, Plushie and Remedies Drop Sound",
        description = "Set the sound for Shelmet, Plushie and Remedis (enter filename)",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    var sprSound = "mfsound"

    @Property(
        type = PropertyType.SLIDER,
        name = "Shelmet, Plushie and Remedies Drop Volume",
        description = "Set the volume for Shelmet, Plushie and Remedis sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var sprVolume = 50

    @Property(
        type = PropertyType.SLIDER,
        name = "Achievement Sound Volume",
        description = "Set the volume for the achievement sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var achievementVolume = 50

    // Debug
    @Property(
        type = PropertyType.SELECTOR,
        name = "Test Property Sound",
        description = "Select a custom sound for a specific item",
        category = "Customization",
        subcategory = "Sound Settings",
        options = ["none"] // This will be dynamically populated in the init block or a separate function
    )
    var customSound = 0

    @Property(
        type = PropertyType.SLIDER,
        name = "Test Property Sound Volume",
        description = "Set the volume for the custom sound",
        category = "Customization",
        subcategory = "Sound Settings",
        min = 0,
        max = 100
    )
    var customVolume = 50

    @Property(
        type = PropertyType.BUTTON,
        name = "Play Test Sound",
        description = "Plays the selected sound to test it",
        placeholder = "Play Sound",
        category = "Customization",
        subcategory = "Sound Settings"
    )
    fun playTestSound() {
        UChat.chat("/playsbotestsound")
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "Test Features",
        description = "Enable test features",
        category = "Debug",
    )
    var testFeatures = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Always Diana Mayor",
        description = "Its always Diana, no need to check for mayor, perks or spade",
        category = "Debug",
    )
    var itsAlwaysDiana = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Always in Skyblock",
        description = "you are always in skyblock, just for trolls and debug",
        category = "Debug",
    )
    var alwaysInSkyblock = false

    // Credits/Infos
    @Property(
        type = PropertyType.BUTTON,
        name = "Discord",
        description = "Open Tickets for help/bug reports",
        placeholder = "Click Me",
        category = "Credits/Infos",
        subcategory = "Infos",
    )
    fun openDiscord() {
        UDesktop.browse(java.net.URI("https://discord.gg/QvM6b9jsJD"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Github",
        description = "View our progress on github",
        placeholder = "Click Me",
        category = "Credits/Infos",
        subcategory = "Infos",
    )
    fun openGithub() {
        UDesktop.browse(java.net.URI("https://github.com/SkyblockOverhaul/SBO"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Patreon",
        description = "Support our development ☕",
        placeholder = "Click Me",
        category = "Credits/Infos",
        subcategory = "Infos",
    )
    fun openPatreon() {
        UDesktop.browse(java.net.URI("https://www.patreon.com/Skyblock_Overhaul"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Website",
        description = "Explore our website for tracking Magic Find upgrades and Attribute upgrades",
        placeholder = "Click Me",
        category = "Credits/Infos",
        subcategory = "Infos",
    )
    fun openWebsite() {
        UDesktop.browse(java.net.URI("https://skyblockoverhaul.com/"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Skyhanni",
        description = "Diana guess",
        placeholder = "Click Me",
        category = "Credits/Infos",
        subcategory = "Credits",
    )
    fun openSoopyV2() {
        UDesktop.browse(java.net.URI("https://github.com/hannibal002/SkyHanni/tree/beta"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "VolcAddons",
        description = "(Render waypoints and some utils)",
        placeholder = "Click Me",
        category = "Credits/Infos",
        subcategory = "Credits",
    )
    fun openVolcAddons() {
        UDesktop.browse(java.net.URI("https://www.chattriggers.com/modules/v/VolcAddons"))
    }

    init {
        initialize()
        val configClass = this.javaClass

        addDependency(configClass.getDeclaredField("dianaAdvancedBurrowGuess"), configClass.getDeclaredField("dianaBurrowGuess"))
        addDependency(configClass.getDeclaredField("dontWarpIfBurrowNearby"), configClass.getDeclaredField("dianaBurrowWarp"))
        addDependency(configClass.getDeclaredField("warpDelayTime"), configClass.getDeclaredField("warpDelay"))
        addDependency(configClass.getDeclaredField("dianaMobTrackerView"), configClass.getDeclaredField("dianaTracker"))
        addDependency(configClass.getDeclaredField("dianaLootTrackerView"), configClass.getDeclaredField("dianaTracker"))
        addDependency(configClass.getDeclaredField("inquisTracker"), configClass.getDeclaredField("dianaTracker"))
        addDependency(configClass.getDeclaredField("fourEyedFish"), configClass.getDeclaredField("dianaTracker"))
        addDependency(configClass.getDeclaredField("inqWarpKey"), configClass.getDeclaredField("inqWaypoints"))
        addDependency(configClass.getDeclaredField("inquisDetectCopy"), configClass.getDeclaredField("inquisDetect"))
        addDependency(configClass.getDeclaredField("inqCylinder"), configClass.getDeclaredField("inqCircle"))
        addDependency(configClass.getDeclaredField("lootAnnouncerPrice"), configClass.getDeclaredField("lootAnnouncerScreen"))
        addDependency(configClass.getDeclaredField("warpCommand"), configClass.getDeclaredField("partyCommands"))
        addDependency(configClass.getDeclaredField("allinviteCommand"), configClass.getDeclaredField("partyCommands"))
        addDependency(configClass.getDeclaredField("transferCommand"), configClass.getDeclaredField("partyCommands"))
        addDependency(configClass.getDeclaredField("moteCommand"), configClass.getDeclaredField("partyCommands"))
        addDependency(configClass.getDeclaredField("carrotCommand"), configClass.getDeclaredField("partyCommands"))
        addDependency(configClass.getDeclaredField("bridgeBotName"), configClass.getDeclaredField("formatBridgeBot"))
        addDependency(configClass.getDeclaredField("carnivalZombieLine"), configClass.getDeclaredField("carnivalZombie"))
        addDependency(configClass.getDeclaredField("goldenFishNotification"), configClass.getDeclaredField("goldenFishTimer"))
        addDependency(configClass.getDeclaredField("flareExpireAlert"), configClass.getDeclaredField("flareTimer"))
        addDependency(configClass.getDeclaredField("notInRangeSetting"), configClass.getDeclaredField("flareTimer"))
        addDependency(configClass.getDeclaredField("highlightAllSlots"), configClass.getDeclaredField("fossilSolver"))
    }

}