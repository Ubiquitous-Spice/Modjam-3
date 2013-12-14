package com.github.ubiquitousspice.mobjam.proxy;

import com.github.ubiquitousspice.mobjam.Constants;
import com.github.ubiquitousspice.mobjam.MobJam;
import net.minecraft.world.EnumGameType;
import net.minecraftforge.common.EnumHelper;

public class CommonProxy
{
	public void registerRenderer() {}

	/**
	 * Hacks the gameMode stuff that we need to hack...
	 */
	public void hackGameMode()
	{
		// set gameMode enum
		MobJam.GAMEMODE = EnumHelper.addEnum(EnumGameType.class, Constants.GAMEMODE.toUpperCase(),
											 new Class[] {int.class, String.class},
											 new Object[] {3, Constants.GAMEMODE});

		// replace commands
	}
}
