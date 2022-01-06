package us.blockgame.mirror.recorder.action.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import us.blockgame.mirror.recorder.action.RecordedAction;

public class RecordedBlockAction extends RecordedAction {

    @Setter @Getter private ItemStack itemStack;

    public RecordedBlockAction(ItemStack itemStack, Location location, RecordedActionType recordedActionType) {
        super(recordedActionType, null, location);

        this.itemStack = itemStack;
    }
}
