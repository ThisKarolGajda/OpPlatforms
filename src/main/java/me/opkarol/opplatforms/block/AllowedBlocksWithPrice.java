package me.opkarol.opplatforms.block;

import me.opkarol.opc.api.file.Configuration;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opplatforms.PlayerPlatformSetting;
import me.opkarol.opplatforms.PlayerPlatformSettings;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class AllowedBlocksWithPrice extends Configuration {
    private final OpMap<String, Integer> map = new OpMap<>();

    public AllowedBlocksWithPrice(@NotNull Plugin plugin) {
        super(plugin, "blocks");
        useSectionKeys("blocks", s -> map.set(s.toUpperCase(), getInt("blocks." + s)));
    }

    public OpMap<String, Integer> getMap() {
        return map;
    }

    public Optional<Integer> getFromUUID(UUID uuid) {
        PlayerPlatformSetting setting = PlayerPlatformSettings.getSettings().getSafeSetting(uuid);
        return getMap().getByKey(setting.block().toUpperCase());
    }

}
