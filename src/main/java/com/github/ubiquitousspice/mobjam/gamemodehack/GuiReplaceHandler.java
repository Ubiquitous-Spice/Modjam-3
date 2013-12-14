package com.github.ubiquitousspice.mobjam.gamemodehack;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;

import java.util.EnumSet;

@SideOnly(Side.CLIENT)
public class GuiReplaceHandler implements ITickHandler
{

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;

		if (screen != null && screen.getClass().equals(GuiCreateWorld.class))
		{
			// insert our gui
			GuiScreen parent = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, (GuiCreateWorld) screen, 0);
			screen = new GuiMakeWorld(parent);
			Minecraft.getMinecraft().displayGuiScreen(screen);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel()
	{
		return "GuiReplacementHandler";
	}
}
