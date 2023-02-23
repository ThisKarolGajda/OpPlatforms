package me.opkarol.opplatforms.wand;

import me.opkarol.opc.api.item.OpItemBuilder;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.utils.PDCUtils;
import me.opkarol.opc.api.utils.VariableUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class WandItem {
    public OpItemBuilder<?> itemBuilder;

    public WandItem() {
        this(new OpItemBuilder<>(Material.BLAZE_ROD)
                .name("#!<fbd300>Różdżka Platform#!<Fd0000>")
                .enchantments(Tuple.of(Enchantment.LUCK, 1))
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .pdc(VariableUtil.getMapFromTuples(Tuple.of("opplatforms:wand-damage", "1000")))
                .generate()
        );
    }

    public WandItem(ItemStack itemStack) {
        this(new OpItemBuilder<>(itemStack));
    }

    public WandItem(OpItemBuilder<?> itemBuilder) {
        this.itemBuilder = itemBuilder;
        String leftWandUsage = String.valueOf(WandItemData.getWandDurability(itemBuilder.generate()));
        this.itemBuilder.lore(Arrays.asList(
                "&7&m---------------------------",
                "#<5389FD>&lCo to jest?",
                "&7Ta magiczna różdżka pozwoli Ci",
                "&7na dużo #<5389FD>łatwiejsze&7 budowanie",
                "&7platform. Różdżka działa &ntylko w",
                "&7powietrzu &7i &nnie nadpisuje&7 bloków.",
                "&7 ",
                "#<5389FD>&lJak używać?",
                "&7Aby użyć różdżki musisz ustawić",
                "&7materiał i prędkość budowy platformy",
                "&7w ustawieniach (#<5389FD>/platforma ustawienia&7).",
                "&7Lewym przyciskiem myszy zaznaczysz",
                "#<5389FD>pierwszy punkt&7, a prawym #<5389FD>drugi punkt&7,",
                "&7które &nwyznaczają wielkość&7 platformy.",
                "&7Po zaznaczeniu punktów platforma",
                "&7zacznie się budować, jeśli posiadasz",
                "&7&nwystarcząjącą ilość&7 pieniędzy i materiałów.",
                "&7 ",
                "#<5389FD>Zużycie różdżki: &7" + leftWandUsage + "&8/&7" + WandItemData.DEFAULT_WAND_DAMAGE
        ));
    }

    public OpItemBuilder<?> getItemBuilder() {
        return itemBuilder;
    }

    public ItemStack getNewItem() {
        ItemStack itemStack = itemBuilder.generate();
        PDCUtils.addNBT(itemStack, WandItemData.WAND_DAMAGE, WandItemData.DEFAULT_WAND_DAMAGE);
        PDCUtils.addNBT(itemStack, WandItemData.WAND_UUID, UUID.randomUUID().toString());
        return itemStack;
    }
}
