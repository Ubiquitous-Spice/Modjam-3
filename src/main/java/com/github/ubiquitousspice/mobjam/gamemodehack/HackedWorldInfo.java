package com.github.ubiquitousspice.mobjam.gamemodehack;

import com.github.ubiquitousspice.mobjam.MobJam;
import com.google.common.base.Throwables;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;

import java.lang.reflect.Field;

public class HackedWorldInfo extends WorldInfo
{
	public HackedWorldInfo(WorldInfo par1WorldInfo)
	{
		super(par1WorldInfo);
		try
		{
			gameTypeField = WorldInfo.class.getDeclaredFields()[18];
			gameTypeField.setAccessible(true);
			hardcoreField = WorldInfo.class.getDeclaredFields()[20];
			hardcoreField.setAccessible(true);
			if (gameTypeField.get(this) == MobJam.GAMEMODE)
			{
				hardcoreField.set(this, true);
			}
		}
		catch (Throwable t)
		{
			Throwables.propagate(t);
		}
	}

	Field gameTypeField;
	Field hardcoreField;

	public NBTTagCompound getNBTTagCompound()
	{
		NBTTagCompound nbttagcompound = super.getNBTTagCompound();
		fixHardcore(nbttagcompound);
		return nbttagcompound;
	}

	/**
	 * Creates a new NBTTagCompound for the world, with the given NBTTag as the "Player"
	 */
	public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
	{
		NBTTagCompound nbttagcompound = super.cloneNBTCompound(par1NBTTagCompound);
		fixHardcore(nbttagcompound);
		return nbttagcompound;
	}

	protected void fixHardcore(NBTTagCompound nbttagcompound)
	{
		try
		{
			if (gameTypeField.get(this) == MobJam.GAMEMODE)
			{
				nbttagcompound.setBoolean("hardcore", false);
			}
		}
		catch (Throwable t)
		{
			Throwables.propagate(t);
		}
	}
}
