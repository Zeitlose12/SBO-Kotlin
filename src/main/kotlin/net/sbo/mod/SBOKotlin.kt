package net.sbo.mod

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.Chat

object SBOKotlin : ModInitializer, ClientModInitializer {
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)
	private var xx = 5

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

		Register.onChatMessage("§7unclaimed event rewards!") { message ->
			logger.info("Chat message received: ${message.string}")
		}

		Register.onWorldChange { client ->
			logger.info("World Changed")
		}

		Register.onGuiOpen { client, screen ->
			logger.info("GUI opened: ${screen.title.string}")
		}

		Register.onGuiClose { client, screen ->
			logger.info("GUI closed: ${screen.title.string}")
		}

		Register.onGuiKey { client, screen, key ->
			logger.info("Key pressed in GUI: ${screen.title.string}, Key: $key")
		}

		Register.onTick(20) { tickDelta ->
			xx += 1
		}

//		Register.onRenderOverlay { context, tickDelta ->
//			val client = MinecraftClient.getInstance()
//			val textRenderer = client.textRenderer
//			val text = "Hallo Welt!"
//			val color = 0x000000 // Schwarz
//
//			context.drawTextWithShadow(
//				textRenderer,
//				text,
//				xx,
//				5,
//				color
//			)
//		}
	}
}