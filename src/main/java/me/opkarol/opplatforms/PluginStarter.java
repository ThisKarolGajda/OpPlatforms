package me.opkarol.opplatforms;

import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.plugins.OpMessagesPlugin;
import me.opkarol.opplatforms.block.AllowedBlocksWithPrice;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderCooldown;
import me.opkarol.opplatforms.commands.PlatformMainCommand;
import me.opkarol.opplatforms.commands.PlatformSettingsCommand;
import me.opkarol.opplatforms.events.PlayerQuit;
import me.opkarol.opplatforms.events.WandClick;
import me.opkarol.opplatforms.inventories.BlockChangeInventory;
import me.opkarol.opplatforms.inventories.PlatformConfirmInventory;
import me.opkarol.opplatforms.inventories.PlatformMainInventory;
import me.opkarol.opplatforms.inventories.PlatformSettingsInventory;

import java.util.UUID;

public final class PluginStarter extends OpMessagesPlugin {
    private final OpMap<UUID, BlockBuilder> map = new OpMap<>();
    private final InventoriesHolder holder = new InventoriesHolder();
    private AllowedBlocksWithPrice blocksWithPrice;

    @Override
    public void enable() {
        getMap().getConfiguration().updateConfig();
        new BlockBuilderCooldown();

        blocksWithPrice = new AllowedBlocksWithPrice(this);

        new PlayerPlatformSettings(this);
        new PlatformSettingsCommand(holder, blocksWithPrice);
        new PlatformMainCommand(this);

        new WandClick(this);
        new PlayerQuit(this);

        holder.addInventory("BlockChangeInventory", new BlockChangeInventory(blocksWithPrice));
        holder.addInventory("PlatformSettingsInventory", new PlatformSettingsInventory(holder));
        holder.addInventory("PlatformMainInventory", new PlatformMainInventory(holder));
        holder.addInventory("PlatformConfirmInventory", new PlatformConfirmInventory(this));
    }

    @Override
    public void disable() { }

    public OpMap<UUID, BlockBuilder> getBlockMap() {
        return map;
    }

    public InventoriesHolder getInventoriesHolder() {
        return holder;
    }

    public AllowedBlocksWithPrice getBlocksWithPrice() {
        return blocksWithPrice;
    }
}
