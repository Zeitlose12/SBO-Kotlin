package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin
import net.sbo.mod.utils.http.Http
import java.util.*
import kotlin.math.floor
import kotlin.math.round
import net.sbo.mod.utils.data.MayorResponse

object MayorState {
    var dateMayorElected: Date? = null
    var newMayorAtDate: Date? = null
    var mayor: String? = null
    var perks: MutableSet<String> = mutableSetOf()
    var mayorApiError: Boolean = false
    var apiLastUpdated: Long? = null
    var minister: String? = null
    var ministerPerk: String? = null
    var skyblockDate: Date? = null
    var skyblockDateString: String = ""
    var refreshingMayor: Boolean = false
    var newMayor: Boolean = false
    var outDatedApi: Boolean = false
    var sbYear: Int = 0
}

object Mayor {
    fun init() {
        Register.onTick(20) {
            if (World.isInSkyblock()) {
                MayorState.skyblockDateString = calcSkyblockDate(System.currentTimeMillis())
                if (MayorState.skyblockDateString.isNotEmpty()) {
                    MayorState.skyblockDate = convertStringToDate(MayorState.skyblockDateString)
                    if (MayorState.newMayorAtDate == null || MayorState.newMayorAtDate!!.time < MayorState.skyblockDate!!.time) {
                        MayorState.newMayor = true
                        val calendar = Calendar.getInstance()
                        calendar.time = MayorState.skyblockDate!!
                        val currentYear = calendar.get(Calendar.YEAR)
                        val compareDate = convertStringToDate("27.3.$currentYear")
                        if (compareDate.time > MayorState.skyblockDate!!.time) {
                            MayorState.dateMayorElected = convertStringToDate("27.3.${currentYear - 1}")
                            MayorState.newMayorAtDate = convertStringToDate("27.3.$currentYear")
                        } else {
                            MayorState.dateMayorElected = convertStringToDate("27.3.$currentYear")
                            MayorState.newMayorAtDate = convertStringToDate("27.3.${currentYear + 1}")
                        }
                    }
                    val calendar = Calendar.getInstance()
                    calendar.time = MayorState.skyblockDate!!
                    MayorState.sbYear = calendar.get(Calendar.YEAR)
                }

                if (MayorState.skyblockDate != null) {
                    if ((MayorState.mayor === null || MayorState.mayorApiError || MayorState.newMayor || MayorState.outDatedApi) && !MayorState.refreshingMayor) {
                        getMayor()
                        MayorState.newMayor = false
                    }
                }
            }
        }
    }

    internal fun getMayor() {
        MayorState.mayor = null
        MayorState.perks.clear()
        MayorState.refreshingMayor = true
        SBOKotlin.logger.info("Attempting to fetch mayor data...")

        Http.sendGetRequest("https://api.hypixel.net/resources/skyblock/election")
            .toJson<MayorResponse> { response ->
                SBOKotlin.logger.info("Successfully received API response.")
                MayorState.refreshingMayor = false

                if (response.success) {
                    MayorState.apiLastUpdated = response.lastUpdated
                    MayorState.mayor = response.mayor.name
                    MayorState.perks = response.mayor.perks.map { it.name }.toMutableSet()
                    MayorState.minister = response.mayor.minister?.name
                    MayorState.ministerPerk = response.mayor.minister?.perk?.name

                    SBOKotlin.logger.info("Found mayor: ${MayorState.mayor} with perks: ${MayorState.perks.joinToString()}")
                    SBOKotlin.logger.info("Minister: ${MayorState.minister} with perk: ${MayorState.ministerPerk}")

                    MayorState.mayorApiError = false
                    MayorState.apiLastUpdated?.let { apiTimeStamp ->
                        val apiDate = convertStringToDate(calcSkyblockDate(apiTimeStamp))
                        if (MayorState.dateMayorElected == null || apiDate.time >= MayorState.dateMayorElected!!.time) {
                            MayorState.outDatedApi = false
                        } else {
                            MayorState.outDatedApi = true
                            MayorState.mayor = "Diana"
                            MayorState.perks = mutableSetOf("Mythological Ritual")
                        }
                    }
                } else {
                    val errorMessage = response.error ?: "Unknown error"
                    Chat.chat("§cError getting mayor from API: $errorMessage")
                    SBOKotlin.logger.error("API error: $errorMessage")
                    MayorState.mayorApiError = true
                    MayorState.mayor = "Diana"
                    MayorState.perks = mutableSetOf("Mythological Ritual")
                }
            }
            .error { error ->
                MayorState.mayorApiError = true
                MayorState.mayor = "Diana"
                MayorState.perks = mutableSetOf("Mythological Ritual")
                Chat.chat("§cError getting mayor from API: ${error.message}")
                SBOKotlin.logger.error("Error getting mayor from API: ${error.message}")
                MayorState.refreshingMayor = false
            }
    }

    // ... (convertStringToDate und calcSkyblockDate bleiben unverändert)
    private fun convertStringToDate(string: String): Date {
        val parts = string.split(".")
        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val year = parts[2].toInt()
        return GregorianCalendar(year, month, day).time
    }

    private fun calcSkyblockDate(date: Long): String {
        val monthsInYear = 12
        val secondsPerMinute = 0.8333333333333334
        val secondsPerMonth = 37200.0

        val unix = floor(date.toDouble() / 1000).toLong()
        var secondsSinceLastLog = unix - 1560276000L

        var year = 1
        var month = 1
        var day = 1
        var hour = 6
        var minute = 0

        val secondsPerYear = secondsPerMonth * monthsInYear
        val secondsPerDay = 1200.0
        val secondsPerHour = 50.0

        val yearDiff = floor(secondsSinceLastLog / secondsPerYear).toInt()
        secondsSinceLastLog -= yearDiff * secondsPerYear.toLong()
        year += yearDiff

        val monthDiff = floor(secondsSinceLastLog / secondsPerMonth).toInt() % 13
        secondsSinceLastLog -= monthDiff * secondsPerMonth.toLong()
        month = (month + monthDiff) % 13
        if (month == 0) month = 13

        val dayDiff = floor(secondsSinceLastLog / secondsPerDay).toInt() % 32
        secondsSinceLastLog -= dayDiff * secondsPerDay.toLong()
        day = (day + dayDiff) % 32
        if (day == 0) day = 32

        val hourDiff = floor(secondsSinceLastLog / secondsPerHour).toInt() % 24
        secondsSinceLastLog -= hourDiff * secondsPerHour.toLong()
        hour = (hour + hourDiff) % 24

        if (hour < 6) {
            if (day < 31) {
                day += 1
            } else {
                day = 1
                month += 1
            }
        }

        val minuteDiff = floor(secondsSinceLastLog / secondsPerMinute).toInt() % 60
        secondsSinceLastLog -= minuteDiff * secondsPerMinute.toLong()
        minute = (minute + minuteDiff) % 60

        minute = (round(minute / 5.0) * 5).toInt() % 60

        return "$day.$month.$year"
    }
}