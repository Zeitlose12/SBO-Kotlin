package net.sbo.mod.event

import net.minecraft.text.Text

typealias ChatMessageListener = (Text) -> Unit

object ChatMessageEventManager {
    private val listeners = mutableListOf<ChatMessageListener>()

    fun register(listener: ChatMessageListener) {
        listeners.add(listener)
    }

    fun trigger(message: Text) {
        listeners.forEach { it(message) }
    }
}