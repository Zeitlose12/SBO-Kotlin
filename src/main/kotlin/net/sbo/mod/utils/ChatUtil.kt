package net.sbo.mod.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object Chat {
    private val client: MinecraftClient
        get() = MinecraftClient.getInstance()

    /**
     * Sends a command to the server.
     * This correctly simulates a player typing a command.
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
     */
    fun chat(message: String) {
        client.inGameHud.chatHud.addMessage(Text.of(message))
    }
}