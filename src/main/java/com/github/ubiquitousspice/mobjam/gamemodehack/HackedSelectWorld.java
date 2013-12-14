package com.github.ubiquitousspice.mobjam.gamemodehack;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;

import java.lang.reflect.Field;
import java.util.Arrays;

public class HackedSelectWorld extends GuiSelectWorld
{
	public HackedSelectWorld(GuiScreen par1GuiScreen)
	{
		super(par1GuiScreen);
	}

	@Override
	public void initGui()
	{
		super.initGui();

		for (Field f : GuiSelectWorld.class.getDeclaredFields())
		{
			if (f.getType().isArray())
			{
				try
				{
					f.setAccessible(true);
					String[] strings = Arrays.copyOf((String[]) f.get(this), 4);
					strings[3] = "gamemode.mobjam";
					f.set(this, strings);
					return;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.enabled && par1GuiButton.id == 3)
		{
			this.mc.displayGuiScreen(new HackedCreateWorld(this));
		}
		else
		{
			super.actionPerformed(par1GuiButton);
		}
	}

}
