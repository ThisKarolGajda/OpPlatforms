package me.opkarol.opplatforms.events;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import me.opkarol.opc.api.event.EventRegister;
import me.opkarol.opc.api.misc.CooldownModule;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderCooldown;
import me.opkarol.opplatforms.wand.Wand;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;
import static me.opkarol.opplatforms.blockbuilder.BlockBuilderTool.stopPreTasks;

public class WandClick {

    public WandClick(PluginStarter pluginStarter) {
        CooldownModule<UUID> cooldown = BlockBuilderCooldown.getCooldownModule();
        EventRegister.registerEvent(PlayerInteractEvent.class, event -> {
            if (event.getClickedBlock() == null || event.getItem() == null) {
                return;
            }
            ItemStack item = event.getItem();
            Player player = event.getPlayer();
            if (!WandItemData.hasWandPdc(item)) {
                return;
            }

            event.setCancelled(true);
            Optional<UUID> optional = WandItemData.getWandUUID(item);
            if (optional.isEmpty()) {
                return;
            }

            UUID uuid = optional.get();

            if (pluginStarter.getActiveBlockBuilder().contains(uuid)) {
                sendMappedMessage(player, "alreadyBuildingPlatform");
                return;
            }

            if (cooldown.getLeftCooldown(uuid) > 0) {
                sendMappedMessage(player, "cooldownActive", Tuple.of("%cooldown%", String.valueOf(cooldown.getLeftCooldown(uuid))));
                return;
            }

            BlockBuilder builder;
            Optional<Wand> optionalWand = pluginStarter.getWandDatabase().getPlayerActiveWand(player.getUniqueId());
            if (optionalWand.isPresent()) {
                Wand wand = optionalWand.get();
                if (!wand.getWandUUID().equals(uuid)) {
                    sendMappedMessage(player, "invalidWand");
                    return;
                }
                builder = wand.getBuilder();
            } else {
                builder = new BlockBuilder();
            }


            Optional<Island> optionalIsland = IridiumSkyblockAPI.getInstance().getIslandViaLocation(event.getClickedBlock().getLocation());
            if (optionalIsland.isEmpty()) {
                sendMappedMessage(player, "dontHavePermission");
                return;
            }

            if (!optionalIsland.get().getMembers().stream().map(User::getUuid).toList().contains(uuid)) {
                sendMappedMessage(player, "dontHavePermission");
                return;
            }

            Vector vector = event.getClickedBlock().getLocation().toVector();
            if (builder.setWorld(player.getWorld())) {
                int vectorIndex = switch (event.getAction()) {
                    case LEFT_CLICK_BLOCK -> {
                        builder.setVector1(vector);
                        yield 1;
                    }
                    case RIGHT_CLICK_BLOCK -> {
                        builder.setVector2(vector);
                        yield 2;
                    }
                    default -> 0;
                };

                if (!builder.checkDifferentHeight(player)) {
                    stopPreTasks(player, builder);
                    return;
                }

                if (builder.checkIfVectorsAreTheSame()) {
                    sendMappedMessage(player, "theSameLocations");
                    stopPreTasks(player, builder);
                    return;
                }

                sendMappedMessage(player, "changedBlockVector", Tuple.of("%block_number%", String.valueOf(vectorIndex)), Tuple.of("%location%", vector.toString()));
                Optional<Integer> blockCost = pluginStarter.getBlocksWithPrice().getFromUUID(player.getUniqueId());
                if (blockCost.isEmpty()) {
                    return;
                }

                builder.setBlockCost(blockCost.get());
                builder.setQueue();
                pluginStarter.getWandDatabase().getMap().set(uuid, new Wand(uuid, player.getUniqueId(), builder));
                if (builder.areBothFilled() && builder.getQueueSize() == 0) {
                    sendMappedMessage(player, "emptyPlatform");
                    stopPreTasks(player, builder);
                    return;
                }

                if (builder.areBothFilled()) {
                    cooldown.addCooldown(uuid);
                    new OpRunnable(r -> {
                        sendFakeBlock(builder, player);
                    }).runTask();
                    sendMappedMessage(player, "bothSelected",
                            Tuple.of("%amount%", String.valueOf(builder.getQueueSize())),
                            Tuple.of("%money%", String.valueOf(builder.getRequiredMoney()))
                    );
                }
            }
            builder.setSetting(PlayerPlatformSettings.getSettings().getSafeSetting(player.getUniqueId()));
        });
    }

    public void sendFakeBlock(@NotNull BlockBuilder builder, Player player) {
        builder.sendFakeBlocks(player);
        builder.setFakeBlocksChanged(false);
        new OpRunnable(r -> {
            if (builder.getFakeBlocksChanged()) {
                r.cancel();
                sendFakeBlock(builder, player);
            } else {
                builder.clearFakeBlocks(player);
            }
        }).runTaskLater(200L);
    }
}
