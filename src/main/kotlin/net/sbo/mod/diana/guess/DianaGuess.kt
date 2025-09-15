package net.sbo.mod.diana.guess

import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.utils.events.Register
import net.sbo.mod.utils.math.SboVec

object DianaGuess {
    private val preciseGuess = PreciseGuessBurrow()
    var finalLocation: SboVec? = null
    var lastGuessTime: Long = 0

    fun init() {
        Register.onWorldChange {
            if (!Diana.dianaBurrowGuess) return@onWorldChange
            preciseGuess.onWorldChange()
        }
    }
}