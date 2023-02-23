package me.opkarol.opplatforms;

import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.plugins.OpMessagesPlugin;
import me.opkarol.opplatforms.block.AllowedBlocksWithPrice;
import me.opkarol.opplatforms.blockbuilder.ActiveBlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderCooldown;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderTool;
import me.opkarol.opplatforms.commands.PlatformMainCommand;
import me.opkarol.opplatforms.events.PlayerQuit;
import me.opkarol.opplatforms.events.WandClick;
import me.opkarol.opplatforms.inventories.BlockChangeInventory;
import me.opkarol.opplatforms.inventories.PlatformConfirmInventory;
import me.opkarol.opplatforms.inventories.PlatformMainInventory;
import me.opkarol.opplatforms.inventories.PlatformSettingsInventory;
import me.opkarol.opplatforms.wand.WandDatabase;

public final class PluginStarter extends OpMessagesPlugin {
    //todo clear saved block builder after item drop

    private final InventoriesHolder holder = new InventoriesHolder();
    private AllowedBlocksWithPrice blocksWithPrice;

    private final ActiveBlockBuilder activeBlockBuilder = new ActiveBlockBuilder();

    private final WandDatabase wandDatabase = new WandDatabase();

    @Override
    public void enable() {
        getMap().getConfiguration().updateConfig();

        new BlockBuilderCooldown();
        new BlockBuilderTool(this);

        blocksWithPrice = new AllowedBlocksWithPrice(this);

        new PlayerPlatformSettings(this);

        new WandClick(this);
        new PlayerQuit(this);

        holder.addInventory("BlockChangeInventory", new BlockChangeInventory(blocksWithPrice));
        holder.addInventory("PlatformSettingsInventory", new PlatformSettingsInventory(holder));
        holder.addInventory("PlatformMainInventory", new PlatformMainInventory(holder));
        holder.addInventory("PlatformConfirmInventory", new PlatformConfirmInventory(this));
    }

    @Override
    public boolean registerCommandsWithBrigadier() {
        getCommandHandler().register(new PlatformMainCommand(this));
        return true;
    }

    @Override
    public void disable() { }

    public InventoriesHolder getInventoriesHolder() {
        return holder;
    }

    public AllowedBlocksWithPrice getBlocksWithPrice() {
        return blocksWithPrice;
    }


    public ActiveBlockBuilder getActiveBlockBuilder() {
        return activeBlockBuilder;
    }

    public WandDatabase getWandDatabase() {
        return wandDatabase;
    }
}
