package net.sbo.mod

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object SBOKotlin : ModInitializer {
	private const val MOD_ID = "sbo-kotlin"
    private val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		logger.info("Hello Fabric world!")
	}
}