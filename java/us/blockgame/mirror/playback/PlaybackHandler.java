package us.blockgame.mirror.playback;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.npc.NPC;
import us.blockgame.lib.timer.Timer;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.playback.command.PlaybackCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlaybackHandler {

    @Getter private Timer timer;
    @Getter private List<NPC> npcList = new ArrayList<>();
    @Getter private Map<UUID, Entity> entityMap = Maps.newHashMap();
    @Getter private boolean playingBack;

    public PlaybackHandler() {
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new PlaybackCommand());

        Bukkit.getPluginManager().registerEvents(new PlaybackListener(), MirrorPlugin.getInstance());
    }

    public void startPlayback() {
        playingBack = true;
        timer = new Timer("Mirror-Playback", 0, false, 0);
        timer.start();
    }

    public void stopPlayback() {
        timer.stop();

        npcList.forEach(NPC::remove);
        npcList.clear();

        getEntityMap().keySet().forEach(u -> {
            Entity entity = getEntity(u);
            entity.remove();
        });

        getEntityMap().clear();

        playingBack = false;
        entityMap.clear();
    }

    public NPC getNPC(UUID uuid) {
        return npcList.stream().filter(n -> n.getUUID().toString().equals(uuid.toString())).findFirst().orElse(null);
    }

    public void addNPC(UUID uuid, String name, String skinSignature, String skinValue, Location location) {
        NPC npc = new NPC(uuid, name, skinValue, skinSignature, location);

        npcList.add(npc);

        Bukkit.getOnlinePlayers().forEach(npc::addPlayer);
    }

    public void removeNPC(UUID uuid) {
        NPC npc = getNPC(uuid);

        Bukkit.getOnlinePlayers().forEach(npc::removePlayer);
        npc.remove();

        npcList.remove(npc);
    }

    public Entity getEntity(UUID uuid) {
        return entityMap.getOrDefault(uuid, null);
    }

    public Entity removeEntity(UUID uuid) {
        return entityMap.remove(uuid);
    }

    public Entity spawnEntity(EntityType entityType, UUID uuid, Location location) {
        Entity entity = location.getWorld().spawnEntity(location, entityType);

        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound nbtTagCompound = nmsEntity.getNBTTag();
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        nmsEntity.c(nbtTagCompound);
        nbtTagCompound.setInt("NoAI", 1);
        nmsEntity.f(nbtTagCompound);

        entityMap.put(uuid, entity);

        return entity;
    }
}
