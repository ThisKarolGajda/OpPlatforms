package me.opkarol.opplatforms.effects;

import me.opkarol.opc.api.misc.opobjects.OpBossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

public class BossBarManager {
    private final OpBossBar bossBar;
    private final Player player;

    public BossBarManager(Player player) {
        this.player = player;
        bossBar = new OpBossBar("WCZYTYWANIE...", BarColor.RED);
    }

    public OpBossBar getBossBar() {
        return bossBar;
    }

    public Player getPlayer() {
        return player;
    }
}
