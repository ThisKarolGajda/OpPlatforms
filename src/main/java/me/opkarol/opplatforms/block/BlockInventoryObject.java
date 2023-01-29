package me.opkarol.opplatforms.block;

import me.opkarol.opc.api.gui.events.OnItemClicked;
import me.opkarol.opc.api.gui.inventory.IInventoryObject;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.utils.StringUtil;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class BlockInventoryObject implements IInventoryObject {
    private final String block;
    private final int cost;
    public BlockInventoryObject(String block, int cost) {
        this.block = block;
        this.cost = cost;
    }

    @Override
    public String getName() {
        return "#<5389FD>&l" + block.toUpperCase();
    }

    @Override
    public List<String> getLore() {
        return Arrays.asList("&7Koszt postawienia bloku", "&7wynosi #<5389FD>" + cost + "$&7.");
    }

    @Override
    public Material getMaterial() {
        return StringUtil.getMaterialFromString(block);
    }

    @Override
    public Consumer<OnItemClicked> getAction() {
        return e -> {
            e.setCancelled(true);
            e.close();
            String object = e.getItem().getMaterial().toString();
            PlayerPlatformSettings.getSettings()
                    .setSetting(e.getPlayer().getUniqueId(), object);
            sendMappedMessage((Player) e.getPlayer(), "changedValueOfSetting", Tuple.of("%setting%", "[BLOK]"), Tuple.of("%value%", object));
        };
    }
}
