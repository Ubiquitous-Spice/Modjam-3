package com.github.ubiquitousspice.mobjam.network;

import com.github.ubiquitousspice.mobjam.Constants;
import com.github.ubiquitousspice.mobjam.MobJam;
import com.google.common.base.Throwables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.logging.Level;

public abstract class PacketBase
{
	public PacketBase()
	{
	}

	public PacketBase(ByteArrayDataInput input) throws IOException
	{
	}

	public Packet250CustomPayload getPacket250()
	{
		try
		{
			ByteArrayDataOutput output = ByteStreams.newDataOutput();
			output.writeByte(getPacketId());
			writeData(output);

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = Constants.MODID;
			packet.data = output.toByteArray();
			packet.length = packet.data.length;
			return packet;
		}
		catch (Exception e)
		{
			MobJam.logger
				  .log(Level.SEVERE, "Error building " + Constants.MODID + " packet! " + getClass().getSimpleName(),
					   e);
			Throwables.propagate(e);
		}

		return null;
	}

	/**
	 * @return the packetID, had better be below 256
	 */
	public abstract int getPacketId();

	/**
	 * The PacketID is already written
	 */
	public abstract void writeData(ByteArrayDataOutput data);

	@SideOnly(Side.CLIENT)
	public abstract void actionClient(WorldClient world, EntityPlayerMP player);

	public abstract void actionServer(World world, EntityPlayerMP player);
}
