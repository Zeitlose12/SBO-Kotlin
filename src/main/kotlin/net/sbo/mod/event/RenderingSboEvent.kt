package net.sbo.mod.event

import net.minecraft.client.gui.DrawContext
/**
 * Used if the event is related to GUI rendering, needs a context passed to it
 */
abstract class RenderingSboEvent(override val context: DrawContext) : SboEvent(), SboEvent.Rendering