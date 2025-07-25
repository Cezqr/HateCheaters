package com.github.subat0m1c.hatecheaters.modules.render

import com.github.subat0m1c.hatecheaters.HateCheaters.Companion.screen
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui
import com.github.subat0m1c.hatecheaters.pvgui.v2.PVGui.loadPlayer
import com.github.subat0m1c.hatecheaters.utils.ChatUtils.setHover
import com.github.subat0m1c.hatecheaters.utils.LogHandler.Logger
import me.odinmain.clickgui.settings.AlwaysActive
import me.odinmain.clickgui.settings.Setting.Companion.withDependency
import me.odinmain.clickgui.settings.impl.*
import me.odinmain.features.Module
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.Colors
import net.minecraft.event.ClickEvent
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@AlwaysActive
object ProfileViewer : Module(
    name = "Profile Viewer",
    description = "Lets you view profiles. /pv name or /hcpv name",
) {
    val pvCommand by BooleanSetting(
        "PV Command",
        default = true,
        desc = "Enabled the /pv command. OVERWRITES NEU PV. When disabled, /hcpv will still work. REQUIRES RESTART"
    )
    val statsPv by BooleanSetting(
        "Use hcpv for stats/messagepv",
        default = false,
        desc = "Uses /hcpv in the stats command instead of /pv."
    )
    val messagePv by BooleanSetting(
        "Message PV",
        default = false,
        desc = "Runs /pv when you click a name in chat. might have lots of compatibility issues."
    )
    val invwalk by BooleanSetting(
        "GUI Move",
        default = false,
        desc = "Lets you move in the gui. USE AT YOUR OWN RISK. It should be fine in theory but hypixel is funky."
    )
    val scale by NumberSetting(
        "Scale",
        default = 1.5,
        increment = 0.1,
        min = 0.1,
        max = 1.5,
        desc = "Scale of the gui."
    )
    val maxRows by NumberSetting(
        "Tali Rows",
        default = 7,
        increment = 1,
        min = 1,
        max = 7,
        desc = "Maximum number of rows that can be displayed in the talisman page. Lower will give more performance, but will render less items."
    )
    private val themesList = arrayListOf("Odin", "Midnight", "Light", "Sunrise", "Custom")
    val themes by SelectorSetting("Theme", default = "Classic", themesList, desc = "Preferred theme")
    val main by ColorSetting(
        "Background",
        default = Colors.gray26,
        true,
        desc = "Color for the background."
    ).withDependency { themes == themesList.lastIndex }
    val font by ColorSetting(
        "Font",
        default = Colors.WHITE,
        true,
        desc = "Font Color (only affects items without vanilla color codes)."
    ).withDependency { themes == themesList.lastIndex }
    val items by ColorSetting(
        "Items",
        default = Colors.gray38,
        true,
        desc = "Background color of items"
    ).withDependency { themes == themesList.lastIndex }
    val line by ColorSetting(
        "Line",
        default = Colors.WHITE,
        true,
        desc = "Separator line color."
    ).withDependency { themes == themesList.lastIndex }
    val code by StringSetting(
        "Code",
        default = "f",
        1,
        desc = "White text color code"
    ).withDependency { themes == themesList.lastIndex }
    val selected by ColorSetting(
        "Selected",
        default = Colors.MINECRAFT_DARK_AQUA,
        true,
        desc = "Color for selected buttons."
    ).withDependency { themes == themesList.lastIndex }
    val button by ColorSetting(
        "Button",
        default = Colors.gray38,
        true,
        desc = "Color for buttons"
    ).withDependency { themes == themesList.lastIndex }
    val roundness by NumberSetting(
        "Roundness",
        default = 10f,
        increment = 0.5,
        min = 0,
        max = 20f,
        desc = "Roundness for the whole gui."
    ).withDependency { themes == themesList.lastIndex }
    val inventoryRound by NumberSetting(
        "Inventory Roundness",
        default = 0f,
        increment = 0.5,
        min = 0,
        max = 20f,
        desc = "Roundness for inventory item backgrounds."
    ).withDependency { themes == themesList.lastIndex }
    val rarityBackgrounds by BooleanSetting(
        "Rarity Background",
        default = false,
        desc = "Renders a background according to the rarity of the item in front of it."
    )

    //https://regex101.com/r/dl1MqJ/1
    private inline val messageRegex get() = Regex("^(((?:Party|Guild|Co-op) >|(?:From|To)|(?:\\[\\d+])?) ?(\\[[^]]*?])? ?(\\w{1,16}) ?(\\[[^]]*?]|[ቾ⚒])?): ?(.+)$")

    @SubscribeEvent
    fun onMessage(event: ClientChatReceivedEvent) {
        if (!messagePv) return
        val (_, _, _, name) = messageRegex.matchEntire(event.message.unformattedText)?.destructured ?: return
        event.message.siblings.find { it.unformattedText.contains(name) }?.chatStyle?.apply {
            chatClickEvent = ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/${if (statsPv) "hcpv" else "pv"} $name"
            )
            setHover(listOf("Click to view ${name}'s profile"))
        }
    }

    val themeEntries = listOf(
        Theme(
            "Odin",
            Colors.gray26,
            Colors.WHITE,
            Colors.gray38,
            Colors.WHITE,
            "f",
            Colors.MINECRAFT_DARK_AQUA,
            Colors.gray38,
            12f,
            0f,
        ),
        Theme(
            "Midnight",
            Color("151345FF"),  // main
            Colors.WHITE,             // font
            Color("1c1d54FF"),  // items
            Color("040622FF"),  // line
            "f",
            Color("26236bFF"),  // selected
            Color("040622FF"),  // button
            10f,
            0f,
        ),
        Theme(
            "Light",
            Colors.WHITE,        // main
            Colors.BLACK,        // font
            Colors.MINECRAFT_DARK_GRAY,    // items
            Colors.MINECRAFT_DARK_GRAY,    // line
            "0",
            Colors.MINECRAFT_GRAY,         // selected
            Colors.MINECRAFT_DARK_GRAY,    // button
            10f,
            0f,
        ),
        Theme(
            "Sunrise",
            Color("fDf1CDFF"), // main
            Color("805690FF"), // font
            Color("f9dc90FF"), // items
            Color("805690FF"), // line
            "b",
            Color("f89e9dFF"), // selected
            Color("d46f93FF"), // button
            10f,
            0f,
        ),
    )

    inline val currentTheme get() = themeEntries.getOrNull(themes) ?: Theme(
        "Custom",
        main,
        font,
        items,
        line,
        code,
        selected,
        button,
        roundness,
        inventoryRound
    )

    override fun onKeybind() {
        launchPV()
    }

    override fun onEnable() {
        launchPV()
        super.onEnable()
        toggle()
    }

}

fun launchPV(name: String? = null, profile: String? = null) {
    loadPlayer(name, profile)
    screen = PVGui
    Logger.info("Trying to display pvgui")
}

data class Theme(
    val name: String,
    val main: Color,
    val font: Color,
    val items: Color,
    val line: Color,
    val fontCode: String,
    val selected: Color,
    val button: Color,
    val roundness: Float,
    val buttons: Float
)