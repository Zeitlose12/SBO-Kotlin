package net.sbo.mod.partyfinder

import gg.essential.universal.utils.toFormattedString
import net.azureaaron.hmapi.network.packet.v2.s2c.PartyInfoS2CPacket
import net.sbo.mod.settings.categories.PartyFinder
import net.sbo.mod.utils.HypixelModApi
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Helper.sleep
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.data.SboDataObject
import net.sbo.mod.utils.http.Http
import net.sbo.mod.utils.http.Http.getBoolean
import net.sbo.mod.utils.http.Http.getMutableMap
import net.sbo.mod.utils.http.Http.getString
import net.sbo.mod.utils.data.SboDataObject.sboData
import net.sbo.mod.SBOKotlin.API_URL
import net.sbo.mod.partyfinder.PartyPlayer.getPartyPlayerStats
import net.sbo.mod.utils.EventBus
import net.sbo.mod.utils.Helper
import net.sbo.mod.utils.data.PartyPlayerStats
import net.sbo.mod.utils.data.GetAllParties
import net.sbo.mod.utils.data.Party
import net.sbo.mod.utils.data.Reqs
import net.sbo.mod.utils.http.Http.getInt
import java.util.UUID
import kotlin.collections.mutableMapOf

object PartyFinderManager {
    private var creatingParty = false
    var inQueue = false
    private var updateBool = false
    private var requeue = false
    private var ghostParty = false
    private var requestSend = false
    private var usedPf = false

    private var partySize = 0
    private var partyMemberCount = 0
    private var partyMember: List<String> = emptyList()
    private var isLeader = false
    private var partyNote = ""
    private var partyType = ""
    private var partyReqs = ""
    private var partyReqsMap = mutableMapOf<String, Any>()
    var isInParty = false

    private val playersSentRequest = mutableMapOf<String, Long>()

    // todo: partycheck api, playercheck api, add fun invitePlayerIfMeetsReqs
    private val partyDisbandRegexes = listOf(
        Regex("^.+ §r§ehas disbanded the party!$"), // works
        Regex("^§r§cThe party was disbanded because (.+)$"), // works
        Regex("^§r§eYou left the party.§r$"), // works
        Regex("^§r§cYou are not currently in a party.$"), // works
        Regex("^§r§eYou have been kicked from the party by .+$"), // works
    )

    private val leaderChangeRegexes = listOf(
        Regex("^§r§eYou have joined §r(.+)'s* §r§eparty!$"), // works
        Regex("^§r§eThe party was transferred to §r(.+) §r§eby §r.+$"), // works
        Regex("^(.+)§r§e has promoted §r(.+) §r§eto Party Leader$") // works
    )

    private val partyJoinRegexes = listOf(
        Regex("^(.+) §r§ejoined the party.$"), // works
        Regex("^§r§eYou have joined §r(.+)'s? §r§eparty!$") // works
    )

    private val partyLeaveRegexes = listOf(
        Regex("^(.+) §r§ehas been removed from the party.$"), // works
        Regex("^(.+) §r§ehas left the party.$"), // works
        Regex("^(.+) §r§ewas removed from your party because they disconnected.$"),
        Regex("^§r§eKicked (.+) because they were offline.$") // works
    )

    fun init() {
        trackMemberRegister()

        Register.command("sbotestrequest") {
            getAllParties("Diana") { parties ->
                if (parties.isEmpty()) {
                    Chat.chat("§6[SBO] §cNo parties found.")
                } else {
                    Chat.chat("§6[SBO] §eFound ${parties.size} parties:")
                    parties.forEach { party ->
                        Chat.chat("§6[SBO] §eParty: ${party.leader} - ${party.partyMembersCount} members - ${party.reqs}")
                    }
                }
            }

            getActiveUsers { activeUsers ->
                Chat.chat("§6[SBO] §eActive users: $activeUsers")
            }

            Chat.chat(Chat.getChatBreak())
            Chat.chat(
                Chat.textComponent("§6[SBO] §bRolexDE §ewants to join your party.\n"),
                Chat.textComponent("§7[§aInvite§7]", "/p RolexDE", "/p invite RolexDE"),
                Chat.textComponent(" §7[§eCheck Stats§7]", "/sboc RolexDE", "/sbocheck RolexDE"),
            )
            Chat.chat(Chat.getChatBreak())
        }

        Register.command("sborequeue") { // todo: change it to sboRequeue like in old sbo when finished with the new party finder
            createParty("", "This","Diana", 6)
        }

        Register.command("sbodequeue") {
            if (inQueue) {
                removePartyFromQueue()
            } else {
                Chat.chat("§6[SBO] §4You are not in a party queue.")
            }
        }

        Register.command("sboKey") { args ->
            if (args.isEmpty()) {
                Chat.chat("§6[SBO] §cPlease provide a key")
            } else if (args[0].startsWith("sbo").not()) {
                Chat.chat("§6[SBO] §cInvalid key format! get one in our Discord")
            } else {
                sboData.sboKey = args[0]
                SboDataObject.save("SboData")
                Chat.chat("§6[SBO] §aKey has been set")
            }
        }

        Register.command("sboClearKey") {
            sboData.sboKey = ""
            SboDataObject.save("SboData")
            Chat.chat("§6[SBO] §aKey has been cleared")
        }

        Register.command("testclickchat") {
            Chat.clickableChat(
                "§6[SBO] §eClick to requeue party with last used requirements.",
                "Requeue Party",
            ) {
                createParty(partyReqs, partyNote, partyType, partySize)
            }
        }

        Register.onChatMessage(
            Regex("^§r§d(?<toFrom>.*?) (?<player>.*?)§r§7: §r§7SBO join party request - (?<id>.*?)$"),
            true
        ) { _, matcher ->
            Chat.chat("match")
            if (matcher.groups["toFrom"]?.value?.contains("From") ?: false) {
                if (partyMemberCount < partySize) {
                    val playerName = Helper.getPlayerName(matcher.groups["player"]?.value ?: "no name")

                    if (PartyFinder.autoInvite) {
                        Chat.command("p invite $playerName")
                    } else {
                        Chat.chat(Chat.getChatBreak())
                        Chat.chat(
                            Chat.textComponent("§6[SBO] §b$playerName §ewants to join your party.\n"),
                            Chat.textComponent("§7[§aInvite§7]", "/p $playerName", "/p invite $playerName"),
                            Chat.textComponent(" §7[§eCheck Stats§7]", "/sboc $playerName", "/sbocheck $playerName"),
                        )
                        Chat.chat(Chat.getChatBreak())
                    }
                }
            }
        }

        Register.onTick(20 * 60 * 4) { // every 4 minutes
            Http.sendGetRequest("$API_URL/countActiveUsers")

            if (inQueue) {
                Http.sendGetRequest("$API_URL/queueUpdate?leaderId=${Player.getUUIDString().replace("-", "")}")
                    .toJsonObject { response ->
                        if (!response.getBoolean("Success")) {
                            inQueue = false
                            Chat.chat("§6[SBO] §4${response.getString("Error") ?: "Unknown error"}")
                        }
                    }
                    .error { error ->
                        inQueue = false
                        Chat.chat("§6[SBO] §4Unexpected error")
                    }
            }
        }

        Register.onDisconnect {
            if (inQueue) {
                removePartyFromQueue()
            }
        }

        HypixelModApi.onPartyInfo{ isInParty, isLeader, members ->
            this.isInParty = isInParty
            this.isLeader = isLeader
            this.partyMember = members
            partyMemberCount = members.size
            queueParty()
            updateParty()
        }

        HypixelModApi.onError { packet ->
            if (packet.id == PartyInfoS2CPacket.ID) {
                creatingParty
                updateBool = false
            }
        }
    }

    fun createParty(
        reqs: String,
        note: String,
        type: String,
        size: Int,
    ) {
        this.partyReqs = reqs
        this.partyNote = checkPartyNote(note)
        this.partyType = type
        this.partySize = size
        this.creatingParty = true
        this.usedPf = true

        HypixelModApi.sendPartyInfoPacket()
    }

    fun queueParty() {
        if (!this.creatingParty) return
        if (partyMember.size < partySize && !inQueue) {
            try {
                val currentTime = System.currentTimeMillis()
                Http.sendGetRequest(
                    "$API_URL/createParty?uuids=${partyMember.joinToString(",").replace("-", "")}" +
                            "&reqs=$partyReqs" +
                            "&note=$partyNote" +
                            "&type=$partyType" +
                            "&size=$partySize" +
                            "&key=${sboData.sboKey}"
                ).toJsonObject { response ->
                    if (response.getBoolean("Success")) {
                        val timeTaken = System.currentTimeMillis() - currentTime
                        inQueue = true
                        creatingParty = false
                        partyReqsMap = response.getMutableMap("Reqs") ?: mutableMapOf()
                        EventBus.emit("refreshPartyList")

                        if (ghostParty) {
                            removePartyFromQueue()
                            ghostParty = false
                        }

                        if (requeue) {
                            requeue = false
                            Chat.clickableChat(
                                "§6[SBO] §eClick to dequeue party",
                                "Dequeue Party",
                            ) {
                                removePartyFromQueue()
                            }
                        }

                        Chat.chat("§6[SBO] §eParty created successfully! Time taken: ${timeTaken}ms")

                        if (isInParty) Chat.command("pc [SBO] Party now in queue.")
                    } else {
                        val errorMessage = response.getString("Error") ?: "Unknown error"
                        Chat.chat("§6[SBO] §4Failed to create party: $errorMessage")

                    }

                }.error { error ->
                    Chat.chat("§6[SBO] §4Unexpected error while creating party: ${error.message}")
                }

            } catch (e: Exception) {
                return
            }
        } else {
            Chat.chat("§6[SBO] §4Party is already in queue or full.")
        }
    }

    fun updateParty() {
        if (updateBool && inQueue && isInParty && isLeader) {
            updateBool = false
            if (partyMember.size >= partySize || partyMember.size < 2) return
            val currentTime = System.currentTimeMillis()
            Http.sendGetRequest(
                "$API_URL/queuePartyUpdate?uuids=${partyMember.joinToString(",").replace("-", "")}" +
                        "&reqs=$partyReqs" +
                        "&note=$partyNote" +
                        "&type=$partyType" +
                        "&size=$partySize" +
                        "&key=${sboData.sboKey}"
            ).toJsonObject { response ->
                if (response.getBoolean("Success")) {
                    val timeTaken = System.currentTimeMillis() - currentTime
                    partyReqsMap = response.getMutableMap("Reqs") ?: mutableMapOf()
                    Chat.chat("§6[SBO] §eParty updated successfully! Time taken: ${timeTaken}ms")
                } else {
                    inQueue = false
                    val errorMessage = response.getString("Error") ?: "Unknown error"
                    Chat.chat("§6[SBO] §4Failed to update party: $errorMessage")
                }
            }.error { error ->
                inQueue = false
                Chat.chat("§6[SBO] §4Unexpected error while updating party")
            }
        }
    }

    fun getAllParties(
        partyType: String,
        onComplete: ((List<Party>) -> Unit)? = null
    ) {
        Http.sendGetRequest("$API_URL/getAllParties?partytype=$partyType").toJson<GetAllParties>(true) { response ->
            if (response.success) {
                val partyList = response.parties
                onComplete?.invoke(partyList)
            } else {
                Chat.chat("§6[SBO] §4Failed to get parties")
            }
        }.error { error ->
            Chat.chat("§6[SBO] §4Unexpected error while getting parties: ${error.message}")
        }
    }

    fun getActiveUsers(
        onComplete: ((Int) -> Unit)? = null
    ) {
        Http.sendGetRequest("$API_URL/activeUsers").toJsonObject { response ->
            onComplete?.invoke(response.getInt("activeUsers") ?: 0)
        }.error { error ->
            Chat.chat("§6[SBO] §4Unexpected error while getting active users: ${error.message}")
        }
    }

    fun checkIfPlayerMeetsReqs(
        stats: PartyPlayerStats,
        reqs: Reqs
    ): Boolean {
        if (stats.sbLvl < reqs.lvl) {
            return false
        }
        if (stats.mythosKills < reqs.kills) {
            return false
        }
        if (reqs.eman9 && !stats.eman9) {
            return false
        }
        if (reqs.looting5 && !stats.looting5daxe) {
            return false
        }
        if (stats.magicalPower < reqs.mp) {
            return false
        }
        return true
    }

    fun sendJoinRequest(
        partyLeader: String,
        partyReqs: Reqs
    ) {
        getPartyPlayerStats { playerStats ->
            if (checkIfPlayerMeetsReqs(playerStats, partyReqs)) {
                if (playersSentRequest.containsKey(partyLeader) && (System.currentTimeMillis() - playersSentRequest[partyLeader]!! < 60000)) { // 1 minute cooldown
                    Chat.chat("§6[SBO] §cYou have already sent a request to this player recently.")
                } else {
                    Chat.chat("§6[SBO] §eSending join request to $partyLeader...")
                    Chat.command("msg $partyLeader SBO join party request - id:${UUID.randomUUID()}")
                    playersSentRequest[partyLeader] = System.currentTimeMillis()
                }
            } else {
                Chat.chat("§6[SBO] §cYou don't meet the requirements to join this party.")
            }
        }
    }

    fun removePartyFromQueue(onComplete: ((Boolean) -> Unit)? = null) {
        if (inQueue) {
            inQueue = false
            Http.sendGetRequest("$API_URL/unqueueParty?leaderId=${Player.getUUIDString().replace("-", "")}")
                .result { response ->
                    onComplete?.invoke(true)
                    Chat.chat("§6[SBO] §eParty removed from queue.")
                }.error { error ->
                    Chat.chat("§6[SBO] §4Unexpected error while removing party from queue")
                }
        } else if (creatingParty) {
            ghostParty = true
        }
    }

    fun trackMemberRegister() {
        Register.onChatMessage { message ->
            val text = message.toFormattedString()
            var match = false
            leaderChangeRegexes.forEach {
                if (it.matches(text)) {
                    match = true
                    isInParty = true
                    isLeader = false
                    removePartyFromQueue()
                    Chat.chat("§6[SBO] §eParty leader changed")
                }
            }
            partyDisbandRegexes.forEach {
                if (it.matches(text)) {
                    creatingParty = false
                    partyMemberCount = 1
                    match = true
                    isInParty = false
                    removePartyFromQueue()
                    Chat.chat("§6[SBO] §4Party has been disbanded.")
                }
            }
            partyJoinRegexes.forEach {
                if (it.matches(text)) {
                    updateBool = true
                    partyMemberCount += 1
                    match = true
                    isInParty = true
                    Chat.chat("§6[SBO] §eParty member joined")
                }
            }
            partyLeaveRegexes.forEach {
                if (it.matches(text)) {
                    updateBool = true
                    partyMemberCount -= 1
                    match = true
                    isInParty = partyMemberCount > 1
                    Chat.chat("§6[SBO] §eParty member left")
                }
            }
            if (match) trackMemberCount()
        }
    }

    fun trackMemberCount() { // todo: test this function
        if (inQueue) {
            if (partyMemberCount >= partySize) {
                sleep(100) {
                    Chat.chat("§6[SBO] §4Party is full, removing from queue.")
                    removePartyFromQueue()
                }
            } else {
                updateBool = true
                sleep(200) {
                    if (updateBool) HypixelModApi.sendPartyInfoPacket()
                }
            }
        } else {
            if (partyMemberCount == 1) return
            if (!isLeader) return
            if (partyMemberCount < partySize && !creatingParty && !requeue && usedPf) {
                requeue = true
                sleep(200) {
                    if (PartyFinder.autoRequeue) {
                        Chat.chat("&6[SBO] &eRequeuing party with last used requirements...")
                        createParty(partyReqs, partyNote, partyType, partySize)
                    } else {
                        Chat.clickableChat(
                            "&6[SBO] &eClick to requeue party with last used requirements.",
                            "Requeue Party",
                        ) {
                            createParty(partyReqs, partyNote, partyType, partySize)
                        }
                    }
                }
            }
        }
    }

    fun checkPartyNote(note: String): String {
        // allowed characters a-z, A-Z, 0-9, comma, dot, exclamation mark, hyphen, underscore, question mark
        return note.replace(Regex("[^a-zA-Z0-9 ,.!?\\-_]"), "")
            .take(30)
            .trim().replace(" ", "%20")
    }
}