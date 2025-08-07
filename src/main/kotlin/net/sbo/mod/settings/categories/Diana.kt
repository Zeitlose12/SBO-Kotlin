package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.sbo.mod.general.AdditionalHubWarps
import net.sbo.mod.general.additionalHubWarps
import net.sbo.mod.utils.Chat.chat
import net.sbo.mod.utils.SboKeyBinds
import org.lwjgl.glfw.GLFW
import javax.swing.text.JTextComponent
enum class SettingDiana {
    INSTASELL, SELLOFFER
}

object Diana : CategoryKt("Diana") {

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
        this.range = 0..30
        this.slider = true
        this.name = Translated("Remove Guess When Close")
        this.description = Translated("Removes the guess waypoint when you are within this distance of it (0 to disable)")
    }
}