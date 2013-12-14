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
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumGameType;
import net.minecraftforge.common.MinecraftForge;

import java.util.logging.Logger;

import static com.github.ubiquitousspice.mobjam.Constants.MODID;
import static com.github.ubiquitousspice.mobjam.Constants.VERSION;

@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = true,
		channels = {MODID},
		clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {MODID}, packetHandler = PacketHandler.HandlerClient.class),
		serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {MODID}, packetHandler = PacketHandler.HandlerServer.class)
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

	public static EnumGameType GAMEMODE;

	public static ZombieBeacon zombieBeacon;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// set the Version
		ModMetadata meta = event.getModMetadata();
		meta.version = VERSION;

		// get Logger
		logger = event.getModLog();

		{
			zombieBeacon = new ZombieBeacon(501);
			GameRegistry.registerBlock(zombieBeacon, "mobjam:zombieBeacon");
			GameRegistry.registerTileEntity(ZombieBeacon.ZombieBeaconTE.class, "ZombieBeacon");
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.hackGameMode();

		proxy.registerRenderer();

		MinecraftForge.EVENT_BUS.register(new EventHandler());

		MinecraftForge.TERRAIN_GEN_BUS.register(new WorldGenHandler());

		// test recipe
		GameRegistry.addShapelessRecipe(new ItemStack(zombieBeacon), Block.dirt);
	}

}
