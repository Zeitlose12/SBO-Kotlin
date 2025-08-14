package net.sbo.mod.diana

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.util.math.BlockPos
import net.sbo.mod.SBOKotlin
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.Register
import net.sbo.mod.settings.categories.Customization
import net.sbo.mod.utils.Chat
import net.minecraft.particle.ParticleTypes as MCParticleTypes
import net.sbo.mod.utils.waypoint.Waypoint
import java.awt.Color
import net.sbo.mod.utils.waypoint.WaypointManager
import net.sbo.mod.utils.SboVec

internal class EvictingQueue<T>(internal val maxSize: Int) {
    internal val queue = mutableListOf<T>()

    fun add(item: T) {
        if (queue.size >= maxSize) {
            queue.removeFirst()
        }
        queue.add(item)
    }

    fun contains(item: T): Boolean {
        return queue.contains(item)
    }

    fun clear() {
        queue.clear()
    }
}

internal data class ParticleCheck(val typeCheck: (packet: ParticleS2CPacket) -> Boolean)

internal object ParticleTypes {
    private const val FLOAT_EPSILON = 0.001f

    internal val PARTICLE_CHECKS = mutableMapOf(
        "ENCHANT" to ParticleCheck { packet ->
            packet.parameters.type == MCParticleTypes.ENCHANT &&
            packet.count == 5 &&
            packet.speed == 0.05f &&
            packet.offsetX == 0.5f &&
            packet.offsetY == 0.4f &&
            packet.offsetZ == 0.5f
        },
        "EMPTY" to ParticleCheck { packet ->
            packet.parameters.type == MCParticleTypes.ENCHANTED_HIT &&
            packet.count == 4 &&
            packet.speed == 0.01f &&
            packet.offsetX == 0.5f &&
            packet.offsetY == 0.1f &&
            packet.offsetZ == 0.5f
        },
        "MOB" to ParticleCheck { packet ->
            packet.parameters.type == MCParticleTypes.CRIT &&
            packet.count == 3 &&
            packet.speed == 0.01f &&
            packet.offsetX == 0.5f &&
            packet.offsetY == 0.1f &&
            packet.offsetZ == 0.5f
        },
        "TREASURE" to ParticleCheck { packet ->
            packet.parameters.type == MCParticleTypes.DRIPPING_LAVA &&
            packet.count == 2 &&
            packet.speed == 0.01f &&
            packet.offsetX == 0.5f &&
            packet.offsetY == 0.1f &&
            packet.offsetZ == 0.5f
        },
        "FOOTSTEP" to ParticleCheck { packet ->
            //todo: fix this check type
            /*packet.parameters.type == MCParticleTypes.FOOTSTEP &&*/
            packet.count == 1 &&
            packet.speed == 0.0f &&
            packet.offsetX == 0.05f &&
            packet.offsetY == 0.0f &&
            packet.offsetZ == 0.05f
        }
    )

    fun getParticleType(packet: ParticleS2CPacket): String? {
        PARTICLE_CHECKS.forEach { (type, check) ->
            if (check.typeCheck(packet)) {
                return type
            }
        }
        return null
    }
}

internal data class Burrow(
    val pos: BlockPos,
    var hasFootstep: Boolean = false,
    var hasEnchant: Boolean = false,
    var type: String? = null,
    var waypoint: Waypoint? = null
)

object BurrowDetector {
    internal var lastInteractedPos: BlockPos? = null
    internal val burrows = mutableMapOf<String, Burrow>()
    internal var removePos: BlockPos? = null
    internal val burrowsHistory = EvictingQueue<String>(2)

    fun init() {
        Register.onPacketReceived(ParticleS2CPacket::class.java) { packet ->
            if (!Diana.dianaBurrowDetect) return@onPacketReceived
            burrowDetect(packet)
        }
        Register.onPacketSent(PlayerActionC2SPacket::class.java) { packet ->
            if (!Diana.dianaBurrowDetect) return@onPacketSent
            playerDigBlock(packet)
        }
        Register.onWorldChange {
            if (!Diana.dianaBurrowDetect) return@onWorldChange
            resetBurrows()
        }
        Register.onPlayerInteract { action, pos, event ->
            if (!Diana.dianaBurrowDetect) return@onPlayerInteract
            rightClickBlock(action, pos)
        }
        Register.command("sboclearburrows", "sbocb") {
            resetBurrows()
            Chat.chat("§6[SBO] §4Burrow Waypoints Cleared!§r")
        }
        //todo: add chat registers when diana elected
//        registerWhen(register("chat", (burrow) => {
//            refreshBurrows();
//        }).setCriteria("&r&eYou dug out a Griffin Burrow! &r&7${burrow}&r"), () => settings.dianaBurrowDetect);
//
//        registerWhen(register("chat", (burrow) => {
//            refreshBurrows();
//        }).setCriteria("&r&eYou finished the Griffin burrow chain!${burrow}"), () => settings.dianaBurrowDetect);
//
//        registerWhen(register("chat", (died) => {
//            refreshBurrows();
//        }).setCriteria(" ☠ You ${died}."), () => getWorld() == "Hub" && settings.dianaBurrowDetect);
    }

    private fun getRGB(type: String): Color {
        return when (type) {
            "Start" -> Color(Customization.StartColor)
            "Mob" -> Color(Customization.MobColor)
            "Treasure" -> Color(Customization.TreasureColor)
            else -> Color(255, 255, 255)
        }
    }

    private fun burrowDetect(packet: ParticleS2CPacket) {
        val particleType = ParticleTypes.getParticleType(packet) ?: return
        val pos = BlockPos(packet.x.toInt(), packet.y.toInt(), packet.z.toInt()).down()
        val posString = "${pos.x} ${pos.y} ${pos.z}"

        if (burrowsHistory.contains(posString)) return
        if (!burrows.containsKey(posString)) burrows[posString] = Burrow(pos)

        when (particleType) {
            "FOOTSTEP" -> burrows[posString]?.hasFootstep = true
            "ENCHANT" -> burrows[posString]?.hasEnchant = true
            "EMPTY" -> burrows[posString]?.type = "Start"
            "MOB" -> burrows[posString]?.type = "Mob"
            "TREASURE" -> burrows[posString]?.type = "Treasure"
        }

        val burrow = burrows[posString]
        if (burrow?.type != null && burrow.waypoint == null) {
            burrowsHistory.add(posString)
            val color = getRGB(burrow.type!!)
            burrow.waypoint = Waypoint(
                burrow.type!!,
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                color.red.toFloat() / 255, color.green.toFloat() / 255, color.blue.toFloat() / 255,
                type = "burrow"
            )
            WaypointManager.addWaypoint(burrow.waypoint!!)
        }
    }

    fun playerDigBlock(packet: PlayerActionC2SPacket) {
        if (packet.action != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) return

        val pos = packet.pos.down()
        val adjustedX = if (pos.x < 0) pos.x + 1 else pos.x
        val adjustedY = pos.y + 2
        val adjustedZ = if (pos.z < 0) pos.z + 1 else pos.z
        val posString = "$adjustedX $adjustedY $adjustedZ"

        if (burrows.containsKey(posString)) {
            removePos = BlockPos(adjustedX, adjustedY, adjustedZ)
            lastInteractedPos = pos
        }
    }

    fun rightClickBlock(action: String, pos: BlockPos?) {
        if (action == "useBlock") {
            val currentPos = BlockPos(pos?.x ?: 0, pos?.y ?: 0, pos?.z ?: 0)
            val adjustedX = if (currentPos.x < 0) currentPos.x + 1 else currentPos.x
            val adjustedY = currentPos.y + 1
            val adjustedZ = if (currentPos.z < 0) currentPos.z + 1 else currentPos.z
            val targetPosString = "$adjustedX $adjustedY $adjustedZ"

            if (burrows.containsKey(targetPosString)) {
                removePos = BlockPos(adjustedX, adjustedY, adjustedZ)
                lastInteractedPos = currentPos
            }
        }
    }

    fun removeBurrowWaypoint(x: Int, y: Int, z: Int) {
        val posString = "$x $y $z"
        if (burrows.containsKey(posString)) {
            val burrow = burrows[posString]
            burrow?.waypoint?.let {
                WaypointManager.removeWaypoint(it)
            }
            burrows.remove(posString)
        }
    }

    fun refreshBurrows() {
        if (removePos == null) return
        removeBurrowWaypoint(removePos!!.x, removePos!!.y, removePos!!.z)
        val player = SBOKotlin.mc.player ?: return
        val playerPos = SboVec(player.x, player.y, player.z)
        val guessWp = WaypointManager.guessWp
        if (guessWp != null && guessWp.pos.distanceTo(playerPos) < 4) {
            guessWp.hide()
        }
    }

    fun resetBurrows() {
        WaypointManager.removeAllOfType("burrow")
        burrows.clear()
        burrowsHistory.clear()
    }
}