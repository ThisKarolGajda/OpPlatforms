package me.opkarol.opplatforms.inventories;

import me.opkarol.opc.api.gui.OpInventory;
import me.opkarol.opc.api.gui.holder.IInventoryHolder;
import me.opkarol.opc.api.gui.inventory.InventoryFactory;
import me.opkarol.opc.api.gui.items.InventoryItem;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderTool;
import me.opkarol.opplatforms.wand.Wand;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.Optional;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class PlatformConfirmInventory implements IInventoryHolder {
    private final OpInventory inventory;

    public PlatformConfirmInventory(PluginStarter pluginStarter) {
        InventoryFactory factory = new InventoryFactory(27, "#<5389FD>&lPotwiedź budowę");
        this.inventory = new OpInventory(factory);

        InventoryItem CONFIRM_BUTTON = new InventoryItem(Material.GREEN_CONCRETE, event -> {
            event.setCancelled(true);
            event.close();
            Player player = (Player) event.getPlayer();
            if (!WandItemData.hasWandPdc(player.getInventory().getItemInMainHand())) {
                sendMappedMessage(player, "wandError");
                return;
            }

            BlockBuilderTool.startBuilder(player);
        })
                .name("&a&lPotwierdź budowę platformy");

        InventoryItem DECLINE_BUTTON = new InventoryItem(Material.RED_CONCRETE, e -> {
            Player player = (Player) e.getPlayer();
            WandItemData.getWandUUID(e.getPlayer().getInventory().getItemInMainHand()).ifPresent(uuid -> {
                Optional<Wand> optional = pluginStarter.getWandDatabase().getWand(uuid);
                optional.ifPresent(wand -> BlockBuilderTool.stopPreTasks(player, wand.getBuilder()));
                pluginStarter.getActiveBlockBuilder().remove(player.getUniqueId());
                pluginStarter.getWandDatabase().remove(player);
                sendMappedMessage(player, "cancelledPlatform");
            });
            e.setCancelled(true);
            e.close();
        })
                .name("&c&lOdrzuć budowę platformy");

        InventoryItem INFORMATION_BUTTON = new InventoryItem(Material.BOOK, e -> e.setCancelled(true))
                .name("#<5389FD>&lPodsumowanie")
                .lore(Arrays.asList("&7Wybrany blok: #<5389FD>%block%&7,", "&7Wybrana szybkość: #<5389FD>%speed%&7,", "&7Ilość bloków: #<5389FD>%block_amount%&7,", "&7Cena za blok: #<5389FD>%block_cost%$&7,", "&7Całkowity koszt: #<5389FD>%cost%$&7."));

        InventoryItem BLANK_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, e -> e.setCancelled(true))
                .name("&k")
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        inventory.set(CONFIRM_BUTTON, 11);
        inventory.set(INFORMATION_BUTTON, 13);
        inventory.set(DECLINE_BUTTON, 15);
        inventory.setAllUnused(0, BLANK_ITEM);
        inventory.build();
    }

    @Override
    public OpInventory getInventory() {
        return inventory;
    }
}
