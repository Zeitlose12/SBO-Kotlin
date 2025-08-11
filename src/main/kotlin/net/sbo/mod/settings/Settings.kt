package net.sbo.mod.settings

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import net.sbo.mod.SBOKotlin
import net.sbo.mod.settings.categories.General
import net.sbo.mod.settings.categories.Diana
import net.sbo.mod.settings.categories.Slayer
import net.sbo.mod.settings.categories.PartyCommands
import net.sbo.mod.settings.categories.Customization
import net.sbo.mod.settings.categories.QOL
import net.sbo.mod.settings.categories.Credits
import net.sbo.mod.settings.categories.PartyFinder

object Settings : ConfigKt("sbo/config") {
    override val name: TranslatableValue = Literal("SBO v1.0.0")
    override val links: Array<ResourcefulConfigLink> = arrayOf(
        ResourcefulConfigLink.create(
            "https://github.com/SkyblockOverhaul/SBO-Kotlin",
            "code",
            Translated("GitHub")
        )
    )

    init {
        category(General)
        category(Diana)
        category(Slayer)
        category(PartyCommands)
        category(Customization)
        category(PartyFinder)
        category(QOL)
        category(Credits)
    }

    fun save() = SBOKotlin.settings.save()
}