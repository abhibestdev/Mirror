package us.blockgame.mirror.recorder.action.impl;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import us.blockgame.mirror.recorder.action.RecordedAction;

public class RecordedEntityVelocityAction extends RecordedAction {

    @Getter private EntityType entityType;
    @Getter private int a;
    @Getter private int b;
    @Getter private int c;

    public RecordedEntityVelocityAction(Entity entity, int a, int b, int c) {
        super(RecordedActionType.ENTITY_VELOCITY, entity.getUniqueId(), entity.getLocation());

        this.entityType = entity.getType();
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
