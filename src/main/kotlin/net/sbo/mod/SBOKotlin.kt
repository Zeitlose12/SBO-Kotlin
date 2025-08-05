package net.sbo.mod

import org.slf4j.LoggerFactory
import net.sbo.mod.init.registerHelpCommand
import net.sbo.mod.utils.World
import net.sbo.mod.utils.Register

object SBOKotlin : ModInitializer, ClientModInitializer {
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		logger.info("Hello from the common Fabric world!")
	}

	override fun onInitializeClient() {
		logger.info("Hello from the client-specific Fabric world!")
		registerHelpCommand()
		Register.command("world") { _ ->
			val worldName = World.getWorld()
			logger.info("Current world: $worldName")
			0 // Return 0 to indicate success
		}
	}
}