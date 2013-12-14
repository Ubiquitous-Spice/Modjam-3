package com.github.ubiquitousspice.mobjam.navigation;

import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.IBlockAccess;

public class SwarmPathFinder extends PathFinder
{
	public SwarmPathFinder(IBlockAccess par1IBlockAccess, boolean par2, boolean par3, boolean par4,
						   boolean par5)
	{
		super(par1IBlockAccess, par2, par3, par4, par5);
	}
}
