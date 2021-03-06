package com.github.ubiquitousspice.mobjam.entities;

import com.github.ubiquitousspice.mobjam.MobJam;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
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
		this.motionY -= 0.03999999910593033D;

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
				tryPlaceFlesh(x, y, z);
			}
		}

		if (this.posX == this.prevPosX && this.posY == this.prevPosY && this.posZ == this.prevPosZ)
		{
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY);
			int z = MathHelper.floor_double(this.posZ);
			tryPlaceFlesh(x, y, z);
			return;
		}

		this.motionX *= (double) f;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= (double) f;

		if (this.onGround)
		{
			this.motionY *= -0.5D;
		}
	}

	private void tryPlaceFlesh(int x, int y, int z)
	{
		if (worldObj.isRemote)
		{
			return;
		}

		int i = worldObj.getBlockId(x, y, z);

		if (i == MobJam.corpses.blockID)
		{
			int meta = worldObj.getBlockMetadata(x, y, z);
			if (meta >= MobJam.corpses.MAX_META)
			{
				worldObj.setBlock(x, y + 1, z, MobJam.corpses.blockID);
			}
			else
			{
				worldObj.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
			}
			this.setDead();
			return;
		}
		else
		{
			worldObj.setBlock(x, y, z, MobJam.corpses.blockID);
			this.setDead();
			return;
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
		player.addPotionEffect(new PotionEffect(Potion.confusion.id, 30, 10));
	}

	@Override
	public float getBlockExplosionResistance(Explosion par1Explosion, World par2World, int par3, int par4, int par5, Block par6Block)
	{
		return 999f;
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
