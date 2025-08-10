package net.sbo.mod.event

import java.util.concurrent.atomic.AtomicLong

/**
 * An event handler that manages and executes a list of listeners for a specific event type.
 *
 * @param T The type of the event, which must inherit from [SboEvent].
 */
class EventHandler<T : SboEvent> private constructor(
    val name: String,
    private val listeners: List<EventListeners.Listener>,
    private val canReceiveCancelled: Boolean,
) {

    private val invokeCount = AtomicLong(0L)

    /**
     * Secondary constructor that automatically derives the handler's name from the event class.
     *
     * @param event The class of the event.
     * @param listeners The list of listeners for this event handler.
     */
    constructor(event: Class<T>, listeners: List<EventListeners.Listener>) : this(
        (event.name.split(".").lastOrNull() ?: event.name).replace("$", "."),
        listeners.sortedBy { it.priority }.toList(),
        listeners.any { it.receiveCancelled },
    )

    /**
     * Posts an event to all registered listeners.
     *
     * @param event The event to be posted.
     * @param onError An optional callback to handle exceptions thrown by listeners.
     * @return `true` if the event was cancelled by a listener, `false` otherwise.
     */
    fun post(event: SboEvent, onError: ((Throwable) -> Unit)?): Boolean {
        invokeCount.incrementAndGet()
        if (listeners.isEmpty()) return false


        for (listener in listeners) {
            if (event is CancellableSboEvent && event.isCancelled && !listener.receiveCancelled) {
                continue
            }

            try {
                listener.invoker.accept(event)
            } catch (throwable: Throwable) {
                System.err.println("Error in EventHandler for '$name' at listener '${listener.name}':")
                throwable.printStackTrace()
            }

            if (event is CancellableSboEvent && event.isCancelled && !canReceiveCancelled) {
                break
            }
        }

        return (event as? CancellableSboEvent)?.isCancelled ?: false
    }
}