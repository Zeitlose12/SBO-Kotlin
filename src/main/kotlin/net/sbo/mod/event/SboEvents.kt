package net.sbo.mod.event

import java.lang.reflect.Method

/**
 * Central object for managing events.
 * It dynamically registers and unregisters event listeners.
 */
object SboEvents {

    private val listeners: MutableMap<Class<out SboEvent>, EventListeners> = mutableMapOf()
    private val handlers: MutableMap<Class<out SboEvent>, EventHandler<out SboEvent>> = mutableMapOf()

    /**
     * Retrieves the event handler for a given event type.
     * If an handler doesn't exist, a new one is created and cached.
     *
     * @param event The class of the event.
     * @return The cached or newly created event handler.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : SboEvent> getEventHandler(event: Class<T>): EventHandler<T> = handlers.getOrPut(event) {
        EventHandler(
            event,
            getEventClasses(event).mapNotNull { listeners[it] }.flatMap(EventListeners::getListeners),
        )
    } as EventHandler<T>

    /**
     * Registers all event-handling methods from an object instance.
     * Methods must be annotated with @HandleEvent.
     *
     * @param instance The object to register.
     */
    fun register(instance: Any) {
        instance.javaClass.declaredMethods.forEach {
            registerMethod(it, instance)
        }
    }

    /**
     * Unregisters all event-handling methods associated with an object instance.
     *
     * @param instance The object to unregister.
     */
    fun unregister(instance: Any) {
        instance.javaClass.declaredMethods.forEach(::unregisterMethod)
    }

    /**
     * Registers a single method as an event listener.
     *
     * @param method The method to register.
     * @param instance The instance the method belongs to.
     */
    private fun registerMethod(method: Method, instance: Any) {
        val (options, eventTypes) = getEventData(method) ?: return
        eventTypes.forEach { eventType ->
            listeners.getOrPut(eventType) { EventListeners(eventType) }
                .addListener(method, instance, options)
        }
    }

    /**
     * Unregisters a single method from the listener list.
     *
     * @param method The method to unregister.
     */
    private fun unregisterMethod(method: Method) {
        val (_, eventTypes) = getEventData(method) ?: return
        eventTypes.forEach { event ->
            unregisterHandler(event)
            listeners.values.forEach { it.removeListener(method) }
        }
    }

    /**
     * Removes an EventHandler from the cache, forcing it to be recreated.
     * This ensures the handler list is up-to-date after unregistering a listener.
     *
     * @param clazz The class of the event whose handler should be invalidated.
     */
    private fun unregisterHandler(clazz: Class<out SboEvent>) {
        this.handlers.keys.removeIf { it.isAssignableFrom(clazz) }
    }


    /**
     * Analyzes a method for the @HandleEvent annotation and extracts the event types.
     *
     * @param method The method to analyze.
     * @return A pair containing the annotation options and a list of event classes.
     */
    @Suppress("UNCHECKED_CAST")
    private fun getEventData(method: Method): Pair<HandleEvent, List<Class<out SboEvent>>>? {
        val options = method.getAnnotation(HandleEvent::class.java) ?: return null
        when (method.parameterCount) {
            1 -> {
                val eventType = method.parameterTypes.first()
                require(SboEvent::class.java.isAssignableFrom(eventType)) {
                    "Method ${method.name} parameter must be a subclass of SboEvent."
                }
                return options to listOf(eventType as Class<out SboEvent>)
            }

            0 -> {
                require(options.eventType != SboEvent::class || options.eventTypes.isNotEmpty()) {
                    "Method ${method.name} must have at least one event type specified in @HandleEvent."
                }
                return if (options.eventTypes.isNotEmpty()) {
                    options to options.eventTypes.map { it.java }
                } else {
                    options to listOf(options.eventType.java)
                }
            }
        }
        return null
    }

    /**
     * Retrieves all superclasses of an event up to the base SboEvent class.
     * This is used to allow listeners on a superclass to receive events from subclasses.
     *
     * @param clazz The starting event class.
     * @return A list containing the class itself and all relevant superclasses.
     */
    private fun getEventClasses(clazz: Class<*>): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()
        classes.add(clazz)

        var current = clazz
        while (current.superclass != null && current.superclass != SboEvent::class.java) {
            val superClass = current.superclass
            classes.add(superClass)
            current = superClass
        }
        return classes
    }
}