package net.sbo.mod

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.minecraft.client.MinecraftClient
import net.sbo.mod.general.WaypointManager
import org.slf4j.LoggerFactory
import net.sbo.mod.init.registerHelpCommand
import net.sbo.mod.settings.Settings
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.World
import net.sbo.mod.general.PartyCommands
import net.sbo.mod.data.SboDataObject
import net.sbo.mod.data.configs.SboData
import net.sbo.mod.utils.SboKeyBinds
import net.sbo.mod.utils.Chat.chat

object SBOKotlin {
	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)
	lateinit var sboData: SboData

	val configurator = Configurator("sbo")
	val settings = Settings.register(configurator)

	@JvmStatic
	fun onInitializeClient() {
		logger.info("Initializing SBO-Kotlin...")
		registerHelpCommand()
		WaypointManager
		PartyCommands.registerPartyChatListeners()
		sboData = SboDataObject.load("SBO", "SboData.json", SboData(), SboData::class.java)
		Register.command("sbo") {
			mc.send{
				mc.setScreen(ResourcefulConfigScreen.getFactory("sbo").apply(null))
			}
		}
		Register.command("sboTest") {
			logger.info(World.isInSkyblock().toString())
			logger.info(sboData.b2bInq.toString())

		}

		SboKeyBinds.register()
		SboKeyBinds.registerKeyBindListener()
	}
}