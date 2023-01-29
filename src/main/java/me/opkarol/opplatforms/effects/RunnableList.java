package me.opkarol.opplatforms.effects;

import me.opkarol.opc.api.map.ActionMap;
import me.opkarol.opc.api.tools.runnable.OpRunnable;

public class RunnableList extends ActionMap<OpRunnable> {
    private static final int LEFT_POSITION_INDEX = 0;
    private static final int RIGHT_POSITION_INDEX = 1;

    public void add(OpRunnable object, boolean isVector1) {
        object.runTaskTimer(15L);
        setRunnable(isVector1 ? LEFT_POSITION_INDEX : RIGHT_POSITION_INDEX, object);
    }

    private void setRunnable(int index, OpRunnable object) {
        OpRunnable runnable = get(index);
        if (runnable != null) {
            runnable.cancel();
        }
        set(object, index);
    }

    public void stop() {
        for (OpRunnable runnable : getMap().getValues()) {
            runnable.cancel();
        }
        clear();
    }
}
