package net.sbo.mod

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.minecraft.client.MinecraftClient
import net.sbo.mod.diana.DianaGuessHandler
import net.sbo.mod.utils.waypoint.WaypointManager
import org.slf4j.LoggerFactory
import net.sbo.mod.init.registerHelpCommand
import net.sbo.mod.settings.Settings
import net.sbo.mod.utils.mayor.Mayor
import net.sbo.mod.utils.Register
import net.sbo.mod.general.PartyCommands
import net.sbo.mod.utils.data.SboDataObject
import net.sbo.mod.partyfinder.PartyFinderManager
import net.sbo.mod.utils.SboKeyBinds
import net.sbo.mod.guis.Main
import net.sbo.mod.partyfinder.PartyCheck
import net.sbo.mod.partyfinder.PartyPlayer
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.ClickActionManager
import net.sbo.mod.utils.HypixelModApi

object SBOKotlin {
	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()

	const val API_URL: String = "https://api.skyblockoverhaul.com"


	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)

	val configurator = Configurator("sbo")
	val settings = Settings.register(configurator)
	val guis = Main

	@JvmStatic
	fun onInitializeClient() {
		logger.info("Initializing SBO-Kotlin...")

		// Load configuration and data
		SboDataObject.init()

		// load Main Features
		registerHelpCommand()
		PartyCommands.registerPartyChatListeners()
		Register.command("sbo") {
			mc.send{
				mc.setScreen(ResourcefulConfigScreen.getFactory("sbo").apply(null))
			}
		}

		Register.command("test", "Test Command", "tester") { args ->
			Chat.chat("Test command executed with argument 1: ${args.getOrNull(0) ?: "No argument provided"}")
			Chat.chat("Test command executed with argument 2: ${args.getOrNull(1) ?: "No second argument provided"}")
			Chat.chat("This is a test command! Arguments: ${args.joinToString(", ")}")
		}

		// Registering Guis
		guis.register()

		ClickActionManager.inti()
		SboKeyBinds.init()
		WaypointManager.init()
		HypixelModApi.init()
		PartyFinderManager.init()
		PartyCheck.init()
		DianaGuessHandler.init()
		Mayor.init()

		Register.onTick(100) { // todo: unregister this register when player is loaded
			if (mc.player != null && PartyPlayer.stats.sbLvl == -1 && HypixelModApi.isOnHypixel) {
				PartyPlayer.init()
			}
		}
		logger.info("SBO-Kotlin initialized successfully!")
	}
}