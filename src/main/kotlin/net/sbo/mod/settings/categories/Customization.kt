package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import java.awt.Color

object Customization : CategoryKt("Customization") {

    init {
        separator {
            this.title = "Waypoint Customization"
        }
    }

    var guessColor by color(
        Color(0.0f, 0.964f, 1.0f).rgb) {
        this.name = Translated("Guess Color")
        this.description = Translated("Pick a color for your guess")
        this.allowAlpha = true
    }

    var MobColor by color(
        Color(1.0f, 0.333f, 0.333f).rgb) {
        this.name = Translated("Mob Burrow Color")
        this.description = Translated("Pick a color for mob burrows")
        this.allowAlpha = true
    }

    var TreasureColor by color(
        Color(1f, 0.666f, 0.0f).rgb) {
        this.name = Translated("Treasure Burrow Color")
        this.description = Translated("Pick a color for treasure burrows")
        this.allowAlpha = true
    }

    var StartColor by color(
        Color(0.333f, 1.0f, 0.333f).rgb) {
        this.name = Translated("Start Burrow Color")
        this.description = Translated("Pick a color for start burrows")
        this.allowAlpha = true
    }

    init {
        separator {
            this.title = "Waypoint Text Customization"
        }
    }

    var waypointTextShadow by boolean(true) {
        this.name = Translated("Waypoint Text Shadow")
        this.description = Translated("Enables shadow for waypoint text")
    }

    var waypointTextScale by float(0.7f) {
        this.name = Translated("Waypoint Text Scale")
        this.description = Translated("Scale of the waypoint text")
        this.range = 0.5f..2.0f
        this.slider = true
    }
}