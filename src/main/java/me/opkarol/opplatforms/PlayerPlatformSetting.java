package me.opkarol.opplatforms;

import me.opkarol.opc.api.utils.StringUtil;
import org.bukkit.Material;

import java.io.Serializable;

public record PlayerPlatformSetting(double speed, String block) implements Serializable {

    public Material getBlock() {
        return StringUtil.getMaterialFromString(block);
    }
}
