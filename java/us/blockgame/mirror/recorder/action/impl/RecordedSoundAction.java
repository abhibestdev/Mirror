package us.blockgame.mirror.recorder.action.impl;

import lombok.Getter;
import org.bukkit.Location;
import us.blockgame.mirror.recorder.action.RecordedAction;

public class RecordedSoundAction extends RecordedAction {

    @Getter private final String name;
    @Getter private final float v;
    @Getter private final float v1;

    public RecordedSoundAction(String name, Location location, float v, float v1) {
        super(RecordedActionType.SOUND, null, location);

        this.name = name;
        this.v = v;
        this.v1 = v1;
    }
}
