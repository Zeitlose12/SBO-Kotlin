package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object PartyFinder : CategoryKt("PartyFinder") {
    var autoInvite by boolean(true) {
        this.name = Translated("Auto Invite")
        this.description = Translated("Auto invites players that send you a join request and meet the party requirements")
    }

    var autoRequeue by boolean(false) {
        this.name = Translated("Auto Requeue")
        this.description = Translated("Automatically requeues the party after a member leaves")
    }

    var scaleText by float(0f) {
        this.name = Translated("Text Scale")
        this.description = Translated("Change the size of the text")
        this.range = -2f..2f
        this.slider = true
    }

    var scaleIcon by float(0f) {
        this.name = Translated("Icon Scale")
        this.description = Translated("Change the size of the icons")
        this.range = -20f..20f
        this.slider = true
    }
}