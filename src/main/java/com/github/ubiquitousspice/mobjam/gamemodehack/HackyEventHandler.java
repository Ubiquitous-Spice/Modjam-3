package com.github.ubiquitousspice.mobjam.gamemodehack;

import com.github.ubiquitousspice.mobjam.Constants;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import java.lang.reflect.Field;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class HackyEventHandler implements IConnectionHandler
{
	public HackyEventHandler()
	{
		try
		{
			worldInfoField = World.class.getDeclaredFields()[25];
			worldInfoField.setAccessible(true);
		}
		catch (Throwable t)
		{
			Throwables.propagate(t);
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void handleGuiOpen(GuiOpenEvent event)
	{
		if (event.gui == null)
		{
			return;
		}

		if (event.gui.getClass().equals(GuiCreateWorld.class))
		{
			GuiScreen parent = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, (GuiCreateWorld) event.gui, 0);
			event.gui = new HackedCreateWorld(parent);
		}
		else if (event.gui.getClass().equals(GuiSelectWorld.class))
		{
			for (Field f : GuiSelectWorld.class.getDeclaredFields())
			{
				if (f.getType().isArray())
				{
					try
					{
						f.setAccessible(true);
						String[] strings = Arrays.copyOf((String[]) f.get(event.gui), 4);
						strings[3] = I18n.getString("gamemode." + Constants.GAMEMODE);
						f.set(event.gui, strings);
						return;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	Field worldInfoField;

	@ForgeSubscribe
	public void handleWorldLoad(WorldEvent.Load event)
	{
		final World world = event.world;
		final WorldInfo worldInfo = world.getWorldInfo();
		try
		{
			worldInfoField.set(world, new HackedWorldInfo(worldInfo));
		}
		catch (Throwable t)
		{
			Throwables.propagate(t);
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
	{
		Minecraft mc = Minecraft.getMinecraft();
		HackedPlayerController controller = new HackedPlayerController(mc, (NetClientHandler) clientHandler);
		mc.playerController = controller;
		controller.setGameType(login.gameType);
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) { return null; }

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

	@Override
	public void connectionClosed(INetworkManager manager) {}
}
