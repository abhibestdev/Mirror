package us.blockgame.mirror.profile;

import lombok.Getter;
import lombok.Setter;
import us.blockgame.mirror.recorder.action.impl.RecordedPlayerAction;

public class MirrorProfile {

    @Setter @Getter private RecordedPlayerAction lastRecordedPlayerAction;
}
