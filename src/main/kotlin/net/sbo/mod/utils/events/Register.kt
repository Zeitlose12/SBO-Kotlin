package net.sbo.mod.utils.events

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.network.packet.Packet
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.utils.Helper.removeFormatting
import net.sbo.mod.utils.chat.ChatHandler
import net.sbo.mod.utils.chat.ChatUtils.formattedString
import net.sbo.mod.utils.events.TickScheduler.ScheduledTask
import net.sbo.mod.utils.events.TickScheduler.tasks
import net.sbo.mod.utils.events.impl.GuiOpenEvent
import net.sbo.mod.utils.events.impl.PacketReceiveEvent
import net.sbo.mod.utils.events.impl.PacketSendEvent
import net.sbo.mod.utils.events.impl.PlayerInteractEvent
import net.sbo.mod.utils.events.impl.GuiKeyEvent
import net.sbo.mod.utils.events.impl.GuiRenderEvent
import net.sbo.mod.utils.events.impl.GuiPostRenderEvent
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Utility object for registering events
 */
object Register {
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
     * @param tick The number of ticks after which the action should be executed.
     * @param action The action to execute. It receives a lambda to unregister itself.
     */
    fun onTick(tick: Int, action: (unregister: () -> Unit) -> Unit) {
        tasks += ScheduledTask(tick, action)
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
            var text = message.formattedString()

            if (noFormatting) text = text.removeFormatting()

            regex.find(text)?.let { result ->
                action(message, result)
            }
        }
    }

    /**
     * Registers an event that listens for chat messages that match a regex.
     * The action receives both the message and the regex matcher for easy value extraction.
     * If the action returns true, the message will be cancelled (not displayed in chat).
     *
     * @param regex The regular expression to filter messages with.
     * @param action The action to execute. It receives the message and the `Matcher`.
     */
    fun onChatMessageCancable(
        regex: Pattern,
        action: (message: Text, matchResult: Matcher) -> Boolean
    ) {
        ChatHandler.registerHandler(regex, action)
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
    fun onGuiOpen(action: (screen: Screen, ci: CallbackInfo) -> Unit) {
        EventBus.on(GuiOpenEvent::class) { event ->
            val screen = event.screen
            action(screen, event.ci)
        }
    }

    /**
     * Registers an event that listens for GUI close events and executes an action.
     * @param action The action to execute when a GUI is closed.
     */
    fun onGuiClose(action: (screen: Screen) -> Unit) {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenEvents.remove(screen).register {
                action(screen)
            }
        }
    }

    /**
     * Registers an event that listens for GUI render events and executes an action.
     * @param action The action to execute when a GUI is rendered.
     */
    fun onGuiRender(action: (GuiRenderEvent) -> Unit) {
        EventBus.on(GuiRenderEvent::class) { event -> action(event) }
    }

    /**
     * Registers an event that listens for GUI post-render events and executes an action.
     * @param action The action to execute after a GUI is rendered.
     */
    fun onGuiPostRender(action: (GuiPostRenderEvent) -> Unit) {
        EventBus.on(GuiPostRenderEvent::class) { event -> action(event) }
    }

    /**
     * Registers an event that listens for key presses in the GUI and executes an action.
     * @param action The action to execute when a key is pressed in the GUI.
     */
    fun onGuiKey(action: (GuiKeyEvent) -> Unit) {
        EventBus.on(GuiKeyEvent::class) { event -> action(event) }
    }

    fun <T : Packet<*>> onPacketReceived(packetClass: Class<T>, action: (packet: T) -> Unit) {
        EventBus.on(PacketReceiveEvent::class) { event ->
            val packet = event.packet
            if (packetClass.isInstance(packet)) {
                @Suppress("UNCHECKED_CAST")
                action(packetClass.cast(packet))
            }
        }
    }

    fun <T : Packet<*>> onPacketSent(packetClass: Class<T>, action: (packet: T) -> Unit) {
        EventBus.on(PacketSendEvent::class) { event ->
            val packet = event.packet
            if (packetClass.isInstance(packet)) {
                @Suppress("UNCHECKED_CAST")
                action(packetClass.cast(packet))
            }
        }
    }

    fun onPlayerInteract(action: (action: String, pos: BlockPos?, player: ClientPlayerEntity, world: World, event: PlayerInteractEvent) -> Unit) {
        EventBus.on(PlayerInteractEvent::class) { busEvent ->
            action(busEvent.action, busEvent.pos, busEvent.player, busEvent.world, busEvent)
        }
    }

    fun onEntityLoad(action: (entity: Entity, clientWorld: ClientWorld) -> Unit) {
        ClientEntityEvents.ENTITY_LOAD.register(action)
    }

    fun onEntityUnload(action: (entity: Entity, clientWorld: ClientWorld) -> Unit) {
        ClientEntityEvents.ENTITY_UNLOAD.register(action)
    }
}