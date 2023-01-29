package me.opkarol.opplatforms.events;

import me.opkarol.opc.api.event.EventRegister;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opplatforms.PluginStarter;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit {
    public PlayerQuit(PluginStarter pluginStarter) {
        EventRegister.registerEvent(PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            OpMap<UUID, BlockBuilder> blockMap = pluginStarter.getBlockMap();
            if (blockMap.containsKey(uuid)) {
                blockMap.unsafeGet(uuid).getRunnableList().stop();
                blockMap.remove(uuid);
            }
        });
    }
}
