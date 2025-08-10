package net.sbo.mod.event

import net.minecraft.client.gui.DrawContext
import net.sbo.mod.utils.DrawContextUtils

/**
 * The base class for all custom events.
 *
 * All methods that handle events must be annotated with @[HandleEvent].
 */
abstract class SboEvent protected constructor() {

    /**
     * Determines if the event has been cancelled.
     */
    var isCancelled: Boolean = false
        private set

    /**
     * Posts the event to all registered listeners.
     */
    fun post() = prePost(onError = null)

    /**
     * Posts the event to all registered listeners, with a custom error handler.
     *
     * @param onError A lambda function to execute if a listener throws an exception.
     */
    fun post(onError: (Throwable) -> Unit) = prePost(onError)

    /**
     * Internal method to handle the event posting process.
     *
     * @param onError An optional error handler.
     * @return true if the event was cancelled, otherwise false.
     */
    private fun prePost(onError: ((Throwable) -> Unit)?): Boolean {
        if (this is Rendering) {
            DrawContextUtils.setContext(this.context)
            val result = SboEvents.getEventHandler(javaClass).post(this, onError)
            DrawContextUtils.clearContext()
            return result
        }
        return SboEvents.getEventHandler(javaClass).post(this, onError)
    }

    /**
     * Interface for events that can be cancelled.
     */
    interface Cancellable {
        /**
         * Cancels the event, preventing further listeners from being called
         * unless they are configured to receive cancelled events.
         */
        fun cancel() {
            val event = this as SboEvent
            event.isCancelled = true
        }
    }

    /**
     * Interface for events related to GUI rendering.
     * These events carry a Minecraft [DrawContext].
     */
    interface Rendering {
        val context: DrawContext
    }
}