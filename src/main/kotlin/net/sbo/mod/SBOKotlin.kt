package net.sbo.mod

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import net.minecraft.text.Text
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.Chat

object SBOKotlin : ModInitializer, ClientModInitializer {
	private const val MOD_ID = "sbo-kotlin"
	private val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		logger.info("Hello from the common Fabric world!")
	}

	override fun onInitializeClient() {
		logger.info("Hello from the client-specific Fabric world!")

		Register.command("sbotest") { context ->
			Chat.chat("Hello from the sbo-kotlin mod!")
			Chat.command("say Hello from the sbo-kotlin mod! command!")
			1
		}
	}
}