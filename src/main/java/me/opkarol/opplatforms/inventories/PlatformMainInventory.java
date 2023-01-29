package me.opkarol.opplatforms.inventories;

import me.opkarol.opc.api.gui.OpInventory;
import me.opkarol.opc.api.gui.holder.IInventoryHolder;
import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.gui.inventory.InventoryFactory;
import me.opkarol.opc.api.gui.items.InventoryItem;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opplatforms.PlayerPlatformSetting;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;

public class PlatformMainInventory implements IInventoryHolder {
    private final OpInventory inventory;

    public PlatformMainInventory(InventoriesHolder inventoriesHolder) {
        InventoryFactory holder = new InventoryFactory(27, "#<5389FD>&lPlatformy");
        this.inventory = new OpInventory(holder);

        InventoryItem HELP_ITEM = new InventoryItem(Material.BOOK, e -> e.setCancelled(true))
                .name("#<5389FD>&lJak używać platform?")
                .lore(Arrays.asList("&7Narzędzie budowania platform umożliwia Ci", "#<5389FD>szybkie tworzenie platform w kształcie prostokąta&7.", "", "&7Aby móc używać narzędzia #<5389FD>musisz mieć różdzkę&7,", "&7która pozwoli ci wybrać granice platformy.", "", "&7Do wybudowania platformy musisz mieć:", "#<5389FD>• &7liczbę bloków w ekwipunku odpowiadająca rozmiarowi,", "#<5389FD>• &7odpowiednią ilość pieniędzy,", "#<5389FD>• &7niewyczerpaną różdżkę.", "#<5389FD>Jeśli masz wszystko, możesz wybudować platformę!"))
                .enchantments(Tuple.of(Enchantment.MENDING, 1))
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        InventoryItem SETTINGS_ITEM = new InventoryItem(Material.REDSTONE, e -> {
            PlayerPlatformSetting setting = PlayerPlatformSettings.getSettings().getSafeSetting(e.getPlayer().getUniqueId());
            inventoriesHolder.getInventory("PlatformSettingsInventory")
                    .ifPresent(inventory -> inventory.getInventory().open((Player) e.getPlayer(),
                            Tuple.of("%speed%", String.valueOf(setting.speed())),
                            Tuple.of("%block%", setting.block())
                    ));
        })
                .name("#<5389FD>&lUstawienia platform")
                .lore(Arrays.asList("&7Naciśnij #<5389FD>LPM &7aby otworzyć okno #<5389FD>ustawień platform&7.", "&7Możesz tam zmienić wartości takie jak: ", "#<5389FD>• &7szybkość budowy platformy,", "#<5389FD>• &7materiał budowlany."));

        InventoryItem WAND_ITEM = new InventoryItem(Material.WOODEN_AXE, e -> e.setCancelled(true))
                .name("#<5389FD>&lRóźdzka")
                .lore(Arrays.asList("&7Róźdzkę możesz zdobyć poprzez #<5389FD>kupienie jej w sklepie XYZ&7.", "&7Ten magiczny przedmiot #<5389FD>niszczy się z każdym użyciem&7.", "", "&7Chociaż jest ona bardzo potężna, może ona", "#<5389FD>stawiać tylko jedną platformę na raz&7."))
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        InventoryItem BLANK_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, e -> e.setCancelled(true))
                .name("&k")
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        inventory.set(HELP_ITEM, 10);
        inventory.set(SETTINGS_ITEM, 13);
        inventory.set(WAND_ITEM, 16);
        inventory.setAllUnused(0, BLANK_ITEM);

        inventory.build();
    }

    public OpInventory getInventory() {
        return inventory;
    }
}
