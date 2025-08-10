package net.sbo.mod

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.sbo.mod.general.WaypointManager
import org.slf4j.LoggerFactory
import net.sbo.mod.init.registerHelpCommand
import net.sbo.mod.settings.Settings
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.World
import net.sbo.mod.general.PartyCommands
import net.sbo.mod.data.SboConfigBundle
import net.sbo.mod.data.SboDataObject
import net.sbo.mod.utils.SboKeyBinds
import net.sbo.mod.guis.Main

object SBOKotlin {
	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)
	lateinit var SBOConfigBundle: SboConfigBundle

	val configurator = Configurator("sbo")
	val settings = Settings.register(configurator)
	val guis = Main

	@JvmStatic
	fun onInitializeClient() {
		logger.info("Initializing SBO-Kotlin...")

		// Load configuration and data
		SBOConfigBundle = SboDataObject.loadAllData("SBO")
		SboDataObject.saveAllDataThreaded("SBO")
		SboDataObject.savePeriodically(10)
		ServerLifecycleEvents.SERVER_STOPPING.register {
			SboDataObject.saveAndBackupAllDataThreaded("SBO")
		}

		// load Main Features
		registerHelpCommand()
		WaypointManager
		PartyCommands.registerPartyChatListeners()
		Register.command("sbo") {
			mc.send{
				mc.setScreen(ResourcefulConfigScreen.getFactory("sbo").apply(null))
			}
		}

		// Registering Guis
		guis.register()


		SboKeyBinds.register()
		SboKeyBinds.registerKeyBindListener()
		logger.info("SBO-Kotlin initialized successfully!")
	}
}