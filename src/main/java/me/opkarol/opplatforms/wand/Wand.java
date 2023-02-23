package me.opkarol.opplatforms.wand;

import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplatforms.blockbuilder.BlockBuilder;

import java.util.UUID;

public class Wand  {
    private final UUID wandUUID;
    private UUID activePlayerUUID;
    private BlockBuilder builder;

    public Wand(UUID wandUUID, UUID activePlayerUUID) {
        this.wandUUID = wandUUID;
        this.activePlayerUUID = activePlayerUUID;
    }

    public Wand(UUID wandUUID, UUID activePlayerUUID, BlockBuilder builder) {
        this.wandUUID = wandUUID;
        this.activePlayerUUID = activePlayerUUID;
        this.builder = builder;
    }

    public Wand(UUID wandUUID) {
        this.wandUUID = wandUUID;
    }

    public UUID getWandUUID() {
        return wandUUID;
    }

    public UUID getActivePlayerUUID() {
        return activePlayerUUID;
    }

    public void setActivePlayerUUID(UUID activePlayerUUID) {
        this.activePlayerUUID = activePlayerUUID;
    }

    public BlockBuilder getBuilder() {
        return VariableUtil.getOrDefault(builder, new BlockBuilder());
    }

    public void setBuilder(BlockBuilder builder) {
        this.builder = builder;
    }
}
