package net.sbo.mod.diana

import net.sbo.mod.utils.HypixelEventApi
import net.sbo.mod.utils.Register
import net.sbo.mod.utils.Chat
import net.sbo.mod.utils.Player
import net.sbo.mod.utils.http.Http
import net.sbo.mod.utils.http.Http.getBoolean
import net.sbo.mod.utils.http.Http.getMutableMap
import net.sbo.mod.utils.http.Http.getString
import net.sbo.mod.utils.data.SboDataObject.sboData

object PartyFinderManager {
    private var creatingParty = false
    private var inQueue = false
    private var updateBool = false
    private var requeue = false
    private var ghostParty = false
    private var requestSend = false

    private var createPartyTimeStamp = 0L
    private var partySize = 0
    private var partyMemberCount = 0
    private var partyNote = ""
    private var partyType = ""
    private var partyReqs = ""
    private var partyReqsMap = mutableMapOf<String, Any>()

    private val playersSentRequest = mutableListOf<String>()

    private const val API_URL = "https://api.skyblockoverhaul.com"

    private val partyDisbandRegexes = listOf( // todo: check if all regexes are correct and maybe move them to a separate file
        Regex("^.+ &r&ehas disbanded the party!&r$"),
        Regex("^&cThe party was disbanded because (.+)$"),
        Regex("^&eYou left the party.&r$"),
        Regex("^&cYou are not currently in a party.&r$"),
        Regex("^&eYou have been kicked from the party by .+$"),
    )

    private val leaderChangeRegexes = listOf(
        Regex("^&eYou have joined &r(.+)'s* &r&eparty!&r$"),
        Regex("^&eThe party was transferred to &r(.+) &r&eby &r.+&r$"),
        Regex("^(.+) &r&e has promoted &r(.+) &r&eto Party Leader&r$")
    )

    private val partyJoinRegexes = listOf(
        Regex("^(.+) &r&ejoined the party.&r$"),
        Regex("^&eYou have joined &r(.+)'[s]? &r&eparty!&r$")
    )

    private val partyLeaveRegexes = listOf(
        Regex("^(.+) &r&ehas been removed from the party.&r$"),
        Regex("^(.+) &r&ehas left the party.&r$"),
        Regex("^(.+) &r&ewas removed from your party because they disconnected.&r$"),
        Regex("^&eKicked (.+) because they were offline.&r$")
    )

    fun init() {
        Register.command("sborequeue") {
            createParty(
                "",
                "This",
                "Diana",
                5
            )
        }

        Register.command("sbotestrequest") {
            Http.sendGetRequest("$API_URL/getAllParties?partytype=Diana").result { response ->
                if (response.isSuccessful) {
                    val data = response.body?.string() ?: "No data"
                    Chat.chat("[SBO] Test request successful: $data")
                } else {
                    Chat.chat("[SBO] Test request failed: ${response.code} - ${response.message}")
                }
            }
            .error { error ->
                Chat.chat("[SBO] Test request error: ${error.message}")
            }
        }

        Register.command("sbodequeue") {
            if (inQueue) {
                removePartyFromQueue()
            } else {
                Chat.chat("§6[SBO] §4You are not in a party queue.")
            }
        }

        Register.command("sboKey") { // todo: add a way to set the key in the settings or over this command
            Chat.chat("§6[SBO] §aKey has been set");
        }

        HypixelEventApi.onPartyInfo{ isInParty, isLeader, members ->
            partyMemberCount = members.size
            queueParty(isInParty, isLeader, members)
            updateParty(isInParty, isLeader, members)
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

        HypixelEventApi.sendPartyInfoPacket()
        createPartyTimeStamp = System.currentTimeMillis()
    }

    fun queueParty(isInParty: Boolean, isLeader: Boolean, members: List<String>) {
        if (!this.creatingParty) return
        if (members.size < partySize && !inQueue) {
            try {
                Http.sendGetRequest(
                    "$API_URL/createParty?uuids=${members.joinToString(",").replace("-", "")}" +
                            "&reqs=$partyReqs" +
                            "&note=$partyNote" +
                            "&type=$partyType" +
                            "&size=$partySize" +
                            "&key=${sboData.sboKey}"
                ).toJsonObject { response ->
                    if (response.getBoolean("Success")) {
                        val timeTaken = System.currentTimeMillis() - createPartyTimeStamp
                        inQueue = true
                        creatingParty = false
                        partyReqsMap = response.getMutableMap("Reqs") ?: mutableMapOf()

                        if (ghostParty) {
                            removePartyFromQueue()
                            ghostParty = false
                        }

                        if (requeue) {
                            requeue = false
                            Chat.chat("§6[SBO] §eClick to dequeue party")
                        }

                        Chat.chat("§6[SBO] §eParty created successfully! " +
                                "Time taken: ${timeTaken}ms, " +
                                "Note: $partyNote, " +
                                "Type: $partyType, " +
                                "Size: $partySize, " +
                                "Reqs: $partyReqs"
                        )

                        if (isInParty) Chat.command("pc [SBO] Party now in queue.");
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

    fun updateParty(isInParty: Boolean, isLeader: Boolean, members: List<String>) {
        if (updateBool && inQueue && isInParty && isLeader) {
            updateBool = false;
            val currentTime = System.currentTimeMillis()
            partyMemberCount = members.size
            if (members.size < partySize) {
                Chat.chat("§6[SBO] §eUpdating party: $partyNote, Type: $partyType, Size: $partySize, Reqs: $partyReqs")

            } else {
                // remove from queue
                inQueue = false
                Chat.chat("§6[SBO] §4Party is full, removing from queue.")
            }
        }
    }

    fun removePartyFromQueue() {
        if (inQueue) {
            inQueue = false
            Http.sendGetRequest("$API_URL/unqueueParty?leaderId=${Player.getUUIDString().replace("-", "")}")
                .result { response ->
                    Chat.chat("§6[SBO] §eParty removed from queue.")
                }.error { error ->
                    Chat.chat("§6[SBO] §4Unexpected error while removing party from queue")
                }
        }
    }

    fun trackRegister() {
        Register.onChatMessage { message ->
            val text = message.string
            leaderChangeRegexes.forEach {
                if (it.matches(text)) {
                    Chat.chat("§6[SBO] §eParty leader changed")
                    removePartyFromQueue()
                }
            }
            partyDisbandRegexes.forEach {
                if (it.matches(text)) {
                    inQueue = false
                    creatingParty = false
                    partyMemberCount = 0
                    trackMemberCount()
                    Chat.chat("§6[SBO] §4Party has been disbanded.")
                }
            }
            partyJoinRegexes.forEach {
                if (it.matches(text)) {
                    updateBool = true
                    partyMemberCount += 1
                    trackMemberCount()
                    Chat.chat("§6[SBO] §eParty member joined")
                }
            }
            partyLeaveRegexes.forEach {
                if (it.matches(text)) {
                    updateBool = true
                    partyMemberCount -= 1
                    trackMemberCount()
                    Chat.chat("§6[SBO] §eParty member left")
                }
            }
        }
    }

    fun trackMemberCount() { // todo: finish this logic
        if (inQueue) {
            if (partyMemberCount >= partySize) {
                Chat.chat("§6[SBO] §4Party is full, removing from queue.")
                removePartyFromQueue()
            } else {
                updateBool = true
            }
        }
    }

    fun checkPartyNote(note: String): String {
        // allowed characters a-z, A-Z, 0-9, space, comma, dot, exclamation mark, hyphen, underscore, question mark
        return note.replace(Regex("[^a-zA-Z0-9 ,.!?\\-_]"), "")
            .take(30)
            .trim()
    }
}