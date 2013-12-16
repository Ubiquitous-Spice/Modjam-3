package com.github.ubiquitousspice.mobjam.entities;

import com.github.ubiquitousspice.mobjam.MobJam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFlyingFlesh extends Entity
{

	public EntityFlyingFlesh(World par1World)
	{
		super(par1World);
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	public void onUpdate()
	{
		super.onUpdate();

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.moveEntity(this.motionX, this.motionY, this.motionZ);

		float f = 0.98F;

		if (this.onGround)
		{
			f = 0.58800006F;
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY);
			int z = MathHelper.floor_double(this.posZ);

			if (!worldObj.isRemote)
			{
				int i = worldObj.getBlockId(x, y, z);

				if (i == MobJam.corpses.blockID)
				{
					int meta = worldObj.getBlockMetadata(x, y, z);
					worldObj.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
				}
				else
				{
					worldObj.setBlock(x, y, z, MobJam.corpses.blockID);
				}
			}
		}

		this.motionX *= (double) f;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= (double) f;

		if (this.onGround)
		{
			this.motionY *= -0.5D;
		}
	}

	@Override
	public boolean handleWaterMovement()
	{
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		return false; // nope.
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(Potion.confusion.id, 10, 10));
	}

	@Override
	public boolean canAttackWithItem()
	{
		return false;
	}

	@Override
	protected void entityInit() { }

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}
}
