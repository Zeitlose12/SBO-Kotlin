package net.sbo.mod.utils

import net.sbo.mod.SBOKotlin
import net.minecraft.scoreboard.*
import java.lang.String.CASE_INSENSITIVE_ORDER

object ScoreBoard {
    private val COMPARATOR: Comparator<ScoreboardEntry> = Comparator.comparing { obj: ScoreboardEntry -> obj.value() }
        .reversed()
        .thenComparing({ obj: ScoreboardEntry -> obj.owner() }, CASE_INSENSITIVE_ORDER);

    fun getLines(): List<String> {
        val scoreboard = SBOKotlin.mc.world?.scoreboard ?: return emptyList()
        val objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return emptyList()

        return scoreboard.getScoreboardEntries(objective)
            .filter { entry -> entry?.owner != null && !entry.hidden() }
            .sortedWith(COMPARATOR)
            .take(15)
            .map { entry ->
                Team.decorateName(
                    scoreboard.getScoreHolderTeam(entry.owner()),
                    entry.name()
                ).string
            }
            .map { decoratedText ->
                decoratedText.replace("§[^a-f0-9]".toRegex(), "")
            }
            .asReversed()
    }
}