package net.sbo.mod.diana

import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.decoration.ArmorStandEntity
import net.sbo.mod.utils.events.Register
import net.minecraft.entity.mob.MobEntity
import net.sbo.mod.SBOKotlin.mc
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.chat.Chat
import net.sbo.mod.utils.chat.ChatUtils.formattedString
import net.sbo.mod.utils.overlay.Overlay
import net.sbo.mod.utils.overlay.OverlayTextLine
import kotlin.math.roundToInt

object DianaMobDetect {
    private val trackedArmorStands = mutableMapOf<Int, String>()
    private val mobHpOverlay: Overlay = Overlay("mythosMobHp", 10f, 10f, 1f, listOf("Chat screen")).setCondition { Diana.mythosMobHp }

    fun init() {
        mobHpOverlay.init()
        Register.onTick(6) {
            val world = mc.world
            if (world != null) {
                updateTrackedArmorStands(world)
            }
        }
    }

    private fun updateTrackedArmorStands(world: ClientWorld) {
        val currentArmorStands = getMatchingArmorStands(world)
        val overlayLines: MutableList<OverlayTextLine> = mutableListOf()

        // Remove armor stands that are no longer present
        trackedArmorStands.keys.retainAll(currentArmorStands.keys)

        for ((id, name) in currentArmorStands) {
            trackedArmorStands[id] = name
            overlayLines.add(OverlayTextLine(name))
        }
        mobHpOverlay.setLines(overlayLines)
    }

    private fun getMatchingArmorStands(world: ClientWorld): Map<Int, String> {
        val entityIdSet = world.entities.map { it.id }.toSet()
        val armorstands = mutableMapOf<Int, String>()
        val keywords = listOf("Inquisitor", "Exalted", "Stalwart")
        for (entity in world.entities) {
            val armorStandId = entity.id + 1
            if (armorStandId in entityIdSet) {
                val armorStand = world.getEntityById(armorStandId)
                if (armorStand is ArmorStandEntity) {
                    val name = armorStand.customName?.formattedString() ?: armorStand.name.formattedString()
                    if (name.isEmpty() || name == "Armor Stand") continue
                    if (keywords.any { name.contains(it, ignoreCase = true) }) {
                        armorstands[armorStandId] = name
                    }
                }
            }
        }
        return armorstands
    }

    fun onInqSpawn() {
        if (Diana.shareInq) {
            val playerPos = Player.getLastPosition()
            Chat.command("pc x: ${playerPos.x.roundToInt()}, y: ${playerPos.y.roundToInt() - 1}, z: ${playerPos.z.roundToInt()}")
        }

        Diana.announceKilltext.firstOrNull()?.let { killText ->
            if (killText.isNotBlank()) {
                Helper.sleep(5000) {
                    Chat.command("pc " + Diana.announceKilltext[0])
                }
            }
        }
    }
}