package com.github.ubiquitousspice.mobjam.navigation;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.Vec3;

public class EntityAIBreakBlocks extends EntityAIBase
{
	private int breakingTime;
	private int field_75358_j = -1;
	private EntityLiving theEntity;
	private SwarmPathNavigate navigate;
	private int ticksAtLastPos;
	private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

	public EntityAIBreakBlocks(EntityLiving par1EntityLiving, SwarmPathNavigate navigate)
	{
		theEntity = par1EntityLiving;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		return theEntity.isCollidedHorizontally && isStopped();
	}

	private boolean isStopped()
	{
		boolean isStopped = false;
		Vec3 vec3 = theEntity.getPosition(1);
		if (navigate.getTotalTicks() - this.ticksAtLastPos > 100)
		{
			if (vec3.squareDistanceTo(this.lastPosCheck) < 2.25D)
			{
				isStopped = true;
			}

			this.ticksAtLastPos = navigate.getTotalTicks();
			this.lastPosCheck.xCoord = vec3.xCoord;
			this.lastPosCheck.yCoord = vec3.yCoord;
			this.lastPosCheck.zCoord = vec3.zCoord;
		}
		return isStopped;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		super.startExecuting();
		this.breakingTime = 0;
	}

//	/**
//	 * Returns whether an in-progress EntityAIBase should continue executing
//	 */
//	public boolean continueExecuting()
//	{
//		double d0 = this.theEntity.getDistanceSq((double)this.entityPosX, (double)this.entityPosY, (double)this.entityPosZ);
//		return this.breakingTime <= 240 && !this.targetDoor.isDoorOpen(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ) && d0 < 4.0D;
//	}

//	/**
//	 * Resets the task
//	 */
//	public void resetTask()
//	{
//		super.resetTask();
//		this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.entityPosX, this.entityPosY, this.entityPosZ, -1);
//	}

	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		super.updateTask();

		Vec3 vec3 = theEntity.getPosition(1);

		if (this.theEntity.getRNG().nextInt(20) == 0)
		{

			this.theEntity.worldObj.playAuxSFX(1010, (int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord, 0);
		}

		++this.breakingTime;
		int i = (int) ((float) this.breakingTime / 240.0F * 10.0F);

		if (i != this.field_75358_j)
		{
			this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, (int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord, i);
			this.field_75358_j = i;
		}

		if (this.breakingTime == 240)
		{
			this.theEntity.worldObj.setBlockToAir((int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord);
			this.theEntity.worldObj.playAuxSFX(1012, (int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord, 0);
			//dddthis.theEntity.worldObj.playAuxSFX(2001, (int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord, this.targetDoor.blockID);
		}
	}
}
