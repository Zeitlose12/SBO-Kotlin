// src/main/kotlin/net/sbo/mod/settings/SBOSorting.kt

package net.sbo.mod.settings

import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.*
import java.util.Comparator

object Sorting : SortingBehavior() {
    private val categories = listOf(
        "General",
        "Diana",
        "Mining",
        "Party Commands",
        "Customization",
        "Quality of Life",
        "Debug",
        "Credits/Infos"
    )
    override fun getCategoryComparator(): Comparator<Category> {
        return Comparator.comparingInt { categories.indexOf(it.name) }
    }
}