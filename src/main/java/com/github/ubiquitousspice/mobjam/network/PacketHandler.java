package com.github.ubiquitousspice.mobjam.network;

import com.github.ubiquitousspice.mobjam.Constants;
import com.github.ubiquitousspice.mobjam.Modjam3;
import com.google.common.base.Throwables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

import java.util.logging.Level;

public abstract class PacketHandler implements IPacketHandler
{

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		// not out channel, ignore
		if (!packet.channel.equals(Constants.MODID))
		{
			return;
		}

		try
		{
			ByteArrayDataInput input = ByteStreams.newDataInput(packet.data);

			// better be a byte
			int id = input.readByte();
			PacketBase loadedPacket = null;

			switch (id)
			{
				// one by one....
				// read their streams
			}

			if (loadedPacket == null)
			{
				throw new RuntimeException("Unknown " + Constants.MODID + " packet type: " + id);
			}

			action((EntityPlayerMP) player, loadedPacket);
		}
		catch (Exception e)
		{
			Modjam3.logger.log(Level.SEVERE, "Error reading " + Constants.MODID + " packet!", e);
			Throwables.propagate(e);
		}
	}

	protected abstract void action(EntityPlayerMP player, PacketBase packet);

	public static class HandlerServer extends PacketHandler
	{
		@Override
		protected void action(EntityPlayerMP player, PacketBase packet)
		{
			World world = player.worldObj;
			packet.actionServer(world, player);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class HandlerClient extends PacketHandler
	{
		@Override
		protected void action(EntityPlayerMP player, PacketBase packet)
		{
			WorldClient world = FMLClientHandler.instance().getClient().theWorld;
			packet.actionClient(world, player);
		}
	}
}
