package com.github.ubiquitousspice.mobjam.proxy;

import com.github.ubiquitousspice.mobjam.blocks.ZombieBeacon;
import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import com.github.ubiquitousspice.mobjam.entities.EntityTarget;
import com.github.ubiquitousspice.mobjam.entities.SwarmZombieRenderer;
import com.github.ubiquitousspice.mobjam.gamemodehack.HackyEventHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderer()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(ZombieBeacon.ZombieBeaconTE.class, new ZombieBeacon.ZombieBeaconTESR());
		RenderingRegistry.registerEntityRenderingHandler(EntitySwarmZombie.class, new SwarmZombieRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityTarget.class, new EntityTarget.EntityTargetRenderer());
	}

	@Override
	public void hackGameMode()
	{
		super.hackGameMode();

		// hack in the gui.
		HackyEventHandler handler = new HackyEventHandler();
		NetworkRegistry.instance().registerConnectionHandler(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}
}