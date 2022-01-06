package us.blockgame.mirror.profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.blockgame.mirror.MirrorPlugin;

public class ProfileListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = MirrorPlugin.getInstance().getProfileHandler();
        if (!profileHandler.hasProfile(player)) {
            profileHandler.addProfile(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = MirrorPlugin.getInstance().getProfileHandler();
        if (profileHandler.hasProfile(player)) {
            profileHandler.removeProfile(player);
        }
    }
}
