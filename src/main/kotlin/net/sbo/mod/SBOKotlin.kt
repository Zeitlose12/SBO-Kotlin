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

object SBOKotlin {
	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)

	val configurator = Configurator("sbo")
	val settings = Settings.register(configurator)

	@JvmStatic
	fun onInitializeClient() {
		logger.info("Hello from the client-specific Fabric world!")
		registerHelpCommand()
		WaypointManager
		PartyCommands.registerPartyChatListeners()
		Register.command("sbo") {
			mc.send{
				mc.setScreen(ResourcefulConfigScreen.getFactory("sbo").apply(null))
			}
		}
		Register.command("sboTest") {
			logger.info(World.isInSkyblock().toString())
		}
	}
}