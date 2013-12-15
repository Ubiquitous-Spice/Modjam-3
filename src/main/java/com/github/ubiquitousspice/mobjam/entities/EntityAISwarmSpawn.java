package com.github.ubiquitousspice.mobjam.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;

public class EntityAISwarmSpawn extends EntityAIBase
{

	private EntityLiving entityLiving;

	/**
	 * The PathNavigate of our entity
	 */
	private PathNavigate entityPathNavigate;

	public EntityAISwarmSpawn(EntityLiving entityLiving)
	{
		this.entityLiving = entityLiving;
		this.entityPathNavigate = entityLiving.getNavigator();
	}

	@Override
	public boolean isInterruptible()
	{
		return true;
	}

	@Override
	public void startExecuting()
	{
		ChunkCoordinates spawn = entityLiving.worldObj.getSpawnPoint();

		this.entityPathNavigate.tryMoveToXYZ(spawn.posX, spawn.posY, spawn.posZ, 1);
	}

	@Override
	public boolean shouldExecute()
	{
		return true;
	}
}
