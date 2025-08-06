package net.sbo.mod.utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.Chat.chat
import org.lwjgl.glfw.GLFW

object SboKeyBinds {

    val guessWarpKey: KeyBinding = KeyBinding(
    "key.sbo-kotlin.guess_warp",
    InputUtil.Type.KEYSYM,
    GLFW.GLFW_KEY_UNKNOWN,
    "key.category.sbo-kotlin.keybinds"
    )

    val inqWarpKey: KeyBinding = KeyBinding(
        "key.sbo-kotlin.inq_warp",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "key.category.sbo-kotlin.keybinds"
    )

    val generalWarpKey: KeyBinding = KeyBinding(
        "key.sbo-kotlin.general_warp",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "key.category.sbo-kotlin.keybinds"
    )


    fun register() {
        KeyBindingHelper.registerKeyBinding(guessWarpKey)
        KeyBindingHelper.registerKeyBinding(inqWarpKey)
    }

    fun registerKeyBindListener() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            while (guessWarpKey.wasPressed()) {
                chat("Guess Warp Key Pressed! key: ${guessWarpKey.boundKey.code}")
                if (Diana.guessWarp != guessWarpKey.boundKey.code) Diana.guessWarp = guessWarpKey.boundKey.code
            }

            while (inqWarpKey.wasPressed()) {
                chat("Inquisitor Warp Key Pressed!")
                if (Diana.inqWarp != inqWarpKey.boundKey.code) Diana.inqWarp = inqWarpKey.boundKey.code
            }

            while (generalWarpKey.wasPressed()) {
                chat("General Warp Key Pressed!")
                if (Diana.generalWarp != generalWarpKey.boundKey.code) Diana.generalWarp = generalWarpKey.boundKey.code
            }
        })
    }
}