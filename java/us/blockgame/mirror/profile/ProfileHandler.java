package us.blockgame.mirror.profile;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.mirror.MirrorPlugin;

import java.util.Map;
import java.util.UUID;

public class ProfileHandler {

    private Map<UUID, MirrorProfile> mirrorProfileMap = Maps.newHashMap();

    public ProfileHandler() {
        Bukkit.getPluginManager().registerEvents(new ProfileListener(), MirrorPlugin.getInstance());
    }

    public void addProfile(Player player) {
        mirrorProfileMap.put(player.getUniqueId(), new MirrorProfile());
    }

    public void removeProfile(Player player) {
        mirrorProfileMap.remove(player.getUniqueId());
    }

    public boolean hasProfile(Player player) {
        return mirrorProfileMap.containsKey(player.getUniqueId());
    }

    public MirrorProfile getProfile(Player player) {
        return mirrorProfileMap.get(player.getUniqueId());
    }
}
