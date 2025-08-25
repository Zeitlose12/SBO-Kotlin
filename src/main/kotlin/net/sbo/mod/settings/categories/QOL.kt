package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object QOL : CategoryKt("QOL") {

    var pickuplogOverlay by boolean(false) {
        this.name = Translated("Pickup Log Overlay")
        this.description = Translated("Displays a pickup log in an overlay like sba. /sboguis to move the overlay")
    }

    var phoenixAnnouncer by boolean(true) {
        this.name = Translated("Phoenix Announcer")
        this.description = Translated("Announces on screen when you drop a phoenix pet")
    }

    var dianaMessageHider by boolean(false) {
        this.name = Translated("Diana Message Hider")
        this.description = Translated("Hides all spammy Diana messages")
    }
}