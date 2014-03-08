package com.github.ubiquitousspice.mobjam.navigation;

import com.github.ubiquitousspice.mobjam.Util;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.ChunkPosition;

public class EntityAISwarmSpawn extends EntityAIBase
{

	private boolean shouldRun = true;
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
		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		return true;
	}

	@Override
	public void startExecuting()
	{
		try
		{
			ChunkPosition spawn = Util.getBeaconLoc(entityLiving.worldObj);
			this.entityPathNavigate.tryMoveToXYZ(spawn.x, spawn.y, spawn.z, 1);
		}
		catch (Exception e)
		{
			if (Util.isOurGameMode(this.entityLiving.worldObj)) {
			e.printStackTrace();
			}
		}
	}

	@Override
	public boolean shouldExecute()
	{
		shouldRun = !shouldRun;
		return shouldRun;//I don't know why this works.
	}
}
