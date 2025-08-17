package net.sbo.mod.utils.events

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object EventBus {
    private val listeners = ConcurrentHashMap<KClass<*>, MutableList<(Any) -> Unit>>()
    private val simpleListeners = ConcurrentHashMap<String, MutableList<(Any?) -> Unit>>()

    /**
     * Registers a listener for a specific event type.
     * The callback is a function that takes the event data as an argument.
     *
     * @param T The type of the event data.
     * @param eventType The class of the event data, e.g., `MyEventData::class`.
     * @param callback The function to be executed when the event is emitted.
     */
    fun <T : Any> on(eventType: KClass<T>, callback: (T) -> Unit) {
        val callbacks = listeners.computeIfAbsent(eventType) { mutableListOf() }

        @Suppress("UNCHECKED_CAST")
        callbacks.add(callback as (Any) -> Unit)
    }

    /**
     * Emits an event with associated data.
     * All registered listeners for the event's type will be notified.
     *
     * @param event The event object containing the data.
     */
    fun emit(event: Any) {
        val callbacks = listeners[event::class]
        callbacks?.forEach { callback ->
            callback(event)
        }
    }

    /**
     * Clears all listeners for a specific event type.
     *
     * @param eventType The class of the event data to clear, e.g., `MyEventData::class`.
     */
    fun clear(eventType: KClass<*>) {
        listeners.remove(eventType)
    }

    // --- Simple String-based API ---

    /**
     * Registers a listener for an event identified by a string.
     * The callback is a function that takes optional event data.
     *
     * @param eventName The name of the event as a string.
     * @param callback The function to be executed, accepting optional data.
     */
    fun on(eventName: String, callback: (Any?) -> Unit) {
        simpleListeners.computeIfAbsent(eventName) { mutableListOf() }.add(callback)
    }

    /**
     * Emits an event identified by a string, with optional data.
     *
     * @param eventName The name of the event to emit.
     * @param data Optional data to pass to the listeners.
     */
    fun emit(eventName: String, data: Any? = null) {
        simpleListeners[eventName]?.forEach { callback ->
            callback(data)
        }
    }

    /**
     * Clears all listeners for a specific string-based event.
     *
     * @param eventName The name of the event to clear.
     */
    fun clear(eventName: String) {
        simpleListeners.remove(eventName)
    }
}