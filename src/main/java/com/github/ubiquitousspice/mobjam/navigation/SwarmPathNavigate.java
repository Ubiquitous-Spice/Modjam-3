package com.github.ubiquitousspice.mobjam.navigation;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class SwarmPathNavigate extends PathNavigate
{
	public SwarmPathNavigate(EntityLiving par1EntityLiving, World par2World)
	{
		super(par1EntityLiving, par2World);
	}
}
