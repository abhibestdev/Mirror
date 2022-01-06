package us.blockgame.mirror.recorder;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.packet.event.PacketInAsyncEvent;
import us.blockgame.lib.packet.event.PacketOutAsyncEvent;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.ReflectionUtil;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.recorder.action.RecordedAction;
import us.blockgame.mirror.recorder.action.impl.*;

import java.lang.reflect.Field;

public class RecorderListener implements Listener {

    @EventHandler
    public void onPacketInAsync(PacketInAsyncEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording()) return;

        Packet packet = event.getPacket();
        Player player = event.getPlayer();

        if (player != null) {

            RecordedPlayerAction recordedPlayerAction = new RecordedPlayerAction(player, player.getLocation());

            if (packet instanceof PacketPlayInArmAnimation) {
                recordedPlayerAction.setSwing(true);
            }
            recorderHandler.recordAction(recordedPlayerAction);
        }
    }

    @EventHandler
    @SneakyThrows
    public void onPacketOutAsync(PacketOutAsyncEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        Packet packet = event.getPacket();
        Player player = event.getPlayer();

        if (!recorderHandler.isRecording()) return;

        if (packet instanceof PacketPlayOutNamedSoundEffect) {

            PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect = (PacketPlayOutNamedSoundEffect) packet;

            String name = (String) ReflectionUtil.getDeclaredObject("a", PacketPlayOutNamedSoundEffect.class, packetPlayOutNamedSoundEffect);
            double x = ((int) ReflectionUtil.getDeclaredObject("b", PacketPlayOutNamedSoundEffect.class, packetPlayOutNamedSoundEffect)) / 8.0D;
            double y = ((int) ReflectionUtil.getDeclaredObject("c", PacketPlayOutNamedSoundEffect.class, packetPlayOutNamedSoundEffect)) / 8.0D;
            double z = ((int) ReflectionUtil.getDeclaredObject("d", PacketPlayOutNamedSoundEffect.class, packetPlayOutNamedSoundEffect)) / 8.0D;
            float v = (float) ReflectionUtil.getDeclaredObject("e", PacketPlayOutNamedSoundEffect.class, packetPlayOutNamedSoundEffect);
            float v1 = ((int) ReflectionUtil.getDeclaredObject("f", PacketPlayOutNamedSoundEffect.class, packetPlayOutNamedSoundEffect)) / 63.0F;;

            RecordedSoundAction recordedSoundAction = new RecordedSoundAction(name, new Location(Bukkit.getWorld("world"), x, y, z), v, v1);
            recorderHandler.recordAction(recordedSoundAction);
            return;
        }

        if (packet instanceof PacketPlayOutWorldEvent) {
            PacketPlayOutWorldEvent packetPlayOutWorldEvent = (PacketPlayOutWorldEvent) packet;

            int a = (int) ReflectionUtil.getDeclaredObject("a", PacketPlayOutWorldEvent.class, packetPlayOutWorldEvent);
            BlockPosition b = (BlockPosition) ReflectionUtil.getDeclaredObject("b", PacketPlayOutWorldEvent.class, packetPlayOutWorldEvent);
            int c = (int) ReflectionUtil.getDeclaredObject("c", PacketPlayOutWorldEvent.class, packetPlayOutWorldEvent);
            boolean d = (boolean) ReflectionUtil.getDeclaredObject("d", PacketPlayOutWorldEvent.class, packetPlayOutWorldEvent);

            RecordedWorldEventAction recordedWorldEventAction = new RecordedWorldEventAction(a, b, c, d);
            recorderHandler.recordAction(recordedWorldEventAction);
            return;
        }
        if (packet instanceof PacketPlayOutEntityVelocity) {
            PacketPlayOutEntityVelocity packetPlayOutEntityVelocity = (PacketPlayOutEntityVelocity) packet;

            Entity entity = Bukkit.getWorld("world").getEntities().stream().filter(e -> e.getEntityId() == packetPlayOutEntityVelocity.getA()).findFirst().orElse(null);

            if (entity == null) return;

            RecordedEntityVelocityAction recordedEntityVelocityAction = new RecordedEntityVelocityAction(entity, packetPlayOutEntityVelocity.getA(), packetPlayOutEntityVelocity.getB(), packetPlayOutEntityVelocity.getC());
            recorderHandler.recordAction(recordedEntityVelocityAction);
            return;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();
        if (!recorderHandler.isRecording()) return;

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            RecordedPlayerAction recordedPlayerAction = new RecordedPlayerAction(player, player.getLocation());
            recordedPlayerAction.setDamage(true);

            recorderHandler.recordAction(recordedPlayerAction);
            return;
        }
        Entity entity = event.getEntity();

        RecordedEntityAction recordedEntityAction = new RecordedEntityAction(entity, entity.getLocation());
        recordedEntityAction.setDamage(true);

        recorderHandler.recordAction(recordedEntityAction);
        return;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording() || event.isCancelled()) return;

        Block block = event.getBlockPlaced();

        RecordedBlockAction recordedBlockAction = new RecordedBlockAction(new ItemBuilder(block.getType()).setDurability((short) block.getData()).toItemStack(), block.getLocation(), RecordedAction.RecordedActionType.BLOCK_CHANGE);
        recorderHandler.recordAction(recordedBlockAction);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording() || event.isCancelled()) return;

        Block block = event.getBlock();

        RecordedBlockAction recordedBlockAction = new RecordedBlockAction(new ItemStack(Material.AIR), block.getLocation(), RecordedAction.RecordedActionType.BLOCK_CHANGE);
        recorderHandler.recordAction(recordedBlockAction);
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {

        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording()) return;

        Item item = event.getItemDrop();

        RecordedItemAction recordedItemAction = new RecordedItemAction(item.getItemStack(), item.getLocation(), RecordedAction.RecordedActionType.ITEM_DROP);
        recorderHandler.recordAction(recordedItemAction);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording()) return;

        if (!(event.getEntity() instanceof Player)) {

            Entity entity = event.getEntity();

            RecordedEntityAction recordedEntityAction = new RecordedEntityAction(entity, entity.getLocation());
            recordedEntityAction.setDead(true);

            recorderHandler.recordAction(recordedEntityAction);
            return;
        }
        Player player = (Player) event.getEntity();

        RecordedPlayerAction recordedPlayerAction = new RecordedPlayerAction(player, player.getLocation());
        recordedPlayerAction.setDead(true);

        recorderHandler.recordAction(recordedPlayerAction);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();

        if (!recorderHandler.isRecording()) return;

        Player player = event.getPlayer();

        RecordedPlayerAction recordedPlayerAction = new RecordedPlayerAction(player, player.getLocation());
        recordedPlayerAction.setRespawn(true);

        recorderHandler.recordAction(recordedPlayerAction);
    }
}
