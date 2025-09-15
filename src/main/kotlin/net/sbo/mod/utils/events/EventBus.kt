package net.sbo.mod.utils.events

import net.sbo.mod.utils.events.annotations.Subscribe
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

object EventBus {
    private val listeners = ConcurrentHashMap<KClass<*>, MutableList<(Any) -> Unit>>()
    private val simpleListeners = ConcurrentHashMap<String, MutableList<(Any?) -> Unit>>()

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

    /* Clear all listeners for a specific event type.
     * This will remove all callbacks associated with the given event type.
     */
    fun clear(eventType: KClass<*>) {
        listeners.remove(eventType)
    }

    /* Simple string-based event system.
     * Allows registering and emitting events identified by a string name.
     */
    fun on(eventName: String, callback: (Any?) -> Unit) {
        simpleListeners.computeIfAbsent(eventName) { mutableListOf() }.add(callback)
    }

    /* Emit a string-based event.
     * All callbacks registered for the given event name will be invoked with the provided data.
     */
    fun emit(eventName: String, data: Any? = null) {
        simpleListeners[eventName]?.forEach { callback -> callback(data) }
    }

    /* Clear all listeners for a specific string-based event.
     * This will remove all callbacks associated with the given event name.
     */
    fun clear(eventName: String) {
        simpleListeners.remove(eventName)
    }

    /* Register all methods annotated with @Subscribe in the given listener object.
     * The listener can have multiple methods annotated with @Subscribe, each taking a single parameter.
     * When an event of the parameter's type is emitted, the corresponding method will be invoked.
     */
    fun register(listener: Any) {
        val clazz = listener::class
        for (method in clazz.members) {
            val annotation = method.findAnnotation<Subscribe>() ?: continue
            if (method.valueParameters.size != 1) {
                error("@Subscribe-annotated methods must have exactly one parameter ${method.name}")
            }
            val paramType = method.valueParameters[0].type.classifier as? KClass<*> ?: continue

            val callback: (Any) -> Unit = { event ->
                if (paramType.isInstance(event)) {
                    method.call(listener, event)
                }
            }
            listeners.computeIfAbsent(paramType) { mutableListOf() }.add(callback)
        }
    }
}
