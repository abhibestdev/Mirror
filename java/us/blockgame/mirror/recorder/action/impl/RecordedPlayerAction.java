package us.blockgame.mirror.recorder.action.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.DataWatcher;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockgame.mirror.recorder.action.RecordedAction;

import java.util.UUID;

public class RecordedPlayerAction extends RecordedAction {

    @Setter @Getter private String name;
    @Setter @Getter private boolean damage;
    @Setter @Getter private String skinSignature;
    @Setter @Getter private String skinValue;
    @Setter @Getter private ItemStack itemInHand;
    @Setter @Getter private ItemStack helmet;
    @Setter @Getter private ItemStack chestplate;
    @Setter @Getter private ItemStack leggings;
    @Setter @Getter private ItemStack boots;
    @Setter @Getter private byte dataWatcherA;
    @Setter @Getter private boolean dead;
    @Setter @Getter private boolean respawn;
    @Setter @Getter private boolean swing;

    public RecordedPlayerAction(UUID uuid, Location location, String name) {
        super(RecordedActionType.PLAYER_UPDATE, uuid, location);

        this.name = name;
    }

    public RecordedPlayerAction(Player player, Location location) {
        super(RecordedActionType.PLAYER_UPDATE, player.getUniqueId(), location);

        this.name = player.getName();

        CraftPlayer craftPlayer = (CraftPlayer) player;

        GameProfile gameProfile = craftPlayer.getProfile();

        PropertyMap propertyMap = gameProfile.getProperties();
        for (Property property : propertyMap.get("textures")) {
            skinSignature = property.getSignature();
            skinValue = property.getValue();
        }

        itemInHand = player.getItemInHand();
        helmet = player.getInventory().getHelmet();
        chestplate = player.getInventory().getChestplate();
        leggings = player.getInventory().getLeggings();
        boots = player.getInventory().getBoots();

        DataWatcher dataWatcher = craftPlayer.getHandle().getDataWatcher();
        dataWatcherA = dataWatcher.getByte(0);
    }
}
