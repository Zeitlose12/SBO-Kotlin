package net.sbo.mod.utils.events

open class EventSubscriber {
    init {
        EventBus.register(this)
    }
}