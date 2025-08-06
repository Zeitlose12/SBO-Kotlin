package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin.mc
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.text.Texts.toText

object Chat {
    private val client: MinecraftClient
        get() = mc

    /**
     * Sends a command to the server.
     * This correctly simulates a player typing a command.
     * @param command The command to send, without the leading slash.
     */
    fun command(command: String) {
        if (command.startsWith("/")) {
            client.networkHandler?.sendChatCommand(command.substring(1));
        }
        else {
            client.networkHandler?.sendChatCommand(command)
        }
    }

    /**
     * Shows a local chat message only visible to the player.
     * @param string The message to display in the chat.
     */
    fun chat(string: String) {
        client.inGameHud.chatHud.addMessage(Text.of(string))
    }

    /**
     * Shows a local chat message only visible to the player.
     * @param text The message to display in the chat.
     */
    fun chat(text: Text) {
        client.inGameHud.chatHud.addMessage(text)
    }
}