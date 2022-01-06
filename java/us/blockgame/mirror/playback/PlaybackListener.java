package us.blockgame.mirror.playback;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.npc.NPC;
import us.blockgame.lib.timer.Timer;
import us.blockgame.lib.timer.event.TimerTickEvent;
import us.blockgame.lib.util.Logger;
import us.blockgame.mirror.MirrorPlugin;
import us.blockgame.mirror.packet.PacketHandler;
import us.blockgame.mirror.recorder.RecorderHandler;
import us.blockgame.mirror.recorder.action.RecordedAction;
import us.blockgame.mirror.recorder.action.impl.*;
import us.blockgame.mirror.util.RotationUtil;

import java.util.List;
import java.util.UUID;

public class PlaybackListener implements Listener {

    @EventHandler
    public void onTimerTick(TimerTickEvent event) {
        Timer timer = event.getTimer();

        if (!timer.getName().equals("Mirror-Playback")) return;

        RecorderHandler recorderHandler = MirrorPlugin.getInstance().getRecorderHandler();
        List<RecordedAction> recordedActionList = recorderHandler.getRecordedActions(timer.getRawTime());

        PlaybackHandler playbackHandler = MirrorPlugin.getInstance().getPlaybackHandler();

        if (timer.getRawTime() >= recorderHandler.getRecordedActions().keySet().size()) {
            playbackHandler.stopPlayback();
            return;
        }

        PacketHandler packetHandler = MirrorPlugin.getInstance().getPacketHandler();

        for (RecordedAction recordedAction : recordedActionList) {

            Location location = recordedAction.getLocation();
            UUID uuid = recordedAction.getUuid();

            switch (recordedAction.getRecordedActionType()) {
                case ITEM_DROP: {

                    RecordedItemAction recordedItemAction = (RecordedItemAction) recordedAction;

                    location.getWorld().dropItemNaturally(location, recordedItemAction.getItemStack());

                    break;
                }
                case BLOCK_CHANGE: {

                    RecordedBlockAction recordedBlockAction = (RecordedBlockAction) recordedAction;

                    Block block = location.getBlock();
                    ItemStack itemStack = recordedBlockAction.getItemStack();

                    block.setType(itemStack.getType());
                    block.setData((byte) itemStack.getDurability());
                    break;
                }
                case PLAYER_UPDATE: {
                    RecordedPlayerAction recordedPlayerAction = (RecordedPlayerAction) recordedAction;

                    if (playbackHandler.getNPC(uuid) == null) {
                        playbackHandler.addNPC(uuid, recordedPlayerAction.getName(), recordedPlayerAction.getSkinSignature(), recordedPlayerAction.getSkinValue(), recordedAction.getLocation());
                    }
                    NPC npc = playbackHandler.getNPC(recordedAction.getUuid());
                    npc.setLocation(recordedAction.getLocation());
                    npc.setArmorSet(recordedPlayerAction.getHelmet(), recordedPlayerAction.getChestplate(), recordedPlayerAction.getLeggings(), recordedPlayerAction.getBoots());

                    PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(npc.getEntityID(), 0, CraftItemStack.asNMSCopy(recordedPlayerAction.getItemInHand()));

                    packetHandler.sendAllPacket(packetPlayOutEntityEquipment);

                    DataWatcher dataWatcher = npc.entityPlayer.getDataWatcher();

                    dataWatcher.watch(10, (byte) 127);

                    dataWatcher.watch(0, recordedPlayerAction.getDataWatcherA());

                    PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(npc.getEntityID(), dataWatcher, true);
                    packetHandler.sendAllPacket(packetPlayOutEntityMetadata);

                    if (recordedPlayerAction.isSwing()) {
                        PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(npc.entityPlayer, 0);
                        packetHandler.sendAllPacket(packetPlayOutAnimation);
                    }

                    if (recordedPlayerAction.isDamage()) {
                        PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(npc.entityPlayer, 1);

                        packetHandler.sendAllPacket(packetPlayOutAnimation);
                    }
                    if (recordedPlayerAction.isDead()) {
                        npc.entityPlayer.setHealth(0f);
                    }
                    if (recordedPlayerAction.isRespawn()) {
                        playbackHandler.removeNPC(npc.getUUID());
                    }
                    npc.updateRelativeLocation();
                    break;
                }
                case ENTITY_UPDATE: {

                    RecordedEntityAction recordedEntityAction = (RecordedEntityAction) recordedAction;

                    Entity entity = playbackHandler.getEntity(recordedAction.getUuid());

                    if (entity == null) {
                        entity = playbackHandler.spawnEntity(recordedEntityAction.getEntityType(), recordedAction.getUuid(), recordedAction.getLocation());
                    }

                    net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();

                    nmsEntity.setLocation(recordedAction.getLocation().getX(), recordedAction.getLocation().getY(), recordedAction.getLocation().getZ(), recordedAction.getLocation().getYaw(), recordedAction.getLocation().getPitch());
                    RotationUtil.setLookDirection(nmsEntity, recordedAction.getLocation().getYaw(), recordedAction.getLocation().getPitch());

                    if (entity instanceof Zombie) {

                        Zombie zombie = (Zombie) entity;

                        zombie.setVillager(recordedEntityAction.isVillager());
                        zombie.setBaby(recordedEntityAction.isBaby());
                    }

                    if (recordedEntityAction.isDamage()) {
                        PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(nmsEntity, 1);
                        packetHandler.sendAllPacket(packetPlayOutAnimation);
                    }

                    if (recordedEntityAction.isDead() && entity instanceof Damageable) {
                        ((Damageable) entity).setHealth(0f);
                    }
                    break;
                }
                case SOUND: {

                    RecordedSoundAction recordedSoundAction = (RecordedSoundAction) recordedAction;

                    PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect = new PacketPlayOutNamedSoundEffect(recordedSoundAction.getName(), location.getX(), location.getY(), location.getZ(), recordedSoundAction.getV(), recordedSoundAction.getV1());
                    packetHandler.sendAllPacket(packetPlayOutNamedSoundEffect);
                    break;
                }

                case WORLD_EVENT: {
                    RecordedWorldEventAction recordedWorldEventAction = (RecordedWorldEventAction) recordedAction;

                    PacketPlayOutWorldEvent packetPlayOutWorldEvent = new PacketPlayOutWorldEvent(recordedWorldEventAction.getA(), recordedWorldEventAction.getB(), recordedWorldEventAction.getC(), recordedWorldEventAction.isD());
                    packetHandler.sendAllPacket(packetPlayOutWorldEvent);
                    break;
                }
                case ENTITY_VELOCITY: {
                    RecordedEntityVelocityAction recordedEntityVelocityAction = (RecordedEntityVelocityAction) recordedAction;
                    break;
                }
            }
        }
    }
}
