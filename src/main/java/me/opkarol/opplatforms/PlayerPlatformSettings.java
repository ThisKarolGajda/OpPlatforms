package me.opkarol.opplatforms;

import me.opkarol.opc.api.database.flat.FlatDatabase;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.tools.autostart.IDisable;
import me.opkarol.opc.api.tools.autostart.OpAutoDisable;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PlayerPlatformSettings extends FlatDatabase<OpMap<UUID, PlayerPlatformSetting>> implements IDisable {
    private final OpMap<UUID, PlayerPlatformSetting> map;
    private static PlayerPlatformSettings settings;
    private static final PlayerPlatformSetting DEFAULT_SETTING = new PlayerPlatformSetting(1, "STONE");

    public PlayerPlatformSettings(Plugin plugin) {
        super(plugin, "players.db");
        this.map = Objects.requireNonNullElseGet(loadObject(), OpMap::new);
        settings = this;
        OpAutoDisable.add(this);
    }

    public OpMap<UUID, PlayerPlatformSetting> getMap() {
        return map;
    }

    public static PlayerPlatformSettings getSettings() {
        return settings;
    }

    public Optional<PlayerPlatformSetting> getSetting(UUID uuid) {
        return map.getByKey(uuid);
    }

    public PlayerPlatformSetting getSafeSetting(UUID uuid) {
        Optional<PlayerPlatformSetting> setting = getSetting(uuid);
        if (setting.isPresent()) {
            return setting.get();
        }
        setSetting(uuid, DEFAULT_SETTING);
        return DEFAULT_SETTING;
    }

    public void setSetting(UUID uuid, PlayerPlatformSetting setting) {
        map.set(uuid, setting);
    }

    public void setSetting(UUID uuid, double speed, String block) {
        setSetting(uuid, new PlayerPlatformSetting(speed, block));
    }

    public void setSetting(UUID uuid, double speed) {
        setSetting(uuid, speed, getSafeSetting(uuid).block());
    }

    public void setSetting(UUID uuid, String block) {
        setSetting(uuid, getSafeSetting(uuid).speed(), block);
    }

    @Override
    public void onDisable() {
        saveObject(map);
    }
}

