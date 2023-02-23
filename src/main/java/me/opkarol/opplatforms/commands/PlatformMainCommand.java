package me.opkarol.opplatforms.commands;

import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplatforms.PlayerPlatformSetting;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderTool;
import me.opkarol.opplatforms.wand.Wand;
import me.opkarol.opplatforms.wand.WandItem;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Optional;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

@Command("platforma")
public class PlatformMainCommand {
    private final InventoriesHolder holder;
    private final PluginStarter pluginStarter;

    public PlatformMainCommand(PluginStarter pluginStarter) {
        this.pluginStarter = pluginStarter;
        this.holder = pluginStarter.getInventoriesHolder();
    }

    @DefaultFor("platforma")
    public void platformMain(Player player) {
        WandItemData.getWandUUID(player.getInventory().getItemInMainHand()).ifPresent(uuid -> {
            Optional<Wand> optional = pluginStarter.getWandDatabase().getWand(uuid);
            if (optional.isPresent() && optional.get().getBuilder().areBothFilled() && !pluginStarter.getActiveBlockBuilder().contains(player.getUniqueId())) {
                Wand wand = optional.get();
                BlockBuilder builder = wand.getBuilder();

                if (!BlockBuilderTool.isHoldingWand(player)) {
                    sendMappedMessage(player, "needsToHoldWand");
                    return;
                }

                if (!WandItemData.hasEnoughDurability(player.getInventory().getItemInMainHand(), builder.getQueueSize())) {
                    sendMappedMessage(player, "wandToWeak");
                    return;
                }

                holder.getInventory("PlatformConfirmInventory").
                        ifPresent(inventory -> inventory.getInventory().open(player,
                                Tuple.of("%block%", builder.getSetting().block()),
                                Tuple.of("%speed%", String.valueOf(builder.getSetting().speed())),
                                Tuple.of("%block_amount%", String.valueOf(builder.getQueueSize())),
                                Tuple.of("%cost%", String.valueOf(builder.getRequiredMoney())),
                                Tuple.of("%block_cost%", String.valueOf(builder.getBlockCost()))
                        ));
            } else {
                holder.getInventory("PlatformMainInventory")
                        .ifPresent(inventory -> inventory.getInventory().openBestInventory(player));
            }
        });
    }

    @Subcommand("ustawienia")
    public void platformSettings(Player player) {
        PlayerPlatformSetting setting = PlayerPlatformSettings.getSettings().getSafeSetting(player.getUniqueId());
        pluginStarter.getInventoriesHolder().getInventory("PlatformSettingsInventory")
                .ifPresent(inventory -> inventory.getInventory().open(player,
                        Tuple.of("%speed%", String.valueOf(setting.speed())),
                        Tuple.of("%block%", setting.block())
                ));
    }

     @Subcommand("stop")
     public void platformStop(Player player, @revxrsal.commands.annotation.Optional Player playerToStop) {
          if (playerToStop == null) {
              // Use it on yourself
              pluginStarter.getWandDatabase().getPlayerActiveWand(player.getUniqueId()).ifPresentOrElse(wand -> {
                  BlockBuilder builder = wand.getBuilder();
                  if (builder.getQueue().getRunnable() != null) {
                      sendMappedMessage(player, "cantStopRunningPlatform");
                      return;
                  }
                  builder.clearVectors();
                  builder.getRunnableList().stop();
                  builder.clearFakeBlocks(player);
                  sendMappedMessage(player, "cancelledPlatform");
                  pluginStarter.getWandDatabase().remove(player);

              }, () -> sendMappedMessage(player, "dontHaveAnyPlatform"));
          } else if (player.hasPermission("platforma.admin.stop")) {
              String playerName = playerToStop.getName();
              pluginStarter.getWandDatabase().getPlayerActiveWand(playerToStop.getUniqueId()).ifPresentOrElse(wand -> {
                  pluginStarter.getActiveBlockBuilder().remove(wand.getWandUUID());
                  BlockBuilder blockBuilder = wand.getBuilder();
                  OpRunnable runnable = blockBuilder.getQueue().getRunnable();
                  if (runnable != null) {
                      runnable.cancel();
                  }
                  blockBuilder.clearVectors();
                  blockBuilder.clearFakeBlocks(player);
                  blockBuilder.getRunnableList().stop();
                  pluginStarter.getWandDatabase().remove(player);
                  player.sendMessage(FormatUtils.formatMessage("&cZastopowano graczowi " + playerName + ", uuid: " + playerToStop.getUniqueId() + " budowę platformy."));
              }, () -> player.sendMessage(FormatUtils.formatMessage("&cGracz " + playerName + ", uuid: " + playerToStop.getUniqueId() + " nie ma aktywnej platformy.")));

          }
     }

    @Subcommand("wand")
    @CommandPermission("platforma.admin.wand")
    public void platformWand(Player player) {
        WandItem wand = new WandItem();
        player.getInventory().addItem(wand.getNewItem());
    }

}
