package com.github.ubiquitousspice.mobjam.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTarget extends EntityLiving
{
	public EntityTarget(World par1World)
	{
		super(par1World);
		this.setSize(.5f, .5f);
	}

	@Override
	protected void entityInit()
	{
		// do nothing
	}

	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if ("mob".equalsIgnoreCase(source.damageType))
		{
			this.setDead();
			// TODO: end game.
			return true;
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	public static class EntityTargetRenderer extends Render
	{

		@Override
		public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
		{
			// do nothing
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity entity)
		{
			return null;
		}
	}
}
