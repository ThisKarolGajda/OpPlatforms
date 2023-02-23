package me.opkarol.opplatforms.blockbuilder;


import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opc.api.list.TaskQueue;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opplatforms.PlayerPlatformSetting;
import me.opkarol.opplatforms.effects.ParticleManager;
import me.opkarol.opplatforms.effects.RunnableList;
import me.opkarol.opplatforms.wand.WandItem;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;
import static me.opkarol.opc.api.utils.MathUtils.removeItems;
import static me.opkarol.opplatforms.blockbuilder.BlockBuilderRequirements.*;

public class BlockBuilder {
    private Vector vector1, vector2;
    private PlayerPlatformSetting setting;
    private World world;
    private TaskQueue<Block> queue = new TaskQueue<>();
    private boolean fakeBlocksChanged;
    private Player player;
    private final RunnableList runnableList = new RunnableList();
    private int blockCost;

    public BlockBuilder(World world, Vector vector1, Vector vector2, PlayerPlatformSetting setting, Player player) {
        this.vector1 = vector1;
        this.vector2 = vector2;
        this.setting = setting;
        this.player = player;
        this.world = world;
        this.queue = calculateQueue();
    }

    public BlockBuilder() {
    }

    public boolean canBeBuild() {
        if (!hasMoney(player, getRequiredMoney())) {
            sendMappedMessage(player, "notEnoughMoney",
                    Tuple.of("%money%", String.valueOf(getRequiredMoney()))
            );
            return false;
        }
        if (!isMaterialCorrect(setting.getBlock())) {
            sendMappedMessage(player, "selectedBlockInvalid");
            return false;
        }
        if (!hasCorrectSpeed(setting.speed())) {
            sendMappedMessage(player, "selectedSpeedInvalid");
            return false;
        }
        if (!containsEnoughMaterial(player, setting.getBlock(), queue.size())) {
            sendMappedMessage(player, "notEnoughMaterials");
            return false;
        }
        if (isTooLarge(getQueueSize())) {
            sendMappedMessage(player, "tooLargePlatform");
            return false;
        }
        if (isEmptyPlatform(getQueueSize())) {
            sendMappedMessage(player, "emptyPlatform");
            return false;
        }
        return checkDifferentHeight(player);
    }

    public boolean checkDifferentHeight(Player player) {
        if (isDifferentHeight(vector1, vector2)) {
            sendMappedMessage(player, "differentYAxis");
            return false;
        }
        return true;
    }

    public void build(ItemStack wand) {
        removeItems(player.getInventory(), setting.getBlock(), queue.size());
        Vault.getInstance().withdraw(player, getRequiredMoney());
        boolean shouldBeRemoved = WandItemData.damageWand(wand, getQueueSize());
        if (shouldBeRemoved) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            sendMappedMessage(player, "wandBroke");
        } else {
            ItemStack item = player.getInventory().getItemInMainHand();
            WandItem wandItem = new WandItem(item);
            player.getInventory().setItemInMainHand(wandItem.getItemBuilder().generate());
        }

        BlockBuilderTool.startBuilderLoop(this);
    }

    public TaskQueue<Block> calculateQueue() {
        if (!areBothFilled()) {
            return new TaskQueue<>();
        }

        TaskQueue<Block> temp = new TaskQueue<>();
        int topBlockX = Math.max(vector1.getBlockX(), vector2.getBlockX());
        int bottomBlockX = Math.min(vector1.getBlockX(), vector2.getBlockX());

        int topBlockZ = Math.max(vector1.getBlockZ(), vector2.getBlockZ());
        int bottomBlockZ = Math.min(vector1.getBlockZ(), vector2.getBlockZ());

        int y = vector1.getBlockY();

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                Block block1 = world.getBlockAt(x, y, z);
                if (block1.getType().isAir() || !block1.getType().isSolid()) {
                    temp.addLast(block1);
                }
            }
        }
        return temp;
    }

    public void setQueue() {
        this.queue = calculateQueue();
    }

    public void setVector1(Vector vector1) {
        setRunnableToVector(vector1, new Particle.DustOptions(Color.RED, 1), true);
        this.vector1 = vector1;
    }

    public void setVector2(Vector vector2) {
        setRunnableToVector(vector2, new Particle.DustOptions(Color.BLUE, 1), false);
        this.vector2 = vector2;
    }

    private void setRunnableToVector(Vector vector, Particle.DustOptions dustOptions, boolean isVector1) {
        this.fakeBlocksChanged = true;
        OpRunnable runnable = new OpRunnable(r -> ParticleManager.place(vector.toLocation(world), dustOptions));
        runnableList.add(runnable, isVector1);
        new OpRunnable(r -> runnable.cancelTask()).runTaskLater(1200L);
    }

    public void setSetting(PlayerPlatformSetting setting) {
        this.setting = setting;
    }

    public boolean setWorld(World world) {
        if (this.world == null ||
                this.world.getName().equals(world.getName())) {
            this.world = world;
            return true;
        } else if (this.world != null &&
                !this.world.getName().equals(world.getName())){
            this.world = world;
            clearVectors();
            getRunnableList().stop();
            return true;
        }
        return false;
    }

    public boolean areBothFilled() {
        return vector1 != null && vector2 != null;
    }

    public double getRequiredMoney() {
        return setting.speed() * blockCost * queue.size();
    }

    public void sendFakeBlocks(Player player) {
        this.player = player;
        fakeBlocksChanged = true;
        for (Block block : queue.getQueue()) {
            player.sendBlockChange(block.getLocation(), Material.GLASS.createBlockData());
        }
    }

    public void clearFakeBlocks(Player player) {
        this.player = player;
        fakeBlocksChanged = false;
        for (Block block : queue.getQueue()) {
            player.sendBlockChange(block.getLocation(), block.getBlockData());
        }
    }

    public boolean getFakeBlocksChanged() {
        return fakeBlocksChanged;
    }

    public void setFakeBlocksChanged(boolean fakeBlocksChanged) {
        this.fakeBlocksChanged = fakeBlocksChanged;
    }

    public RunnableList getRunnableList() {
        return runnableList;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public PlayerPlatformSetting getSetting() {
        return setting;
    }

    public int getBlockCost() {
        return blockCost;
    }

    public void setBlockCost(int blockCost) {
        this.blockCost = blockCost;
    }

    public boolean checkIfVectorsAreTheSame() {
        if (vector1 == null || vector2 == null) {
            return false;
        }

        return vector1.getBlockX() == vector2.getBlockX() &&
                vector1.getBlockY() == vector2.getBlockY() &&
                vector1.getBlockZ() == vector2.getBlockZ();

    }

    public void clearVectors() {
        vector1 = null;
        vector2 = null;
    }

    public TaskQueue<Block> getQueue() {
        return queue;
    }

    public Player getPlayer() {
        return player;
    }
}