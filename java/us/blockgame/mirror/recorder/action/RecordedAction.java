package us.blockgame.mirror.recorder.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;

@RequiredArgsConstructor
public class RecordedAction {

    @Getter private final RecordedActionType recordedActionType;
    @Getter private final UUID uuid;
    @Getter private final Location location;

    public enum RecordedActionType {
        ITEM_DROP, BLOCK_CHANGE, PLAYER_UPDATE, ENTITY_UPDATE, SOUND, WORLD_EVENT, ENTITY_VELOCITY
    }
}
