// src/main/kotlin/net/sbo/mod/utils/ChatTrigger.kt

package net.sbo.mod.utils

import net.minecraft.text.Style
import net.minecraft.text.TextColor
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*
import java.util.regex.Pattern

class ChatTrigger(criteria: String, private val action: (message: String, args: List<String>) -> Unit) {

    private var shouldCancel = false
    private val criteriaPattern: Pattern

    init {
        val regexString = criteria.replace(Regex("\\{[^}]+}"), "(.+)")
        this.criteriaPattern = Pattern.compile(regexString)
    }

    fun cancelEvent(cancel: Boolean): ChatTrigger {
        this.shouldCancel = cancel
        return this
    }

    fun processMessage(message: String): Boolean {
        val matcher = criteriaPattern.matcher(message)

        if (matcher.matches()) {
            val args = (1..matcher.groupCount()).map { matcher.group(it) }
            action.invoke(message, args)
            return shouldCancel
        }
        return false
    }
}

object ChatUtils {
    private val colorToFormatChar: Map<TextColor, Formatting> = Formatting.entries.mapNotNull { format ->
        TextColor.fromFormatting(format)?.let { it to format }
    }.toMap()

    private fun getColorFormatChar(color: TextColor): Char? {
        val formatting = colorToFormatChar[color]
        return formatting?.code
    }

    private fun Style.getFormatCodes() = buildString {
        append("§r")

        if (this@getFormatCodes.isBold) append("§l")
        if (this@getFormatCodes.isItalic) append("§o")
        if (this@getFormatCodes.isUnderlined) append("§n")
        if (this@getFormatCodes.isStrikethrough) append("§m")
        if (this@getFormatCodes.isObfuscated) append("§k")

        this@getFormatCodes.color?.let(ChatUtils::getColorFormatChar)?.run { append("§").append(this) }
    }

    fun toFormattedString(text: Text): String {
        val builder = StringBuilder()

        text.visit(
            { style, content ->
                builder.append(style.getFormatCodes())
                builder.append(content)
                Optional.empty<Any>()
            },
            Style.EMPTY
        )
        return builder.toString()
    }
}