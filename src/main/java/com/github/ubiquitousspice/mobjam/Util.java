package com.github.ubiquitousspice.mobjam;

import com.github.ubiquitousspice.mobjam.network.PacketBase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class Util
{
	public static void sendPacketToServer(PacketBase packet)
	{
		PacketDispatcher.sendPacketToServer(packet.getPacket250());
	}

	public static void sendPacketToAllPlayers(PacketBase packet)
	{
		PacketDispatcher.sendPacketToAllPlayers(packet.getPacket250());
	}

	public static void sendPacketToAllInWorld(PacketBase packet, World world)
	{
		PacketDispatcher.sendPacketToAllInDimension(packet.getPacket250(), world.provider.dimensionId);
	}

	public static void sendPacketToAllInDimension(PacketBase packet, int dimensionId)
	{
		PacketDispatcher.sendPacketToAllInDimension(packet.getPacket250(), dimensionId);
	}

	public static void sendPacketToPlayer(PacketBase packet, EntityPlayer player)
	{
		PacketDispatcher.sendPacketToPlayer(packet.getPacket250(), (Player) player);
	}

	public static void sendPacketToAllAround(Entity entity, double range, PacketBase packet)
	{
		PacketDispatcher.sendPacketToAllAround(entity.posX, entity.posY, entity.posZ, range, entity.dimension, packet.getPacket250());
	}

	public static void sendPacketToAllAround(double X, double Y, double Z, double range, int dimensionId, PacketBase packet)
	{
		PacketDispatcher.sendPacketToAllAround(X, Y, Z, range, dimensionId, packet.getPacket250());
	}

	public static boolean isOurGameMode()
	{
		return FMLCommonHandler.instance().getSidedDelegate().getServer().getGameType() == MobJam.GAMEMODE;
	}

	public static boolean isOurGameMode(EntityPlayerMP player)
	{
		return player.theItemInWorldManager.getGameType() == MobJam.GAMEMODE;
	}
}
