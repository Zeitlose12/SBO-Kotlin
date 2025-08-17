package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import net.sbo.mod.utils.waypoint.AdditionalHubWarps



object Diana : CategoryKt("Diana") {
    enum class SettingDiana {
        INSTASELL, SELLOFFER
    }

    init {
        separator {
            this.title = "Diana Burrows"
        }
    }

    var dianaBurrowGuess by boolean(true) {
        this.name = Translated("Diana Burrow Guess")
        this.description = Translated("Guess the burrow location. Needs Driping Lava Partciles and set /particlequality to Extreme for more accuracy")
    }

    var dianaBurrowDetect by boolean(true) {
        this.name = Translated("Diana Burrow Detection")
        this.description = Translated("Detects Diana burrows | to reset waypoints /sboclearburrows")
    }

    init {
        separator {
            this.title = "Diana Warp"
        }
    }

    var allowedWarps by select(AdditionalHubWarps.CRYPT, AdditionalHubWarps.DA) {
        this.name = Translated("Add Warps")
        this.description = Translated("Select the warps you want to be able to warp to with the guess and inquisitor warp keys.")
    }

    var dontWarpIfBurrowClose by boolean(true) {
        this.name = Translated("Don't Warp If Burrow Close")
        this.description = Translated("If enabled, the warp key will not warp you if you are within 60 blocks of a burrow")
    }

    var warpDiff by int(10) {
        this.range = 0..100
        this.slider = true
        this.name = Translated("Warp Block Difference")
        this.description = Translated("The additional block difference to consider when warping to a waypoint. (0 to disable)")
    }

    var warpDelay by int(0) {
        this.range = 0..1000
        this.slider = true
        this.name = Translated("Warp Delay")
        this.description = Translated("The delay bevor you can warp after guessing with spade. (0 to disable)")
    }

    init {
        separator {
            this.title = "Diana Tracker"
        }
    }
    var fourEyedFish by boolean(true) {
        this.name = Translated("Four-Eyed Fish")
        this.description = Translated("Set if you have a Four-Eyed Fish on your griffin pet")
    }

    var lootAnnouncerChat by boolean(true) {
        this.name = Translated("Rare Drop Announcer")
        this.description = Translated("Announces relic/shelmet/plushie/remedies in chat")
    }

    var lootAnnouncerScreen by boolean(false) {
        this.name = Translated("Loot Screen Announcer")
        this.description = Translated("Announces chimera/stick/relic on screen")
    }

    var lootAnnouncerPrice by boolean(true) {
        this.name = Translated("Show Price Title")
        this.description = Translated("Shows chimera/stick/relic price as a subtitle on screen")
        this.condition = { lootAnnouncerScreen }
    }

    var lootAnnouncerParty by boolean(false) {
        this.name = Translated("Loot Party Announcer")
        this.description = Translated("Announces chimera/stick/relic and Shelmet/Plushie/Remedies (only when dropped from Inquisitor) in party chat")
    }

    var sendSinceMessage by boolean(true) {
        this.name = Translated("Stats Message")
        this.description = Translated("Sends the chat Message with stat: [SBO] Took 120 Mobs to get a Inquis!")
    }

    var bazaarSettingDiana by enum(SettingDiana.SELLOFFER) {
        this.name = Translated("Bazaar Setting")
        this.description = Translated("Bazaar setting to set the price for loot")
    }

    init {
        separator {
            this.title = "Diana Waypoints"
        }
    }

    var allWaypointsAreInqs by boolean(false) {
        this.name = Translated("All Waypoints From Chat Are Inqs")
        this.description = Translated("All coordinates from chat are considered Inquisitor waypoints (only works in Hub and during Diana event)")
    }

    var guessLine by boolean(true) {
        this.name = Translated("Guess Line")
        this.description = Translated("Draws line for guess, Disable View Bobbing in controls if its buggy")
    }

    var inqLine by boolean(true) {
        this.name = Translated("Inquisitor Line")
        this.description = Translated("Draws line for inquisitor, Disable View Bobbing in controls if its buggy")
    }

    var burrowLine by boolean(true) {
        this.name = Translated("Burrow Line")
        this.description = Translated("Draws line for burrow, Disable View Bobbing in controls if its buggy")
    }

    var dianaLineWidth by int(5) {
        this.range = 1..20
        this.slider = true
        this.name = Translated("Diana Line Width")
        this.description = Translated("The width of the lines drawn for Diana waypoints")
    }

    var removeGuessDistance by int(0) {
        this.range = 0..20
        this.slider = true
        this.name = Translated("Remove Guess When Close")
        this.description = Translated("Removes the guess waypoint when you are within this distance of it (0 to disable)")
    }

    init {
        separator {
            this.title = "Other"
        }
    }

    var chimMessageBool by boolean(false) {
        this.name = Translated("Chim Message")
        this.description = Translated("Enables custom chim message")
    }

    var customChimMessage by strings("&6[SBO] &6&lRARE DROP! &d&lChimera! &b{mf} &b#{amount}") {
        this.name = Translated("Custom Chim Message Text")
        this.description = Translated("use: {mf} for MagicFind, {amount} for drop Amount this event and {percentage} for chimera/inquis ratio.")
    }
}