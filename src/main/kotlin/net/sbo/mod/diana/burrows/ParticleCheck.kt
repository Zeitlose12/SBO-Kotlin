package net.sbo.mod.diana.burrows

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket

internal data class ParticleCheck(val typeCheck: (packet: ParticleS2CPacket) -> Boolean)