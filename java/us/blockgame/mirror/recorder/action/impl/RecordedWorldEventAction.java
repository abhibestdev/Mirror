package us.blockgame.mirror.recorder.action.impl;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import us.blockgame.mirror.recorder.action.RecordedAction;

public class RecordedWorldEventAction extends RecordedAction {

    @Getter private final int a;
    @Getter private final BlockPosition b;
    @Getter private final int c;
    @Getter private final boolean d;

    public RecordedWorldEventAction(int a, BlockPosition b, int c, boolean d) {
        super(RecordedActionType.WORLD_EVENT, null, null);

        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
