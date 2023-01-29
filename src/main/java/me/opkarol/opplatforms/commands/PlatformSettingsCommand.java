package me.opkarol.opplatforms.commands;

import me.opkarol.opc.api.command.simple.Command;
import me.opkarol.opc.api.gui.holder.InventoriesHolder;
import me.opkarol.opc.api.list.OpList;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.utils.StringUtil;
import me.opkarol.opplatforms.PlayerPlatformSetting;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import me.opkarol.opplatforms.block.AllowedBlocksWithPrice;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.opkarol.opc.api.plugins.OpMessagesPlugin.sendMappedMessage;

public class PlatformSettingsCommand extends Command {
    private final InventoriesHolder holder;
    private final AllowedBlocksWithPrice blocksWithPrice;

    public PlatformSettingsCommand(InventoriesHolder inventoriesHolder, AllowedBlocksWithPrice blocksWithPrice) {
        super("ustawieniaplatform");
        this.holder = inventoriesHolder;
        this.blocksWithPrice = blocksWithPrice;
    }

    @Override
    public List<String> tabComplete(int currentIndex, OpList<String> args) {
        List<String> list = new ArrayList<>();
        if (currentIndex == 1) {
            list.addAll(OpList.asList("szybkosc", "blok"));
        }
        if (currentIndex == 2) {
            args.get(0).ifPresent(s -> {
                switch (s.toLowerCase()) {
                    case "szybkosc" -> list.add("<WARTOSC>");
                    case "blok" -> list.addAll(blocksWithPrice.getMap().keySet());
                }
            });
        }
        return list;
    }

    @Override
    public void execute(CommandSender sender, OpList<String> args) {
        if (!(sender instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        if (args.size() == 0) {
            PlayerPlatformSetting setting = PlayerPlatformSettings.getSettings().getSafeSetting(playerUUID);
            holder.getInventory("PlatformSettingsInventory")
                    .ifPresent(inventory -> inventory.getInventory().open(player,
                            Tuple.of("%speed%", String.valueOf(setting.speed())),
                            Tuple.of("%block%", setting.block())
                    ));
            return;
        }

        Optional<String> optional = args.get(0);
        if (optional.isEmpty()) {
            return;
        }

        if (args.size() < 2) {
            return;
        }

        switch (optional.get().toLowerCase()) {
            case "szybkosc" -> args.ifPresent(1, object -> {
                double value = StringUtil.getDoubleFromString(object);
                if (value < 1) {
                    sendMappedMessage(player, "selectedSpeedInvalid");
                    return;
                }
                PlayerPlatformSettings.getSettings()
                        .setSetting(playerUUID, StringUtil.getDoubleFromString(object));
                sendMappedMessage(player, "changedValueOfSetting", Tuple.of("%setting%", "[SZYBKOSC]"), Tuple.of("%value%", object));
            });
            case "blok" -> args.ifPresent(1, object -> {
                if (blocksWithPrice.getMap().getByKey(object.toUpperCase()).isEmpty()) {
                    sendMappedMessage(player, "selectedBlockInvalid");
                    return;
                }

                PlayerPlatformSettings.getSettings()
                        .setSetting(playerUUID, object);
                sendMappedMessage(player, "changedValueOfSetting", Tuple.of("%setting%", "[BLOK]"), Tuple.of("%value%", object));
            });
        }
    }

    @Override
    public boolean hasCustomTabCompletion() {
        return true;
    }
}
