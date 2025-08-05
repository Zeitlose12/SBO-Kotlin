package net.sbo.mod.utils

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext

/**
 * Utility object for registering events
 */
object Register {
    private val guiOpenActions = mutableListOf<(client: MinecraftClient, screen: Screen) -> Unit>()
    private val guiCloseActions = mutableListOf<(client: MinecraftClient, screen: Screen) -> Unit>()
    private val guiRenderActions = mutableListOf<(client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) -> Unit>()
    private val guiPostRenderActions = mutableListOf<(client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) -> Unit>()
    private val guiKeyActions = mutableListOf<(client: MinecraftClient, screen: Screen, key: Int) -> Unit>()

    fun runGuiOpenActions(client: MinecraftClient, screen: Screen) { guiOpenActions.forEach { action -> action(client, screen) } }
    fun runGuiCloseActions(client: MinecraftClient, screen: Screen) { guiCloseActions.forEach { action -> action(client, screen) } }
    fun runGuiRenderActions(client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) { guiRenderActions.forEach { action -> action(client, screen, context, mouseX, mouseY, delta) } }
    fun runGuiPostRenderActions( client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) { guiPostRenderActions.forEach { action -> action(client, screen, context, mouseX, mouseY, delta) } }
    fun runGuiKeyActions(client: MinecraftClient, screen: Screen, key: Int) { guiKeyActions.forEach { action -> action(client, screen, key) } }

    /**
     * Registers a client command with the specified name and action.
     * @param name The name of the command to register.
     * @param action The action to execute when the command is invoked.
     */
    fun command(name: String, action: (CommandContext<FabricClientCommandSource>) -> Int) {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal(name)
                    .executes(action)
            )
        }
    }

    /**
     * Registers a tick event that executes an action every specified number of ticks.
     * @param interval The number of ticks after which the action should be executed.
     * @param action The action to execute on each tick.
     */
    fun onTick(interval: Int, action: (MinecraftClient) -> Unit) {
        var tickCounter = 0
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            tickCounter++
            if (tickCounter >= interval) {
                action(client)
                tickCounter = 0
            }
        }
    }

    /**
     * Registers an event that listens for chat messages and executes an action if the message meets the criteria.
     * @param criteria Optional string to filter messages. If null, all messages will trigger the action.
     * @param action The action to execute when a chat message is received that meets the criteria.
     */
    fun onChatMessage(criteria: String, action: (message: Text) -> Unit) {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            if (message.string.contains(criteria)) {
                action(message)
            }
        }
    }

    /**
     * Registers an event that listens for world load events and executes an action.
     * @param action The action to execute when a world is loaded.
     */
    fun onWorldChange(action: (client: MinecraftClient) -> Unit) {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, _ ->
            action(MinecraftClient.getInstance())
        }
    }

    /**
     * Registers an event that listens for GUI open events and executes an action.
     * @param action The action to execute when a GUI is opened.
     */
    fun onGuiOpen(action: (client: MinecraftClient, screen: Screen) -> Unit) {
        guiOpenActions.add(action)
    }

    /**
     * Registers an event that listens for GUI close events and executes an action.
     * @param action The action to execute when a GUI is closed.
     */
    fun onGuiClose(action: (client: MinecraftClient, screen: Screen) -> Unit) {
        guiCloseActions.add(action)
    }

    /**
     * Registers an event that listens for GUI render events and executes an action.
     * @param action The action to execute when a GUI is rendered.
     */
    fun onGuiRender(action: (client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) -> Unit) {
        guiRenderActions.add(action)
    }

    /**
     * Registers an event that listens for GUI post-render events and executes an action.
     * @param action The action to execute after a GUI is rendered.
     */
    fun onGuiPostRender(action: (client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) -> Unit) {
        guiPostRenderActions.add(action)
    }

    /**
     * Registers an event that listens for key presses in the GUI and executes an action.
     * @param action The action to execute when a key is pressed in the GUI.
     */
    fun onGuiKey(action: (client: MinecraftClient, screen: Screen, key: Int) -> Unit) {
        guiKeyActions.add(action)
    }
}