package me.opkarol.opplatforms.wand;

import me.opkarol.opc.api.map.OpMap;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class WandDatabase {
    private final OpMap<UUID, Wand> map = new OpMap<>();

    public OpMap<UUID, Wand> getMap() {
        return map;
    }

    public void add(Wand wand) {
        getMap().set(wand.getWandUUID(), wand);
    }

    public Optional<Wand> getWand(UUID uuid) {
        return getMap().getByKey(uuid);
    }

    public void remove(UUID uuid) {
        getMap().remove(uuid);
    }

    public void remove(Wand wand) {
        getMap().remove(wand.getWandUUID());
    }

    public void remove(Player player) {
        Optional<Wand> optional = getPlayerActiveWand(player);
        optional.ifPresent(this::remove);
    }

    //todo add check if player already has active wand
    public Optional<Wand> getPlayerActiveWand(UUID uuid) {
        return getMap().getValues().stream().filter(wand -> wand.getActivePlayerUUID().equals(uuid)).findAny();
    }

    public Optional<Wand> getPlayerActiveWand(Player player) {
        return getPlayerActiveWand(player.getUniqueId());
    }

    public boolean hasAnyActiveWand(UUID uuid) {
        return getPlayerActiveWand(uuid).isPresent();
    }
}
