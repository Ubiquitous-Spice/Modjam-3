package com.github.ubiquitousspice.mobjam;

import com.github.ubiquitousspice.mobjam.blocks.Corpses;
import com.github.ubiquitousspice.mobjam.blocks.ZombieBeacon;
import com.github.ubiquitousspice.mobjam.entities.EntityFlyingFlesh;
import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import com.github.ubiquitousspice.mobjam.network.PacketHandler;
import com.github.ubiquitousspice.mobjam.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.world.EnumGameType;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

import static com.github.ubiquitousspice.mobjam.Constants.MODID;

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
	public static Corpses corpses;
	public static ZombieBeacon.PillarBrick pillarBrick;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

		// get Logger
		logger = event.getModLog();

		{
			zombieBeacon = new ZombieBeacon(501);
			GameRegistry.registerBlock(zombieBeacon, "mobjam:zombieBeacon");
			GameRegistry.registerTileEntity(ZombieBeacon.ZombieBeaconTE.class, "ZombieBeacon");

			corpses = new Corpses(502);
			corpses.setUnlocalizedName("mobjam.corpses");
			corpses.setTextureName("mobjam:corpses");
			GameRegistry.registerBlock(corpses, "mobjam:corpses");

			pillarBrick = new ZombieBeacon.PillarBrick(503);
			pillarBrick.setHardness(1.5F).setResistance(10.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("stonebricksmooth")
					   .setTextureName("stonebrick");
			GameRegistry.registerBlock(pillarBrick, "mobjam:PillarBrick");
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.hackGameMode();

		proxy.registerRenderer();

		MinecraftForge.EVENT_BUS.register(new EventHandler());

		List<Map<?, ?>> maplist = new LinkedList();
		for (Field field : EntityList.class.getDeclaredFields())
		{
			try
			{
				field.setAccessible(true);
				maplist.add((Map) field.get(null));
			}
			catch (Throwable t)
			{
			}
		}

		Map<Map, Object> removemap = new HashMap();
		for (Map map : maplist)
		{
			Set<Map.Entry> entryset = map.entrySet();
			entryloop:
			for (Map.Entry entry : entryset)
			{
				for (Object o1 : new Object[] {entry.getKey(), entry.getValue()})
				{
					for (Object o2 : new Object[] {EntitySwarmZombie.class, "Zombie", 54})
					{
						if (o1.equals(o2))
						{
							System.out.println("Removing from entity maps: " + entry.getKey() + " and " + entry.getValue());
							removemap.put(map, o1);
							continue entryloop;
						}
					}
				}
			}
		}
		for (Map.Entry<Map, Object> entry : removemap.entrySet())
		{
			entry.getKey().remove(entry.getValue());
		}
		EntityList.addMapping(EntitySwarmZombie.class, "Zombie", 54, 44975, 7969893);

		EntityRegistry.registerModEntity(EntityFlyingFlesh.class, "FlyingFlesh", 0, this, 20, 1, false);
	}

}
