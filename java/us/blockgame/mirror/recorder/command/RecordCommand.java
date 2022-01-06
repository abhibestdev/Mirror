package us.blockgame.mirror.recorder.command;

import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.CC;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.recorder.RecorderHandler;

public class RecordCommand {

    @Command(name = "record", permission = "op")
    public void record(CommandArgs args) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording()) {
            recorderHandler.startRecording();
            args.getSender().sendMessage(CC.GREEN + "The recording has started.");
            return;
        }
        recorderHandler.stopRecording();
        args.getSender().sendMessage(CC.GREEN + "The recording has stopped.");
        return;
    }
}
