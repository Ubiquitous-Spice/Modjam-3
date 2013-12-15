package com.github.ubiquitousspice.mobjam.proxy;

import com.github.ubiquitousspice.mobjam.blocks.ZombieBeacon;
import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import com.github.ubiquitousspice.mobjam.entities.SwarmZombieRenderer;
import com.github.ubiquitousspice.mobjam.gamemodehack.GuiReplaceHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderZombie;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderer()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(ZombieBeacon.ZombieBeaconTE.class, new ZombieBeacon.ZombieBeaconTESR());
		RenderingRegistry.registerEntityRenderingHandler(EntitySwarmZombie.class, new SwarmZombieRenderer());
	}

	@Override
	public void hackGameMode()
	{
		super.hackGameMode();

		// hack in the gui.
		TickRegistry.registerTickHandler(new GuiReplaceHandler(), Side.CLIENT);
	}
}