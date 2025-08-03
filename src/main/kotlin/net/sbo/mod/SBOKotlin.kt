package net.sbo.mod

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import org.slf4j.LoggerFactory
import net.minecraft.text.Text
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.Chat

object SBOKotlin : ModInitializer, ClientModInitializer {
	private const val MOD_ID = "sbo-kotlin"
	internal val logger = LoggerFactory.getLogger(MOD_ID)

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

//		Register.onRenderOverlay { matrices, tickDelta ->
//			val client = MinecraftClient.getInstance()
//			val textRenderer = client.textRenderer
//
//			// Der Text, der angezeigt werden soll
//			val text = "Hallo Welt!"
//
//			// Zeichne den Text an den Koordinaten (5, 5) mit einem Schatten
//			textRenderer.drawStringWithShadow(
//				matrices,
//				text,
//				5f,
//				5f,
//				0xFFFFFF // weiße Farbe
//			)
//		}
	}
}