package net.sbo.mod.utils

import gg.essential.universal.utils.toFormattedString
import net.minecraft.text.Text
import java.util.regex.Matcher
import java.util.regex.Pattern

object ChatHandler {

    private val messageHandlers = mutableListOf<ChatRule>()

    fun registerHandler(pattern: Pattern, action: (Text, Matcher) -> Boolean) {
        messageHandlers.add(ChatRule(pattern, action))
    }

    fun processMessage(message: Text): Boolean {
        val messageString = message.toFormattedString()
        messageHandlers.forEach { rule ->
            val matcher = rule.pattern.matcher(messageString)
            if (matcher.find()) {
                return rule.action(message, matcher)
            }
        }
        return true
    }

    private data class ChatRule(
        val pattern: Pattern,
        val action: (Text, Matcher) -> Boolean
    )
}