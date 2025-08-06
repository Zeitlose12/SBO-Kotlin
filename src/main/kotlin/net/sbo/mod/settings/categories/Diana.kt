package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.sbo.mod.utils.Chat.chat
import net.sbo.mod.utils.SboKeyBinds
import org.lwjgl.glfw.GLFW
import javax.swing.text.JTextComponent

object Diana : CategoryKt("Diana") {

    var allWaypointsAreInqs by boolean(false) {
        this.name = Translated("All Waypoints From Chat Are Inqs")
        this.description = Translated("All coordinates from chat are considered Inquisitor waypoints (only works in Hub and during Diana event)")
    }


    init {
        separator {
            this.title = "Diana Waypoints"
        }
    }

    var guessWarp by ObservableEntry(
        key(GLFW.GLFW_KEY_UNKNOWN) {
            this.name = Translated("Guess Warp Key")
            this.description = Translated("Sets the key to warp to the 'Guess' waypoint. Leave empty to disable.")
        }
    ) { oldKey, newKey ->
        chat("Waypoint Keybind changed from $oldKey to $newKey")
        val keyCode = InputUtil.Type.KEYSYM.createFromCode(newKey)
        SboKeyBinds.guessWarpKey.setBoundKey(keyCode)
        KeyBinding.updateKeysByCode()
    }

    var inqWarp by ObservableEntry(
        key(GLFW.GLFW_KEY_UNKNOWN) {
            this.name = Translated("Inquisitor Warp Key")
            this.description = Translated("Sets the key to warp to the newest 'Inquisitor' waypoint. Leave empty to disable.")
        }
    ) { oldKey, newKey ->
        chat("Inquisitor Keybind changed from $oldKey to $newKey")
        val keyCode = InputUtil.Type.KEYSYM.createFromCode(newKey)
        SboKeyBinds.inqWarpKey.setBoundKey(keyCode)
        KeyBinding.updateKeysByCode()
    }

    var generalWarp by ObservableEntry(
        key(GLFW.GLFW_KEY_UNKNOWN) {
            this.name = Translated("General Warp Key")
            this.description = Translated("Sets the key to warp to the newest 'General' waypoint. Leave empty to disable.")
        }
    ) { oldKey, newKey ->
        chat("General Keybind changed from $oldKey to $newKey")
        val keyCode = InputUtil.Type.KEYSYM.createFromCode(newKey)
        SboKeyBinds.generalWarpKey.setBoundKey(keyCode)
        KeyBinding.updateKeysByCode()
    }

    var guessLine by boolean(true) {
        this.name = Translated("Guess Line")
        this.description = Translated("Draws line for guess, Disable View Bobbing in controls if its buggy")
    }

    var removeGuessDistance by int(10) {
        this.range = 0..30
        this.slider = true
        this.name = Translated("Remove Guess When Close")
        this.description = Translated("Removes the guess waypoint when you are within this distance of it (0 to disable)")
    }



}