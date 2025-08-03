package net.sbo.mod.utils

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

/**
 * Ein zentrales Objekt für die Registrierung von Mod-Events.
 */
object Register {
    /**
     * Registriert einen einfachen Client-seitigen Command.
     *
     * @param name Der Name des Commands.
     * @param action Die Aktion, die ausgeführt wird, wenn der Command aufgerufen wird.
     */
    fun command(name: String, action: (CommandContext<FabricClientCommandSource>) -> Int) {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal(name)
                    .executes(action)
            )
        }
    }
}