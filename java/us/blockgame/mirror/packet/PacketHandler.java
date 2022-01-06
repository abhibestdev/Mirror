package us.blockgame.mirror.packet;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHandler {

    public void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendAllPacket(Packet packet) {
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packet));
    }
}
