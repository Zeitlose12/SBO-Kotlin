package net.sbo.mod.settings.categories

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import net.sbo.mod.utils.Chat.chat

enum class TestColor {
    RED, GREEN, BLUE, YELLOW, PURPLE
}

object General : CategoryKt("General") {

    init {
        separator {
            this.title = "Overlays"
        }
    }

    var bobberOverlay by boolean(false) {
        this.name = Translated("Bobber Overlay")
        this.description = Translated("Tracks the number of bobbers near you /sboguis to move the overlay")
    }

    var legionOverlay by boolean(false) {
        this.name = Translated("Legion Overlay")
        this.description = Translated("Tracks the players near you for legion buff /sboguis to move the overlay")
    }

//    var test1 by boolean(false) {
//        this.name = Translated("test1")
//        this.description = Translated("test1 description")
//    }
//
//    var test2 by float(1f) {
//        this.range = 0f..5f
//        this.slider = true
//        this.name = Translated("test2")
//        this.description = Translated("test2 description")
//    }
//
//    init {
//        button {
//            title = "testButton"
//            text = "Open"
//            description = "opens the test screen"
//            onClick {
//                chat("This is a test button!")
//            }
//        }
//    }
//
//    var backgroundColor by color(0xC0000000u.toInt()) {
//        this.name = Translated("backgroundColor")
//        this.description = Translated("backgroundColor description")
//        this.allowAlpha = true
//    }
//
//    var testIput by strings("test") {
//        this.name = Translated("testInput")
//        this.description = Translated("testInput description")
//    }
//
//    var singleColorSelector  by enum(TestColor.PURPLE) {
//        this.name = Translated("testSelector")
//        this.description = Translated("testSelector description")
//    }
//
//    var multiColorSelectore by select(TestColor.PURPLE) {
//        this.name = Translated("testMultiSelector")
//        this.description = Translated("testMultiSelector description")
//    }
}