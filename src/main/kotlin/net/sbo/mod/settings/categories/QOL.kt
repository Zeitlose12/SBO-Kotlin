package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object QOL : CategoryKt("QOL") {

    var phoenixAnnouncer by boolean(true) {
        this.name = Translated("Phoenix Announcer")
        this.description = Translated("Announces on screen when you drop a phoenix pet")
    }
}