package net.sbo.mod.utils.events.impl

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.world.World
import net.minecraft.util.math.BlockPos

class PlayerInteractEvent(
    val action: String,
    val pos: BlockPos?,
    val player: ClientPlayerEntity,
    val world: World,
    var isCanceled: Boolean = false
)
