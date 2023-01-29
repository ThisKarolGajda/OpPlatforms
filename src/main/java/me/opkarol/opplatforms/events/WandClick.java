package me.opkarol.opplatforms.events;

import me.opkarol.opc.api.event.EventRegister;
import me.opkarol.opc.api.misc.CooldownModule;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.blockbuilder.ActiveBlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderCooldown;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class WandClick {
    private final ActiveBlockBuilder activeBlockBuilder = ActiveBlockBuilder.getActiveBlockBuilder();

    public WandClick(PluginStarter pluginStarter) {
        CooldownModule<UUID> cooldown = BlockBuilderCooldown.getCooldownModule();
        EventRegister.registerEvent(PlayerInteractEvent.class, event -> {
            if (event.getClickedBlock() == null || event.getItem() == null) {
                return;
            }

            Player player = event.getPlayer();
            if (!WandItemData.hasWandPdc(event.getItem())) {
                return;
            }

            event.setCancelled(true);
            UUID uuid = player.getUniqueId();

            if (activeBlockBuilder.contains(uuid)) {
                sendMappedMessage(player, "alreadyBuildingPlatform");
                return;
            }

            if (cooldown.getLeftCooldown(uuid) > 0) {
                sendMappedMessage(player, "cooldownActive", Tuple.of("%cooldown%", String.valueOf(cooldown.getLeftCooldown(uuid))));
                return;
            }

            BlockBuilder builder = pluginStarter.getBlockMap().getOrDefault(uuid, new BlockBuilder());
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

                if (builder.checkIfVectorsAreTheSame()) {
                    sendMappedMessage(player, "theSameLocations");
                    stopBuilderTask(builder);
                    return;
                }

                sendMappedMessage(player, "changedBlockVector", Tuple.of("%block_number%", String.valueOf(vectorIndex)), Tuple.of("%location%", vector.toString()));
                if (!builder.checkDifferentHeight(player)) {
                    builder.getRunnableList().stop();
                    return;
                }

                Optional<Integer> blockCost = pluginStarter.getBlocksWithPrice().getFromUUID(player.getUniqueId());
                if (blockCost.isEmpty()) {
                    return;
                }
                builder.setBlockCost(blockCost.get());

                builder.setQueue();
                if (builder.areBothFilled() && builder.getQueueSize() == 0) {
                    sendMappedMessage(player, "emptyPlatform");
                    stopBuilderTask(builder);
                    return;
                }

                pluginStarter.getBlockMap().set(uuid, builder);
                if (builder.areBothFilled()) {
                    cooldown.addCooldown(uuid);
                    new OpRunnable(r -> sendFakeBlock(builder, player))
                            .runTask();
                    sendMappedMessage(player, "bothSelected", Tuple.of("%amount%", String.valueOf(builder.getQueueSize())), Tuple.of("%money%", String.valueOf(builder.getRequiredMoney())));
                }
            }
            builder.setSetting(PlayerPlatformSettings.getSettings().getSafeSetting(uuid));
        });
    }

    private void stopBuilderTask(BlockBuilder builder) {
        builder.getRunnableList().stop();
        builder.clearVectors();
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
