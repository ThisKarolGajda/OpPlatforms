package me.opkarol.opplatforms.commands;

import me.opkarol.opc.api.command.simple.Command;
import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.list.OpList;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;
import me.opkarol.opplatforms.blockbuilder.BlockBuilderTool;
import me.opkarol.opplatforms.wand.WandItemData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class PlatformMainCommand extends Command {
    private final InventoriesHolder holder;
    private final PluginStarter pluginStarter;

    public PlatformMainCommand(PluginStarter pluginStarter) {
        super("platforma");
        this.pluginStarter = pluginStarter;
        this.holder = pluginStarter.getInventoriesHolder();
    }

    @Override
    public void execute(CommandSender sender, OpList<String> args) {
        if (!(sender instanceof Player player)) {
            return;
        }

        Optional<BlockBuilder> optional = pluginStarter.getBlockMap().getByKey(player.getUniqueId());
        if (optional.isPresent() && optional.get().areBothFilled()) {
            BlockBuilder builder = optional.get();

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
            if (args.size() == 0) {
                holder.getInventory("PlatformMainInventory")
                        .ifPresent(inventory -> inventory.getInventory().openBestInventory(player));
                return;
            }
            args.ifPresent(0, s -> {
                if (s.equalsIgnoreCase("wand") && sender.isOp()) {
                    ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand();
                    WandItemData.addWandPdc(itemStack);
                }
            });
        }
    }
}
