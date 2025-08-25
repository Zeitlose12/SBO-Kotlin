package net.sbo.mod.utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.client.network.OtherClientPlayerEntity
import net.sbo.mod.utils.chat.Chat
import java.util.*

object MobEvents {
    private val trackedMobs: MutableMap<UUID, OtherClientPlayerEntity> = mutableMapOf()

    fun register() {
        ClientEntityEvents.ENTITY_LOAD.register { entity, clientWorld ->
            if (entity is OtherClientPlayerEntity) {
                val displayName = entity.name?.string
                if (displayName != null &&
                    (displayName.contains("Minos Inquisitor") || displayName.contains("Minos Champion") || displayName.contains("Minotaur "))) {
                    trackedMobs[entity.uuid] = entity
                }
            }
        }

        ClientEntityEvents.ENTITY_UNLOAD.register { entity, clientWorld ->
            if (trackedMobs.containsKey(entity.uuid)) {
                val removedEntity = trackedMobs.remove(entity.uuid)
            }
        }
    }
}
