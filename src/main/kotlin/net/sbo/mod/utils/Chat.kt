package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin.mc
import net.minecraft.text.Text
import net.minecraft.text.Texts.toText
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.util.Formatting

object Chat {

    /**
     * Sends a command to the server.
     * This correctly simulates a player typing a command.
     * @param command The command to send, without the leading slash.
     */
    fun command(command: String) {
        if (command.startsWith("/")) {
            mc.networkHandler?.sendChatCommand(command.substring(1));
        }
        else {
            mc.networkHandler?.sendChatCommand(command)
        }
    }

    /**
     * Shows a local chat message only visible to the player.
     * @param string The message to display in the chat.
     */
    fun chat(string: String) {
        mc.inGameHud.chatHud.addMessage(Text.of(string))
    }

    /**
     * Shows a local chat message only visible to the player.
     * @param text The message to display in the chat.
     */
    fun chat(text: Text) {
        mc.inGameHud.chatHud.addMessage(text)
    }

    /**
     * Sends a message to the server chat.
     * @param message The message to send.
     */
    fun say(message: String) {
        mc.networkHandler?.sendChatMessage(message)
    }

    /**
     * Sends a clickable message to the player.
     *
     * @param message The text to display in the message.
     * @param hover The text to show when the player hovers over the message.
     * @param onClick The code to execute when the player clicks the message.
     */
    fun clickableChat(
        message: String,
        hover: String,
        onClick: () -> Unit,
    ) {
        val actionId = ClickActionManager.registerAction(onClick)
        val hoverText = Text.literal(hover).formatted(Formatting.YELLOW)

        val clickEvent = ClickEvent.RunCommand("/__sbo_run_clickable_action $actionId")
        val hoverEvent = HoverEvent.ShowText(hoverText)

        val styledText = Text.literal(message).setStyle(
            Style.EMPTY
                .withClickEvent(clickEvent)
                .withHoverEvent(hoverEvent)
                .withUnderline(true)
        )

        mc.inGameHud.chatHud.addMessage(styledText)
    }

    /**
     * Gets a message that fills one line of chat by repeating the separator.
     * @param separator The string to repeat. Defaults to "-".
     * @return The message string that fills the chat line.
     */
    fun getChatBreak(separator: String = "-", colorcodes: String = "§b"): String {
        // Return early if the separator is empty to avoid errors
        if (separator.isEmpty()) {
            return ""
        }
        val textRenderer = mc.textRenderer
        val chatWidth = mc.inGameHud.chatHud.width
        val separatorWidth = textRenderer.getWidth(separator)

        if (separatorWidth <= 0) {
            return ""
        }

        val repeatCount = chatWidth / separatorWidth
        return colorcodes + separator.repeat(repeatCount)
    }
}