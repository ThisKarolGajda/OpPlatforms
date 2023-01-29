package me.opkarol.opplatforms.wand;

import me.opkarol.opc.OpAPI;
import me.opkarol.opc.api.utils.PDCUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class WandItemData {
    private static final NamespacedKey key = NamespacedKey.minecraft("wand");

    public static boolean hasWandPdc(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return PDCUtils.hasNBT(itemStack, key);
    }

    public static void addWandPdc(ItemStack itemStack) {
        PDCUtils.addNBT(itemStack, key, "1");
    }

    /**
     * @return boolean -> if it should be removed
     */
    public static boolean damageWand(ItemStack wand, int damage) {
        if (!(wand.getItemMeta() instanceof Damageable damageable)) {
            return false;
        }
        int maxDurability = wand.getType().getMaxDurability();

        int diff = damageable.getDamage() + damage;
        if (diff >= maxDurability) {
            return true;
        }
        damageable.setDamage(damageable.getDamage() + damage);
        wand.setItemMeta(damageable);
        return false;
    }

    public static boolean hasEnoughDurability(ItemStack wand, int damage) {
        if (!(wand.getItemMeta() instanceof Damageable damageable)) {
            return false;
        }

        int maxDurability = wand.getType().getMaxDurability();

        int diff = damageable.getDamage() + damage;
        return diff <= maxDurability;
    }
}
