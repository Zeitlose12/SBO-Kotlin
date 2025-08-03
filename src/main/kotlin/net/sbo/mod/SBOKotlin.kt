package net.sbo.mod

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import net.minecraft.text.Text
import net.sbo.mod.utils.Register

object SBOKotlin : ModInitializer, ClientModInitializer {
	private const val MOD_ID = "sbo-kotlin"
	private val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		logger.info("Hello from the common Fabric world!")
	}

	override fun onInitializeClient() {
		logger.info("Hello from the client-specific Fabric world!")

		Register.command("sbo-kotlin") { context ->
			context.source.sendFeedback(Text.of("Hello from the sbo-kotlin command!"))
			1 // Return 1 to indicate success
		}
	}
}