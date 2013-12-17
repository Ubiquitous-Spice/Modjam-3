package com.github.ubiquitousspice.mobjam.proxy;

import com.github.ubiquitousspice.mobjam.blocks.ZombieBeacon;
import com.github.ubiquitousspice.mobjam.entities.EntityFlyingFlesh;
import com.github.ubiquitousspice.mobjam.entities.EntityFlyingFleshRender;
import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import com.github.ubiquitousspice.mobjam.entities.SwarmZombieRenderer;
import com.github.ubiquitousspice.mobjam.gamemodehack.HackyEventHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;

import static cpw.mods.fml.relauncher.Side.CLIENT;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderer()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(ZombieBeacon.ZombieBeaconTE.class, new ZombieBeacon.ZombieBeaconTESR());
		RenderingRegistry.registerEntityRenderingHandler(EntitySwarmZombie.class, new SwarmZombieRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingFlesh.class, new EntityFlyingFleshRender());
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

	@SideOnly(CLIENT)
	RenderItem renderItem = new RenderItem();
	@SideOnly(CLIENT)
	ItemStack compassStack = new ItemStack(Item.compass);

	@ForgeSubscribe
	@SideOnly(CLIENT)
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			renderItem.zLevel = 200.0F;
			renderItem.renderItemIntoGUI(null, Minecraft.getMinecraft().getTextureManager(), compassStack, 0, 0);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
}