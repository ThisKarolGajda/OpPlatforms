package me.opkarol.opplatforms.blockbuilder;

import me.opkarol.opc.api.utils.VariableUtil;

import java.util.HashSet;
import java.util.UUID;

public class ActiveBlockBuilder extends HashSet<UUID> {

    private static ActiveBlockBuilder activeBlockBuilder;

    public ActiveBlockBuilder() {
        activeBlockBuilder = this;
    }

    public static ActiveBlockBuilder getActiveBlockBuilder() {
        return VariableUtil.getOrDefault(activeBlockBuilder, new ActiveBlockBuilder());
    }
}
