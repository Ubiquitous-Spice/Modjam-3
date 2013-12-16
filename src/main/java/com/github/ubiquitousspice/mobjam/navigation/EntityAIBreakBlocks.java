package com.github.ubiquitousspice.mobjam.navigation;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIBreakBlocks extends EntityAIBase
{
	protected EntityLiving theEntity;
	protected int entityPosX;
	protected int entityPosY;
	protected int entityPosZ;
	protected Block targetDoor;

	/**
	 * If is true then the Entity has stopped Door Interaction and compoleted the task.
	 */
	boolean hasStoppedDoorInteraction;
	float entityPositionX;
	float entityPositionZ;

	public EntityAIBreakBlocks(EntityLiving par1EntityLiving)
	{
		this.theEntity = par1EntityLiving;
		setMutexBits(~0);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean supercontinueExecuting()
	{
		return !this.hasStoppedDoorInteraction;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void superstartExecuting()
	{
		this.hasStoppedDoorInteraction = false;
		this.entityPositionX = (float) ((double) ((float) this.entityPosX + 0.5F) - this.theEntity.posX);
		this.entityPositionZ = (float) ((double) ((float) this.entityPosZ + 0.5F) - this.theEntity.posZ);
	}

	/**
	 * Updates the task
	 */
	public void superupdateTask()
	{
		float f = (float) ((double) ((float) this.entityPosX + 0.5F) - this.theEntity.posX);
		float f1 = (float) ((double) ((float) this.entityPosZ + 0.5F) - this.theEntity.posZ);
		float f2 = this.entityPositionX * f + this.entityPositionZ * f1;

		if (f2 < 0.0F)
		{
			this.hasStoppedDoorInteraction = true;
		}
	}

	/**
	 * Determines if a door can be broken with AI.
	 */
	private Block findUsableDoor(int par1, int par2, int par3)
	{
		int l = this.theEntity.worldObj.getBlockId(par1, par2, par3);
		return Block.blocksList[l];
	}

	private int breakingTime;
	private int field_75358_j = -1;

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		System.out.println(theEntity.motionX + " " + theEntity.motionY + " " + theEntity.motionZ);
		//theEntity.motionY+=5;
		return Math.abs(theEntity.motionX) + Math.abs(theEntity.motionY) + Math.abs(theEntity.motionZ) < 0 && false;
//		if (!true)
//		{
//			System.out.println("that");
//			return false;
//		}
//		else
//		{
//			PathNavigate pathnavigate = this.theEntity.getNavigator();
//			PathEntity pathentity = pathnavigate.getPath();
//			//System.out.println(pathentity+" "+!pathentity.isFinished() +" "+pathnavigate.getCanBreakDoors());
//			if (pathentity != null && !pathentity.isFinished() && pathnavigate.getCanBreakDoors())
//			{
//				System.out.println("test");
//				for (int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i)
//				{
//					PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
//					this.entityPosX = pathpoint.xCoord;
//					this.entityPosY = pathpoint.yCoord + 1;
//					this.entityPosZ = pathpoint.zCoord;
//
//					if (this.theEntity.getDistanceSq((double) this.entityPosX, this.theEntity.posY, (double) this.entityPosZ) <= 2.25D)
//					{
//						this.targetDoor = this.findUsableDoor(this.entityPosX, this.entityPosY, this.entityPosZ);
//
//						if (this.targetDoor != null)
//						{
//							return true;
//						}
//					}
//				}
//
//				this.entityPosX = MathHelper.floor_double(this.theEntity.posX);
//				this.entityPosY = MathHelper.floor_double(this.theEntity.posY + 1.0D);
//				this.entityPosZ = MathHelper.floor_double(this.theEntity.posZ);
//				this.targetDoor = this.findUsableDoor(this.entityPosX, this.entityPosY, this.entityPosZ);
//				return this.targetDoor != null;
//			}
//			else
//			{
//				return false;
//			}
//		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		superstartExecuting();
		this.breakingTime = 0;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		System.out.println("continueexec");
		double d0 = this.theEntity.getDistanceSq((double) this.entityPosX, (double) this.entityPosY, (double) this.entityPosZ);
		return (this.breakingTime <= 240) &&
			   (d0 < 4.0D);
	}

	/**
	 * Resets the task
	 */
	public void resetTask()
	{
		super.resetTask();
		this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.entityPosX, this.entityPosY, this.entityPosZ, -1);
	}

	/**
	 * Updates the task
	 */
	public void updateTask()
	{
		superupdateTask();
		System.out.println(hasStoppedDoorInteraction + " " + targetDoor);
		if (this.theEntity.getRNG().nextInt(20) == 0)
		{
			this.theEntity.worldObj.playAuxSFX(1010, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
		}

		++this.breakingTime;
		int i = (int) ((float) this.breakingTime / 240.0F * 10.0F);

		if (i != this.field_75358_j)
		{
			this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.entityId, this.entityPosX, this.entityPosY, this.entityPosZ, i);
			this.field_75358_j = i;
		}

		if (this.breakingTime == 240 && this.theEntity.worldObj.difficultySetting == 3)
		{
			this.theEntity.worldObj.setBlockToAir(this.entityPosX, this.entityPosY, this.entityPosZ);
			this.theEntity.worldObj.playAuxSFX(1012, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
			this.theEntity.worldObj.playAuxSFX(2001, this.entityPosX, this.entityPosY, this.entityPosZ, this.targetDoor.blockID);
		}
	}
}
