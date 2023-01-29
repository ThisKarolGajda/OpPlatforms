package me.opkarol.opplatforms.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {
    public static List<Location> getHollowCube(Location loc, double particleDistance) {
        List<Location> result = new ArrayList<>();
        World world = loc.getWorld();
        double minX = loc.getBlockX();
        double minY = loc.getBlockY();
        double minZ = loc.getBlockZ();
        double maxX = loc.getBlockX()+1;
        double maxY = loc.getBlockY()+1;
        double maxZ = loc.getBlockZ()+1;

        for (double x = minX; x <= maxX; x = Math.round((x + particleDistance) * 1e2) / 1e2) {
            for (double y = minY; y <= maxY; y = Math.round((y + particleDistance) * 1e2) / 1e2) {
                for (double z = minZ; z <= maxZ; z = Math.round((z + particleDistance) * 1e2) / 1e2) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }

    public static void place(Location loc, Particle.DustOptions dustOptions) {
        for (Location location : getHollowCube(loc, 0.25)) {
            if (location == null || location.getWorld() == null) {
                continue;
            }
            location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, 0, 0, 0, 0, dustOptions);
        }
    }
}
