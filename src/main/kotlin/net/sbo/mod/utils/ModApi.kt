package net.sbo.mod.utils

import net.azureaaron.hmapi.events.HypixelPacketEvents;
import net.azureaaron.hmapi.network.HypixelNetworking;
import net.azureaaron.hmapi.network.packet.s2c.ErrorS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HelloS2CPacket;
import net.azureaaron.hmapi.network.packet.s2c.HypixelS2CPacket;
import net.azureaaron.hmapi.network.packet.v1.s2c.LocationUpdateS2CPacket;
import net.azureaaron.hmapi.network.packet.v2.s2c.PartyInfoS2CPacket

object HypixelEventApi {
    var isOnHypixel: Boolean = false
    var isOnSkyblock: Boolean = false
    var isLeader: Boolean = false
    var isInParty: Boolean = false
    var partyMembers: List<String> = emptyList()
    var mode: String = ""

    private val partyInfoListeners = mutableListOf<(isInParty: Boolean, isLeader: Boolean, members: List<String>) -> Unit>()


    fun init() {
        HypixelPacketEvents.HELLO.register(::handlePacket)
        HypixelPacketEvents.PARTY_INFO.register(::handlePacket)
        HypixelPacketEvents.LOCATION_UPDATE.register(::handlePacket)

        Register.command("sboapitest") {
            if (isOnHypixel) {
                Chat.chat("[SBO] You are on Hypixel, Skyblock mode: $isOnSkyblock, current mode: $mode")
            } else {
                Chat.chat("[SBO] You are not on Hypixel")
            }
        }
    }

    private fun handlePacket(packet: HypixelS2CPacket) {
        when (packet) {
            is HelloS2CPacket -> {
                onHelloPacket(packet)
            }

            is LocationUpdateS2CPacket -> {
                onLocationUpdatePacket(packet)
            }

            is PartyInfoS2CPacket -> {
                onPartyInfoPacket(packet)
            }

            is ErrorS2CPacket -> {
                onErrorPacket(packet)
            }

            else -> {}
        }
    }

    private fun onLocationUpdatePacket(packet: LocationUpdateS2CPacket) {
        isOnSkyblock = packet.serverType.orElse("") == "SKYBLOCK"
        mode = packet.mode.orElse("")
    }

    private fun onHelloPacket(packet: HelloS2CPacket) {
        isOnHypixel = true
        Chat.chat("[SBO] Connected to Hypixel")
    }

    private fun onPartyInfoPacket(packet: PartyInfoS2CPacket) {
        this.isInParty = packet.inParty
        this.partyMembers = packet.members?.keys?.map { it.toString() } ?: emptyList()

        if (this.isInParty) this.isLeader = packet.members?.get(Player.getUUID())?.toString() == "LEADER" else this.isLeader = true
        if (this.partyMembers.isEmpty()) this.partyMembers += Player.getUUIDString()

        partyInfoListeners.forEach { listener ->
            listener(this.isInParty, this.isLeader, this.partyMembers)
        }
    }

    fun onPartyInfo(listener: (isInParty: Boolean, isLeader: Boolean, members: List<String>) -> Unit) {
        partyInfoListeners.add(listener)
    }


    private fun onErrorPacket(packet: ErrorS2CPacket) {
        if (packet.id == LocationUpdateS2CPacket.ID) {
            isOnSkyblock = false
            mode = ""
        }
    }

    fun sendPartyInfoPacket() {
        HypixelNetworking.sendPartyInfoC2SPacket(2);
    }
}