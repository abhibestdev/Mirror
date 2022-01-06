package us.blockgame.mirror.recorder.task;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.lib.util.Logger;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.recorder.RecorderHandler;
import us.blockgame.mirror.recorder.action.impl.RecordedEntityAction;

import java.util.Map;
import java.util.UUID;

public class RecorderTask extends BukkitRunnable {

    private Map<UUID, Location> lastLocation = Maps.newHashMap();

    public void run() {

        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        Bukkit.getWorld("world").getEntities().stream().filter(e -> (e instanceof LivingEntity) && (!(e instanceof Player))).forEach(e -> {

            if (!lastLocation.containsKey(e.getUniqueId()) || lastLocation.get(e.getUniqueId()).distanceSquared(e.getLocation()) > 0) {

                RecordedEntityAction recordedEntityAction = new RecordedEntityAction(e, e.getLocation());

                switch (e.getType()) {
                    case ZOMBIE: {
                        Zombie zombie = (Zombie) e;

                        recordedEntityAction.setBaby(zombie.isBaby());
                        recordedEntityAction.setVillager(zombie.isVillager());
                        break;
                    }
                }
                recorderHandler.recordAction(recordedEntityAction);
                lastLocation.put(e.getUniqueId(), e.getLocation());
            }

        });
    }

}
