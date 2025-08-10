package net.sbo.mod.utils

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object EventBus {
    private val listeners = ConcurrentHashMap<KClass<*>, MutableList<(Any) -> Unit>>()

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
}

// Example usage:

// 1. Define your event data classes. These should be data classes or similar.
data class UserLoggedInEvent(val userId: Int, val username: String)
data class ProductAddedToCartEvent(val productId: String, val quantity: Int)

fun main() {
    // 2. Register listeners for specific events.
    EventBus.on(UserLoggedInEvent::class) { event ->
        println("User logged in! ID: ${event.userId}, Username: ${event.username}")
    }

    EventBus.on(ProductAddedToCartEvent::class) { event ->
        println("Product added to cart! ID: ${event.productId}, Quantity: ${event.quantity}")
    }

    // You can have multiple listeners for the same event type.
    EventBus.on(UserLoggedInEvent::class) { event ->
        println("Another listener for UserLoggedInEvent: Welcome, ${event.username}!")
    }

    // 3. Emit events with data.
    println("--- Emitting a UserLoggedInEvent ---")
    EventBus.emit(UserLoggedInEvent(userId = 123, username = "Alice"))

    println("\n--- Emitting a ProductAddedToCartEvent ---")
    EventBus.emit(ProductAddedToCartEvent(productId = "book-456", quantity = 1))

    // 4. Clearing listeners.
    println("\n--- Clearing UserLoggedInEvent listeners ---")
    EventBus.clear(UserLoggedInEvent::class)

    println("--- Emitting a UserLoggedInEvent again (listeners should be gone) ---")
    EventBus.emit(UserLoggedInEvent(userId = 789, username = "Bob"))
}