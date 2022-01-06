package us.blockgame.mirror.recorder.action.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import us.blockgame.mirror.recorder.action.RecordedAction;

public class RecordedEntityAction extends RecordedAction {

    @Setter @Getter private EntityType entityType;
    @Setter @Getter private boolean damage;
    @Setter @Getter private boolean dead;
    @Setter @Getter private boolean baby;
    @Setter @Getter private boolean villager;

    public RecordedEntityAction(Entity entity, Location location) {
        super(RecordedActionType.ENTITY_UPDATE, entity.getUniqueId(), location);

        this.entityType = entity.getType();
    }
}
