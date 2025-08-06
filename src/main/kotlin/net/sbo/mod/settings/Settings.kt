package net.sbo.mod.settings

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import net.sbo.mod.SBOKotlin
import net.sbo.mod.settings.categories.General
import net.sbo.mod.settings.categories.Customization
import net.sbo.mod.settings.categories.Diana

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
        category(Customization)
        category(Diana)
    }

    init {
        separator {
            this.title = "Test Separator"
            this.description = "This is a test separator to demonstrate the functionality of the settings system."
        }
    }

    fun save() = SBOKotlin.settings.save()
}