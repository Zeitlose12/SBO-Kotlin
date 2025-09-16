package net.sbo.mod.utils.events.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)


/**
 * Marks a function as an event listener for the Sbo event bus.
 *
 * The function must have a single parameter which is the event type.
 *
 * The function must be inside an object singleton.
 *
 * The function will be registered to the event bus at compile time.
 *
 * The function will be called when the event is fired.
 */
annotation class SboEvent