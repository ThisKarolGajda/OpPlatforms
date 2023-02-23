package me.opkarol.opplatforms.wand;

import me.opkarol.opc.api.utils.PDCUtils;
import me.opkarol.opc.api.utils.StringUtil;
import me.opkarol.opplatforms.PluginStarter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class WandItemData {
    public static final String DEFAULT_WAND_DAMAGE = "1000";
    public static final NamespacedKey WAND_DAMAGE = new NamespacedKey(PluginStarter.getInstance(), "wand-damage");
    public static final NamespacedKey WAND_UUID = new NamespacedKey(PluginStarter.getInstance(), "wand-uuid");


    public static boolean hasWandPdc(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return PDCUtils.hasNBT(itemStack, WAND_DAMAGE) &&
                PDCUtils.hasNBT(itemStack, WAND_UUID);
    }

    public static int getWandDurability(ItemStack itemStack) {
        return StringUtil.getInt(PDCUtils.getNBT(itemStack, WAND_DAMAGE));
    }

    /**
     * @return boolean -> if it should be removed
     */
    public static boolean damageWand(ItemStack wand, int damage) {
        int wandDurability = getWandDurability(wand);

        int diff = wandDurability - damage;
        if (diff <= 0) {
            return true;
        }

        PDCUtils.addNBT(wand, WAND_DAMAGE, String.valueOf(diff));
        return false;
    }

    public static boolean hasEnoughDurability(ItemStack wand, int damage) {
        int wandDurability = getWandDurability(wand);

        int diff = wandDurability - damage;
        return diff >= 0;
    }

    public static Optional<UUID> getWandUUID(ItemStack itemStack) {
        String data = PDCUtils.getNBT(itemStack, WAND_UUID);
        if (data != null) {
            return Optional.of(UUID.fromString(data));
        }
        return Optional.empty();
    }
}
