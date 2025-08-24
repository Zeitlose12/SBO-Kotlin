package net.sbo.mod.utils.chat

import net.minecraft.text.Text
import net.sbo.mod.settings.categories.Debug
import net.sbo.mod.utils.chat.ChatUtils.formattedString
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

object ChatHandler {

    // CopyOnWriteArrayList ist threadsicher für Iteration + gleichzeitige Änderungen
    private val messageHandlers = CopyOnWriteArrayList<ChatRule>()

    fun registerHandler(pattern: Pattern, action: (Text, Matcher) -> Boolean) {
        messageHandlers.add(ChatRule(pattern, action))
    }

    @Synchronized
    fun processMessage(message: Text): Boolean {
        val messageString = message.formattedString().replace("§r", "")
        if (messageString.contains("[SBO]")) return true
        if (Debug.debugMessages && !messageString.contains("❈ Defense")) {
            println("Processing chat message: $messageString")
        }

        var cancelled = false
        for (rule in messageHandlers) {
            val matcher = rule.pattern.matcher(messageString)
            if (matcher.find()) {
                if (rule.action(message, matcher)) {
                    cancelled = true
                }
            }
        }
        return cancelled
    }

    private data class ChatRule(
        val pattern: Pattern,
        val action: (Text, Matcher) -> Boolean
    )
}