package me.opkarol.opplatforms.blockbuilder;

import me.opkarol.opc.api.wrappers.OpBossBar;
import me.opkarol.opc.api.wrappers.OpParticle;
import me.opkarol.opc.api.wrappers.OpSound;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.wand.Wand;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class BlockBuilderTool {
    private static PluginStarter pluginStarter;

    public BlockBuilderTool(PluginStarter pluginStarterNew) {
        pluginStarter = pluginStarterNew;
    }

    public static void startBuilder(Player player) {
        WandItemData.getWandUUID(player.getInventory().getItemInMainHand()).ifPresent(uuid -> {
            Optional<Wand> optional = pluginStarter.getWandDatabase().getWand(uuid);
            if (optional.isPresent()) {
                Wand wand = optional.get();
                BlockBuilder builder = wand.getBuilder();
                UUID playerUUID = player.getUniqueId();
                if (!builder.areBothFilled()) {
                    pluginStarter.getWandDatabase().remove(player);
                    return;
                }

                sendMappedMessage(player, "worker.deletingPastSave");
                stopPreTasks(player, builder);
                if (!builder.canBeBuild()) {
                    pluginStarter.getWandDatabase().remove(player);
                    pluginStarter.getActiveBlockBuilder().remove(playerUUID);
                    builder.clearVectors();
                    return;
                }
                sendMappedMessage(player, "worker.startingWork");
                builder.build(player.getInventory().getItemInMainHand());
            }
        });
    }

    public static void stopPreTasks(Player player, BlockBuilder blockBuilder) {
        blockBuilder.clearFakeBlocks(player);
        blockBuilder.clearVectors();
        blockBuilder.getRunnableList().stop();
    }

    public static void startBuilderLoop(BlockBuilder blockBuilder) {
        Player player = blockBuilder.getPlayer();
        pluginStarter.getActiveBlockBuilder().add(player.getUniqueId());
        OpBossBar bossBar = new OpBossBar("&x&4&7&7&a&f&d&l☁ &fWCZYTYWANIE...", BarColor.BLUE);
        OpParticle particle = new OpParticle().setParticle(Particle.EXPLOSION_LARGE)
                .setAmount(1);
        OpSound eachTimeSound = new OpSound()
                .setSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK);
        OpSound onEndSound = new OpSound()
                .setSound(Sound.BLOCK_ANVIL_LAND);
        int maxSize = blockBuilder.getQueue().size();

        bossBar.addPlayer(player);
        blockBuilder.getQueue().useWithDelay((long) (20L/blockBuilder.getSetting().speed()), block1 -> {
            particle.setLocation(block1.getLocation())
                    .display(player);
            eachTimeSound.play(player, block1.getLocation());
            block1.setType(blockBuilder.getSetting().getBlock());
            int current = (maxSize - blockBuilder.getQueueSize());
            bossBar.getBossBar().setProgress((double) current / (double) maxSize);
            bossBar.setTitle("&fWypełnianie: #<5389FD>" + current + " &f/ #<5389FD>" + maxSize + "");
        }, () -> {
            pluginStarter.getActiveBlockBuilder().remove(player.getUniqueId());
            bossBar.removePlayers();
            if (player.isOnline()) {
                onEndSound.play(player);
                sendMappedMessage(player, "worker.finishedWork");
            }
            blockBuilder.clearVectors();
            pluginStarter.getWandDatabase().remove(player);
        });
    }

    public static boolean isHoldingWand(Player player) {
        return WandItemData.hasWandPdc(player.getInventory().getItemInMainHand());
    }
}
