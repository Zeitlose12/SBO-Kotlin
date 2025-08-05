package net.sbo.mod

import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import net.sbo.mod.init.registerHelpCommand

object SBOKotlin {
	@JvmField
	val mc: MinecraftClient = MinecraftClient.getInstance()
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)

	@JvmStatic
	fun onInitializeClient() {
		logger.info("Hello from the client-specific Fabric world!")
		registerHelpCommand()
	}
}