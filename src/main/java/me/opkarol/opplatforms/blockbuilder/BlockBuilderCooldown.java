package me.opkarol.opplatforms.blockbuilder;

import me.opkarol.opc.api.misc.CooldownModule;

import java.util.UUID;

public class BlockBuilderCooldown extends CooldownModule<UUID> {
    @Override
    public long getCooldownTimeSeconds() {
        return 5;
    }
}
