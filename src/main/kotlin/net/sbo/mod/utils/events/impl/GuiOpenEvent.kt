package net.sbo.mod.utils.events.impl

import net.minecraft.client.gui.screen.Screen
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class GuiOpenEvent(val screen: Screen, val ci: CallbackInfo)