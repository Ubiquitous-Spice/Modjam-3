package com.github.ubiquitousspice.mobjam.gamemodehack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;

@SideOnly(Side.CLIENT)
public class HackedMainMenu extends GuiMainMenu
{
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 1)
		{
			this.mc.displayGuiScreen(new HackedSelectWorld(this));
		}
		else
		{
			super.actionPerformed(par1GuiButton);
		}
	}
}