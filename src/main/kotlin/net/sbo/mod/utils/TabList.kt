package net.sbo.mod.utils

import net.minecraft.client.network.PlayerListEntry
import net.minecraft.text.Text
import net.sbo.mod.SBOKotlin.mc

object TabList {
    fun getTabEntries(): List<PlayerListEntry> {
        return try {
            val client = mc
            client.player?.networkHandler?.playerList?.toList() ?: emptyList()
        } catch (e: Exception) {
            if (e is ArrayIndexOutOfBoundsException) {
                emptyList()
            } else {
                throw e
            }
        }
    }

    fun findInfo(key: String): String? {
        val tabEntries = getTabEntries()
        for (entry in tabEntries) {
            val lineText: Text = entry.displayName ?: Text.literal(entry.profile.name)
            val lineString = lineText.string.trim()
            if (lineString.startsWith(key)) {
                return lineString.substring(key.length).trim()
            }
        }
        return null
    }
}