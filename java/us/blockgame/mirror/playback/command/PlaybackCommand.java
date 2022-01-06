package us.blockgame.mirror.playback.command;

import org.bukkit.ChatColor;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.CC;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.playback.PlaybackHandler;
import us.blockgame.mirror.recorder.RecorderHandler;

public class PlaybackCommand {

    @Command(name = "playback", permission = "op")
    public void playback(CommandArgs args) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (recorderHandler.isRecording()) {
            args.getSender().sendMessage(ChatColor.RED + "There is currently an ongoing recording.");
            return;
        }
        if (recorderHandler.getRecordedActions().size() <= 0) {
            args.getSender().sendMessage(CC.RED + "The feed is clear.");
            return;
        }
        PlaybackHandler playbackHandler = MirrorPlugin.getInstance().getPlaybackHandler();

        if (playbackHandler.isPlayingBack()) {
            args.getSender().sendMessage(CC.RED + "There is already an ongoing playback.");
            return;
        }
        playbackHandler.startPlayback();

        args.getSender().sendMessage(ChatColor.GREEN + "Started Playback!");
        return;
    }
}
