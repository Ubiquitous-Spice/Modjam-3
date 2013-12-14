package com.github.ubiquitousspice.mobjam.gamemodehack;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import java.util.EnumSet;

@SideOnly(Side.CLIENT)
public class GuiReplaceHandler implements ITickHandler
{

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;

		if (screen != null && screen.getClass().equals(GuiMainMenu.class))
		{
			// insert our main menu that later uses our gui
			Minecraft.getMinecraft().displayGuiScreen(new HackedMainMenu());
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
