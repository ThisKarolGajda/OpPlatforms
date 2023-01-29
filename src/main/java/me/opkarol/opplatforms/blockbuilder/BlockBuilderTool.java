package me.opkarol.opplatforms.blockbuilder;

import me.opkarol.opc.api.list.TaskQueue;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.misc.opobjects.OpBossBar;
import me.opkarol.opc.api.misc.opobjects.OpParticle;
import me.opkarol.opc.api.misc.opobjects.OpSound;
import me.opkarol.opplatforms.PlayerPlatformSetting;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class BlockBuilderTool {
    private static final ActiveBlockBuilder activeBlockBuilder = ActiveBlockBuilder.getActiveBlockBuilder();

    public static void startBuilder(Player player, OpMap<UUID, BlockBuilder> map) {
        Optional<BlockBuilder> optional = map.getByKey(player.getUniqueId());
        if (optional.isEmpty() || (!optional.get().areBothFilled())) {
            return;
        }

        BlockBuilder blockBuilder = optional.get();
        sendMappedMessage(player, "worker.deletingPastSave");
        stopPreTasks(player, map, blockBuilder);
        if (!blockBuilder.canBeBuild()) {
            return;
        }

        sendMappedMessage(player, "worker.startingWork");
        blockBuilder.build(player.getInventory().getItemInMainHand());
    }

    public static void stopPreTasks(Player player, OpMap<UUID, BlockBuilder> map, BlockBuilder blockBuilder) {
        map.remove(player.getUniqueId());
        blockBuilder.getRunnableList().stop();
        blockBuilder.clearFakeBlocks(player);
    }

    public static void startBuilderLoop(TaskQueue<Block> queue, PlayerPlatformSetting setting, Player player) {
        activeBlockBuilder.add(player.getUniqueId());
        OpBossBar bossBar = new OpBossBar("&x&4&7&7&a&f&d&l☁ &7WCZYTYWANIE...", BarColor.BLUE);
        OpParticle particle = new OpParticle().setParticle(Particle.EXPLOSION_LARGE)
                .setAmount(1);
        OpSound eachTimeSound = new OpSound()
                .setSound(Sound.BLOCK_ANVIL_HIT);
        OpSound onEndSound = new OpSound()
                .setSound(Sound.BLOCK_ANVIL_LAND);
        int maxSize = queue.size();

        bossBar.addPlayer(player);
        queue.useWithDelay((long) (20L/setting.speed()), block1 -> {
            particle.setLocation(block1.getLocation())
                    .display(player);
            eachTimeSound.play(player, block1.getLocation());
            block1.setType(setting.getBlock());
            int current = (maxSize - queue.size());
            bossBar.getBossBar().setProgress((double) current / (double) maxSize);
            bossBar.setTitle("&x&4&7&7&a&f&d&l☁ &7Wypełnianie: #<5389FD>" + current + " &7/ #<5389FD>" + maxSize + "&7.");
        }, () -> {
            activeBlockBuilder.remove(player.getUniqueId());
            bossBar.removePlayers();
            if (player.isOnline()) {
                onEndSound.play(player);
                sendMappedMessage(player, "worker.finishedWork");
            }
        });
    }

    public static boolean isHoldingWand(Player player) {
        return WandItemData.hasWandPdc(player.getInventory().getItemInMainHand());
    }
}
