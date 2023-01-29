package me.opkarol.opplatforms.blockbuilder;

import me.opkarol.opc.api.extensions.Vault;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BlockBuilderRequirements {
    public static boolean hasMoney(Player player, double amount) {
        return Vault.getInstance().isEnabled() && Vault.getInstance().has(player, amount);
    }

    public static boolean isMaterialCorrect(Material material) {
        return material.isBlock() && material.isSolid();
    }

    public static boolean hasCorrectSpeed(double speed) {
        return speed > 0;
    }

    public static boolean containsEnoughMaterial(Player player, Material block, int amount) {
        return player.getInventory().contains(block, amount);
    }

    public static boolean isDifferentHeight(Vector vector1, Vector vector2) {
        return vector1 != null && vector2 != null && vector2.getBlockY() != vector1.getBlockY();
    }

    public static boolean isTooLarge(int blocks) {
        return blocks > 512;
    }

    public static boolean isEmptyPlatform(int blocks) {
        return blocks == 0;
    }
}