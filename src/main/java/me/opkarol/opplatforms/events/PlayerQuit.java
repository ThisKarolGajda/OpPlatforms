package me.opkarol.opplatforms.events;

import me.opkarol.opc.api.event.EventRegister;
import me.opkarol.opplatforms.PluginStarter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuit {
    public PlayerQuit(PluginStarter pluginStarter) {
        EventRegister.registerEvent(PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            pluginStarter.getWandDatabase().getMap().getByKey(uuid).ifPresent(wand -> {
                wand.getBuilder().getRunnableList().stop();
                pluginStarter.getWandDatabase().remove(player);
            });
        });
    }
}
