package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin.mc
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext
import net.sbo.mod.SBOKotlin.logger
import java.util.regex.Matcher
import java.util.regex.Pattern
import net.sbo.mod.utils.ChatHandler
import net.sbo.mod.utils.Helper.removeFormatting

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import gg.essential.universal.utils.toFormattedString
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.network.packet.Packet
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Utility object for registering events
 */
object Register {
    private val guiOpenActions = mutableListOf<(client: MinecraftClient, screen: Screen) -> Unit>()
    private val guiCloseActions = mutableListOf<(client: MinecraftClient, screen: Screen) -> Unit>()
    private val guiRenderActions = mutableListOf<(client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) -> Unit>()
    private val guiPostRenderActions = mutableListOf<(client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) -> Unit>()
    private val guiKeyActions = mutableListOf<(client: MinecraftClient, screen: Screen, key: Int, cir: CallbackInfoReturnable<Boolean>) -> Unit>()
    private val PacketReceivedActions = mutableListOf<(packet: Packet<*>) -> Unit>()

    fun runGuiOpenActions(client: MinecraftClient, screen: Screen) { guiOpenActions.forEach { action -> action(client, screen) } }
    fun runGuiCloseActions(client: MinecraftClient, screen: Screen) { guiCloseActions.forEach { action -> action(client, screen) } }
    fun runGuiRenderActions(client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) { guiRenderActions.forEach { action -> action(client, screen, context, mouseX, mouseY, delta) } }
    fun runGuiPostRenderActions( client: MinecraftClient, screen: Screen, context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) { guiPostRenderActions.forEach { action -> action(client, screen, context, mouseX, mouseY, delta) } }
    fun runGuiKeyActions(client: MinecraftClient, screen: Screen, key: Int, cir: CallbackInfoReturnable<Boolean>) { guiKeyActions.forEach { action -> action(client, screen, key, cir) } }
    fun runPacketReceivedActions(packet: Packet <*>) { PacketReceivedActions.forEach { action -> action(packet) } }
    /**
     * Registers a command with the specified name and aliases.
     * The action is executed when the command is invoked, with the provided arguments.
     *
     * @param name The name of the command.
     * @param aliases Optional aliases for the command.
     * @param action The action to execute when the command is invoked.
     */
    fun command(
        name: String,
        vararg aliases: String,
        action: (Array<String>) -> Unit
    ) {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

            fun createLiteral(commandName: String) =
                ClientCommandManager.literal(commandName)
                    .executes {
                        action(emptyArray())
                        1
                    }
                    .then(
                        ClientCommandManager.argument("args", StringArgumentType.greedyString())
                            .executes {
                                val argsString = StringArgumentType.getString(it, "args")
                                val args = argsString.split(' ').filter { s -> s.isNotEmpty() }.toTypedArray()
                                action(args)
                                1
                            }
                    )

            dispatcher.register(createLiteral(name))

            aliases.forEach { alias ->
                dispatcher.register(createLiteral(alias))
            }
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
     * Registers an event that listens for the client disconnecting from the server.
     * The action is executed when the client disconnects.
     */
    fun onDisconnect(action: () -> Unit) {
        ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
            action()
        }
    }

    /**
     * Registers an event that listens for chat messages.
     * The action receives the message as a `Text` object.
     */
    fun onChatMessage(
        action: (message: Text) -> Unit
    ) {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            action(message)
        }
    }

    /**
     * Registers an event that listens for chat messages that match a regex.
     * The action receives both the message and the regex match result for easy value extraction.
     *
     * @param regex The regular expression to filter messages with.
     * @param action The action to execute. It receives the message and the `MatchResult`.
     */
    fun onChatMessage(
        regex: Regex,
        noFormatting: Boolean = false,
        action: (message: Text, matchResult: MatchResult) -> Unit
    ) {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            var text = ""
            if (message.siblings.isEmpty()) {
                text = message.toFormattedString()
            } else {
                text = message.string
            }

            if (noFormatting) text = text.removeFormatting()

            regex.find(text)?.let { result ->
                action(message, result)
            }
        }
    }

    /**
     * Registers an event that listens for chat messages that match a list of regex patterns.
     * The action receives both the message and the regex match result for easy value extraction.
     *
     * @param regexes A list of regular expressions to filter messages with.
     * @param noFormatting If true, removes formatting from the message before matching.
     * @param action The action to execute. It receives the message and the `MatchResult
     */
    fun onChatMessage(
        regexes: List<Regex>,
        noFormatting: Boolean = false,
        action: (message: Text, matchResult: MatchResult) -> Unit
    ) {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            var text = ""
            if (message.siblings.isEmpty()) {
                text = message.toFormattedString()
            } else {
                text = message.string
            }

            if (noFormatting) text = text.removeFormatting()

            regexes.forEach { regex ->
                regex.find(text)?.let { result ->
                    action(message, result)
                }
            }
        }
    }

    /**
     * Registers a listener for chat messages that match a given criteria string.
     *
     * @param criteria The pattern to match. Use ${variable} for placeholders.
     * @param action A lambda function to execute upon a match. It receives a list of the captured strings.
     */
    fun onChatMessage(
        criteria: String,
        noFormatting: Boolean,
        action: (message: Text, capturedParts: List<String>) -> Unit
    ) {
        val processedCriteria = criteria.replace('&', '§')
        val placeholderRegex = Regex("\\{[^}]+}")
        val literalParts = processedCriteria.split(placeholderRegex)
        val finalRegexPattern = literalParts
            .joinToString(separator = "(.*?)") { Regex.escape(it) }
        val criteriaRegex = Regex(finalRegexPattern)

        ClientReceiveMessageEvents.GAME.register { message: Text, _ ->
            var text = ""
            if (message.siblings.isEmpty()) {
                text = message.toFormattedString()
            } else {
                text = message.string
            }

            if (noFormatting) text = text.removeFormatting()

            val matchResult = criteriaRegex.matchEntire(text)

            if (matchResult != null) {
                val captured = matchResult.groupValues.drop(1)

                action(message, captured)
            }
        }
    }

    fun onChatMessageCancable(
        regex: Pattern,
        action: (message: Text, matchResult: Matcher) -> Boolean
    ) {
        ChatHandler.registerHandler(regex, action)
    }

    /**
     * Registers an event that listens for chat messages containing a specific string.
     * The action receives the message as a `Text` object.
     * This is mainly used for testing purposes
     *
     * @param criteria The string to search for in chat messages.
     * @param action The action to execute when a matching message is found.
     */
    fun chatMessageNormal(criteria: String, action: (message: Text) -> Unit) {
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
            action(mc)
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
    fun onGuiKey(action: (client: MinecraftClient, screen: Screen, key: Int, cir: CallbackInfoReturnable<Boolean>) -> Unit) {
        guiKeyActions.add(action)
    }

    fun onPacketReceived(action: (packet: Packet<*>) -> Unit) {
        PacketReceivedActions.add(action)
    }
}