package me.opkarol.opplatforms.inventories;

import me.opkarol.opc.api.gui.OpInventory;
import me.opkarol.opc.api.gui.holder.IInventoryHolder;
import me.opkarol.opc.api.gui.inventory.PagedInventoryFactory;
import me.opkarol.opc.api.gui.items.InventoryItem;
import me.opkarol.opc.api.gui.items.InventoryItemSpecialData;
import me.opkarol.opplatforms.block.AllowedBlocksWithPrice;
import me.opkarol.opplatforms.block.BlockInventoryObject;
import org.bukkit.Material;

import java.util.stream.Collectors;

public class BlockChangeInventory implements IInventoryHolder {
    private final OpInventory inventory;

    public BlockChangeInventory(AllowedBlocksWithPrice blocksWithPrice) {
        PagedInventoryFactory holder = new PagedInventoryFactory(27, "#<5389FD>&lWybierz blok!");
        this.inventory = new OpInventory(holder);

        InventoryItem nextPageItem = new InventoryItem(Material.BARRIER)
                .name("&bNastępna strona");
        nextPageItem.addSpecialData(InventoryItemSpecialData.PAGED_INVENTORY_BUTTON_NEXT);

        InventoryItem previousPageItem = new InventoryItem(Material.BARRIER)
                .name("&bPoprzednia strona");
        previousPageItem.addSpecialData(InventoryItemSpecialData.PAGED_INVENTORY_BUTTON_PREVIOUS);

        this.inventory.setInventoryObjects(blocksWithPrice.getMap().keySet().stream()
                .map(string -> new BlockInventoryObject(string, blocksWithPrice.getMap().unsafeGet(string)))
                .collect(Collectors.toList()));

        inventory.setGlobalItem(previousPageItem, 18);
        inventory.setGlobalItem(nextPageItem, 26);
        inventory.build();
    }

    @Override
    public OpInventory getInventory() {
        return this.inventory;
    }
}
