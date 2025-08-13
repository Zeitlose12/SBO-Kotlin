package net.sbo.mod.overlays

import net.minecraft.client.MinecraftClient
import net.sbo.mod.utils.Register
import net.sbo.mod.settings.categories.General
import net.sbo.mod.utils.World
import java.util.UUID

object Legion {
    private var legionCount: Int = 0

    fun init () {
        Register.onTick(20) { client ->
            if (!General.legionOverlay || !World.isInSkyblock()) return@onTick
            val player = client.player ?: return@onTick
            val world = client.world ?: return@onTick
            val nearbyPlayers = world.players
                .filter { otherPlayer ->
                    otherPlayer != player &&

                    (otherPlayer.uuid.version() == 4 || otherPlayer.uuid.version() == 1) &&
                    getPlayerPing(client, otherPlayer.uuid) > 0 &&
                    otherPlayer.distanceTo(player) <= 30
                }
                .distinctBy { it.uuid }
            legionCount = nearbyPlayers.size
        }
    }

    fun getPlayerPing(client: MinecraftClient, uuid: UUID): Int {
        return client.networkHandler?.getPlayerListEntry(uuid)?.latency ?: 0
    }
}