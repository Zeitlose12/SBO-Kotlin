package net.sbo.mod.diana

object DianaTracker {
    private var isDianaActive: Boolean = true // replaces checkDiana
    private var lastDianaMobDeath: Long = 0L // replaces mobDeath2SecsTrue and mobDeath4SecsTrue
    private var gotLootShare: Boolean = false

    private val rareDrops = listOf("DWARF_TURTLE_SHELLMET", "CROCHET_TIGER_PLUSHIE", "ANTIQUE_REMEDIES", "MINOS_RELIC") // not trackable by chat
    private val otherDrops = listOf("ENCHANTED_ANCIENT_CLAW", "ANCIENT_CLAW", "ENCHANTED_GOLD", "ENCHANTED_IRON")
    private val forbiddenCoins = listOf(1, 5, 20, 1000, 2000, 3000, 4000, 5000, 7500, 8000, 10000, 12000, 15000, 20000, 25000, 40000, 50000)

    fun init() {

    }

    fun trackWithPickuplog(item: String, amount: Int) {

    }

    fun trackItem(item: String, amount: Int) {

    }

}