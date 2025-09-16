package net.sbo.mod.utils.events

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object EventBus {
    private val listeners = ConcurrentHashMap<KClass<*>, MutableList<(Any) -> Unit>>()

    /* Register a listener for a specific event type.
     * The callback will be invoked when an event of the specified type is emitted.
     */
    fun <T : Any> on(eventType: KClass<T>, callback: (T) -> Unit) {
        val callbacks = listeners.computeIfAbsent(eventType) { mutableListOf() }
        @Suppress("UNCHECKED_CAST")
        callbacks.add(callback as (Any) -> Unit)
    }

    /* Emit an event to all registered listeners for the event's type.
     * The event can be any object, and its class will be used to find matching listeners.
     */
    fun emit(event: Any) {
        val callbacks = listeners[event::class]
        callbacks?.forEach { callback -> callback(event) }
    }
}
