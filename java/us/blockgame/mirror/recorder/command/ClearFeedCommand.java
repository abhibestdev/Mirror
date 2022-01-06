package us.blockgame.mirror.recorder.command;

import org.bukkit.ChatColor;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.CC;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.playback.PlaybackHandler;
import us.blockgame.mirror.recorder.RecorderHandler;

public class ClearFeedCommand {

    @Command(name = "clearfeed", permission = "op")
    public void clearFeed(CommandArgs args) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (recorderHandler.getRecordedActions().size() <= 0) {
            args.getSender().sendMessage(ChatColor.RED + "The feed is already empty.");
            return;
        }
        PlaybackHandler playbackHandler = MirrorPlugin.getInstance().getPlaybackHandler();

        if (playbackHandler.isPlayingBack()) {
            args.getSender().sendMessage(CC.RED + "There is already an ongoing playback.");
            return;
        }

        recorderHandler.getRecordedActions().clear();
        args.getSender().sendMessage(ChatColor.GREEN + "The feed has been cleared!");
        return;
    }
}
