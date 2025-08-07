package net.sbo.mod.event

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Consumer

/**
 * A type alias for a function that checks an event against a condition.
 */
typealias SboEventPredicate = (event: SboEvent) -> Boolean

/**
 * Manages a list of event listeners for a specific event type.
 * Uses reflection to find and encapsulate event-handling methods.
 */
class EventListeners private constructor(val name: String) {

    private val listeners: MutableList<Listener> = mutableListOf()

    /**
     * Secondary constructor that derives the listener group name from the event class.
     *
     * @param event The event class.
     */
    constructor(event: Class<*>) : this((event.name.split(".").lastOrNull() ?: event.name).replace("$", "."))

    /**
     * Removes a specific listener instance.
     *
     * @param listener The listener instance to remove.
     */
    fun removeListener(listener: Any) {
        listeners.removeIf { it.invoker == listener }
    }

    /**
     * Adds a new listener created from a method and an instance.
     *
     * @param method The method that serves as the listener.
     * @param instance The object instance the method belongs to.
     * @param options The options from the [HandleEvent] annotation.
     */
    fun addListener(method: Method, instance: Any, options: HandleEvent) {
        require(Modifier.isPublic(method.modifiers)) {
            "Method ${method.name}() in ${instance.javaClass.name} must be public."
        }

        val name = buildListenerName(method)
        val eventConsumer = when (method.parameterCount) {
            0 -> createZeroParameterConsumer(method, instance)
            1 -> createSingleParameterConsumer(method, instance)
            else -> throw IllegalArgumentException("Method ${method.name} must have 0 or 1 parameters.")
        }

        listeners.add(Listener(name, eventConsumer, options))
    }

    /**
     * Generates a unique name for a listener based on its method signature.
     *
     * @param method The method to name.
     * @return The unique listener name.
     */
    private fun buildListenerName(method: Method): String {
        val paramTypesString = method.parameterTypes.joinTo(
            StringBuilder(),
            prefix = "(",
            postfix = ")",
            separator = ", ",
            transform = Class<*>::getName,
        ).toString()

        return "${method.declaringClass.name}.${method.name}$paramTypesString"
    }

    /**
     * Creates a consumer that executes a method with zero parameters.
     *
     * @param method The method to execute.
     * @param instance The instance to invoke the method on.
     * @return A consumer function.
     */
    private fun createZeroParameterConsumer(method: Method, instance: Any): (Any) -> Unit {
        return { _: Any -> method.invoke(instance) }
    }

    /**
     * Creates a consumer that executes a method with a single event parameter.
     *
     * @param method The method to execute.
     * @param instance The instance to invoke the method on.
     * @return A consumer function.
     */
    private fun createSingleParameterConsumer(method: Method, instance: Any): (Any) -> Unit {
        return { event -> method.invoke(instance, event) }
    }

    /**
     * Returns the list of registered listeners.
     *
     * @return An immutable list of listeners.
     */
    fun getListeners(): List<Listener> = listeners

    /**
     * Represents a single listener.
     *
     * @property name The unique name of the listener.
     * @property invoker The consumer that will be invoked with the event.
     * @property options The [HandleEvent] annotation options.
     */
    class Listener(
        val name: String,
        val invoker: Consumer<Any>,
        options: HandleEvent,
    ) {
        val priority: Int = options.priority
        val receiveCancelled: Boolean = options.receiveCancelled

        private val predicates: List<SboEventPredicate>

        /**
         * Determines if the listener should be invoked for a given event based on its predicates.
         *
         * @param event The event to check.
         * @return true if the listener should be invoked, false otherwise.
         */
        fun shouldInvoke(event: SboEvent): Boolean {
            return predicates.all { it(event) }
        }

        init {
            predicates = buildList {
                if (!receiveCancelled) {
                    add { event -> (event as? CancellableSboEvent)?.isCancelled != true }
                }
            }
        }
    }
}