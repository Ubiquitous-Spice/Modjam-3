package com.github.ubiquitousspice.modjam3;

import com.github.ubiquitousspice.modjam3.network.PacketHandler;
import com.github.ubiquitousspice.modjam3.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import java.util.logging.Logger;

import static com.github.ubiquitousspice.modjam3.Constants.MODID;
import static com.github.ubiquitousspice.modjam3.Constants.VERSION;

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
public class Modjam3
{
	@Mod.Instance(MODID)
	public static Modjam3 instance;

	@SidedProxy(
			modId = MODID,
			clientSide = "com.github.ubiquitousspice.modjam3.proxy.ClientProxy",
			serverSide = "com.github.ubiquitousspice.modjam3.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// set the Version
		ModMetadata meta = event.getModMetadata();
		meta.version = VERSION;

		// get Logger
		logger = event.getModLog();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{

	}

}
