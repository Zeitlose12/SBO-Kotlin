package net.sbo.mod.event

import kotlin.reflect.KClass

/**
 * Annotation to mark methods as event listeners.
 * Methods annotated with this will be automatically registered and called when the corresponding event is posted.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class HandleEvent(

    val eventType: KClass<out SboEvent> = SboEvent::class,

    val eventTypes: Array<KClass<out SboEvent>> = [],

    val onlyOnSkyblock: Boolean = false,

    val priority: Int = 0,

    val receiveCancelled: Boolean = false,
) {
    companion object {
        const val HIGHEST = -2 // First to execute
        const val HIGH = -1
        const val LOW = 1
        const val LOWEST = 2 // Last to execute
    }
}