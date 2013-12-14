package com.github.ubiquitousspice.mobjam;

import com.github.ubiquitousspice.mobjam.blocks.ZombieBeacon;
import com.github.ubiquitousspice.mobjam.network.PacketHandler;
import com.github.ubiquitousspice.mobjam.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.logging.Logger;

import static com.github.ubiquitousspice.mobjam.Constants.MODID;
import static com.github.ubiquitousspice.mobjam.Constants.VERSION;

@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = true,
		channels = {MODID},
		clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {MODID},
																 packetHandler = PacketHandler.HandlerClient.class),
		serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {MODID},
																 packetHandler = PacketHandler.HandlerServer.class)
)
@Mod(modid = MODID, version = MODID)
public class MobJam
{
	@Mod.Instance(MODID)
	public static MobJam instance;

	@SidedProxy(
			modId = MODID,
			clientSide = "com.github.ubiquitousspice.mobjam.proxy.ClientProxy",
			serverSide = "com.github.ubiquitousspice.mobjam.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	//instances:

	ZombieBeacon zombieBeacon;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// set the Version
		ModMetadata meta = event.getModMetadata();
		meta.version = VERSION;

		// get Logger
		logger = event.getModLog();

		{
			ZombieBeacon beacon = new ZombieBeacon(501);
			GameRegistry.registerBlock(beacon, "mobjam:zombieBeacon");
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{

	}

}