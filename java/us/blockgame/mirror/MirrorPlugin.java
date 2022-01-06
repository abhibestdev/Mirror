package us.blockgame.mirror;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockgame.mirror.packet.PacketHandler;
import us.blockgame.mirror.playback.PlaybackHandler;
import us.blockgame.mirror.profile.ProfileHandler;
import us.blockgame.mirror.recorder.RecorderHandler;

public class MirrorPlugin extends JavaPlugin {

    @Getter private static MirrorPlugin instance;

    @Getter private ProfileHandler profileHandler;
    @Getter private PacketHandler packetHandler;
    @Getter private RecorderHandler recorderHandler;
    @Getter private PlaybackHandler playbackHandler;

    @Override
    public void onEnable() {
        instance = this;

        registerHandlers();
    }

    private void registerHandlers() {
        profileHandler = new ProfileHandler();
        packetHandler = new PacketHandler();
        recorderHandler = new RecorderHandler();
        playbackHandler = new PlaybackHandler();
    }
}
