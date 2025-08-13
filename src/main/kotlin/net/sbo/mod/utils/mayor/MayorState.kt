package net.sbo.mod.utils.mayor

import java.util.Date

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