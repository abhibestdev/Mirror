package us.blockgame.mirror.recorder;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.timer.Timer;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.recorder.action.RecordedAction;
import us.blockgame.mirror.recorder.command.ClearFeedCommand;
import us.blockgame.mirror.recorder.command.RecordCommand;
import us.blockgame.mirror.recorder.task.RecorderTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecorderHandler {

    @Setter @Getter private boolean recording;
    @Setter @Getter private Timer timer;
    @Setter @Getter private Map<Integer, List<RecordedAction>> recordedActions = Maps.newHashMap();

    private RecorderTask recorderTask;


    public RecorderHandler() {
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new RecordCommand());
        commandHandler.registerCommand(new ClearFeedCommand());

        Bukkit.getPluginManager().registerEvents(new RecorderListener(), MirrorPlugin.getInstance());
    }

    public void startRecording() {
        timer = new Timer("Mirror-Recording", 0, false, 0);

        timer.start();

        recorderTask = new RecorderTask();
        recorderTask.runTaskTimerAsynchronously(MirrorPlugin.getInstance(), 0L, 0L);

        recording = true;
    }

    public void stopRecording() {
        timer.stop();
        recording = false;

        recorderTask.cancel();
    }

    public void recordAction(RecordedAction recordedAction) {

        int time = timer.getRawTime();

        List<RecordedAction> recordedActionsList = getRecordedActions(time);
        recordedActionsList.add(recordedAction);

        updateRecordedActions(time, recordedActionsList);
    }

    public List<RecordedAction> getRecordedActions(int timeStamp) {
        return recordedActions.getOrDefault(timeStamp, new ArrayList<>());
    }

    public void updateRecordedActions(int timeStamp, List<RecordedAction> recordedActionList) {
        recordedActions.put(timeStamp, recordedActionList);
    }

}
