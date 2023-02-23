package me.opkarol.opplatforms.inventories;

import me.opkarol.opc.api.gui.OpInventory;
import me.opkarol.opc.api.gui.holder.IInventoryHolder;
import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.gui.inventory.InventoryFactory;
import me.opkarol.opc.api.gui.items.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;

public class PlatformSettingsInventory implements IInventoryHolder {
    private final OpInventory inventory;

    public PlatformSettingsInventory(InventoriesHolder inventoriesHolder) {
        InventoryFactory holder = new InventoryFactory(27, "#<5389FD>&lUstawienia platform");
        this.inventory = new OpInventory(holder);

        InventoryItem BLOCK_CHANGER_ITEM = new InventoryItem(Material.STONE_BRICKS,
                e -> {
                    e.setCancelled(true);
                    inventoriesHolder.getInventory("BlockChangeInventory")
                            .ifPresent(inventory -> inventory.getInventory().openInventory(e.getPlayer()));
                })
                .name("#<5389FD>&lZmień blok platform")
                .lore(Arrays.asList("&7Blok platformy jest to materiał", "&7z którego będą budowane platformy.", "", "&7Naciśnij #<5389FD>LPM&7, a otworzy Ci się", "&7ekwipunek, który ma możliwość zmiany bloku.", "", "#<5389FD>Niektóre bloki są zabronione&7, dlatego", "&7nie będziesz mógł ich użyć.", "", "&7Pamiętaj: #<5389FD>Każdy blok ma swoją własną cenę&7."));

        InventoryItem BLOCK_SPEED_ITEM = new InventoryItem(Material.SPECTRAL_ARROW,
                e -> {
                    e.setCancelled(true);
                    new SpeedChangeAnvilInventory((Player) e.getPlayer());
                })
                .name("#<5389FD>&lZmień szybkość stawiania")
                .lore(Arrays.asList("&7Szybkość jest to wartość z którą", "&7będzie budowana twoja platforma.", "", "&7Naciskąjać #<5389FD>LPM&7 otworzy Ci się", "&7pole w którym możesz zmienić prędkość.", "", "#<5389FD>Wraz ze wzrostem szybkości", "#<5389FD>zwiększa się też koszt budowy&7."));

        InventoryItem BLOCK_SUMMARY_ITEM = new InventoryItem(Material.BOOK, e -> e.setCancelled(true))
                .lore(Arrays.asList("&7Twoje ustawienia możesz zmienić", "&7poprzez naciśnięcie w przyciski obok&7.", "", "&7Wybrany blok: #<5389FD>%block%&7.", "&7Wybrana prędkość: #<5389FD>%speed%&7."))
                .name("#<5389FD>&lObecne ustawienia");

        InventoryItem BLOCK_BACK_ITEM = new InventoryItem(Material.BARRIER, e -> inventoriesHolder.getInventory("PlatformMainInventory")
                    .ifPresent(inventory -> inventory.getInventory().openBestInventory(e.getPlayer())))
                .name("#<FFC0CB>&lPowrót");

        InventoryItem BLANK_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, e -> e.setCancelled(true))
                .name("&k")
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        inventory.set(BLOCK_CHANGER_ITEM, 10);
        inventory.set(BLOCK_SPEED_ITEM, 13);
        inventory.set(BLOCK_SUMMARY_ITEM, 16);
        inventory.set(BLOCK_BACK_ITEM, 26);
        inventory.setAllUnused(0, BLANK_ITEM);
    }

    @Override
    public OpInventory getInventory() {
        return inventory;
    }
}
