package me.opkarol.opplatforms.inventories;

import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opc.api.utils.StringUtil;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import me.opkarol.opplatforms.PluginStarter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class SpeedChangeAnvilInventory {

    public SpeedChangeAnvilInventory(Player viewer) {
        new AnvilGUI.Builder()
                .onComplete((completion) -> {
                    int i = StringUtil.getIntFromString(completion.getText());
                    if (i >= 1) {
                        PlayerPlatformSettings.getSettings()
                                .setSetting(viewer.getUniqueId(), i);
                        sendMappedMessage(viewer, "changedValueOfSetting", Tuple.of("%setting%", "[SZYBKOSC]"), Tuple.of("%value%", String.valueOf(i)));
                        return List.of(AnvilGUI.ResponseAction.close());
                    }
                    return List.of(AnvilGUI.ResponseAction.replaceInputText("Zła wartość"));
                })
                .plugin(PluginStarter.getInstance())
                .title(FormatUtils.formatMessage("&9&lWpisz prędkość"))
                .text(">")
                .itemLeft(new ItemStack(Material.WRITABLE_BOOK))
                .open(viewer);
    }
}
