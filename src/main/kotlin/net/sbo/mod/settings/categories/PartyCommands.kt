package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object PartyCommands : CategoryKt("Party Commands") {
    var warpCommand by boolean(false) {
        this.name = Translated("Warp Party")
        this.description = Translated("!w, !warp")
    }

    var allinviteCommand by boolean(false) {
        this.name = Translated("Allinvite")
        this.description = Translated("!allinv, !allinvite")
    }

    var transferCommand by boolean(false) {
        this.name = Translated("Party Transfer")
        this.description = Translated("!transfer [Player] (if no player is defined it transfers the party to the command writer)")
    }

    var moteCommand by boolean(false) {
        this.name = Translated("Promote/Demote")
        this.description = Translated("!promote/demote [Player] (if no player is defined it pro/demotes the command writer)")
    }

    var carrotCommand by boolean(false) {
        this.name = Translated("Ask Carrot")
        this.description = Translated("Enable !carrot Command")
    }

    var timeCommand by boolean(false) {
        this.name = Translated("Time Check")
        this.description = Translated("Sends your time in party chat (!time)")
    }

    var tpsCommand by boolean(false) {
        this.name = Translated("Check Tps")
        this.description = Translated("Sends the server tps in party chat (!tps)")
    }

    var dianaPartyCommands by boolean(true) {
        this.name = Translated("Diana Party Commands")
        this.description = Translated("Enable Diana party commands (!chim, !inq, !relic, !stick, !since, !burrow, !mob) (note: you need to have Diana tracker enabled)")
    }
}