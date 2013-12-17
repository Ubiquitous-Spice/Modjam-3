package com.github.ubiquitousspice.mobjam.blocks;

import com.github.ubiquitousspice.mobjam.entities.EntityFlyingFlesh;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class Corpses extends Block
{
	public final float MAX_META = 6F;

	public Corpses(int par1)
	{
		super(par1, Material.sponge);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setBlockUnbreakable();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0, 0, 0, 1f, 1f, 1f);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		float height = (1 + meta) / (MAX_META + 1);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, height, 1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		float height = (1 + meta) / (MAX_META);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, height, 1.0F);
		return AxisAlignedBB.getAABBPool().getAABB(
				(double) x + this.minX,
				(double) y + this.minY,
				(double) z + this.minZ,
				(double) x + this.maxX,
				(double) (y + height),
				(double) z + this.maxZ);
	}

	@Override
	public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return 5;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return 20;
	}

	@Override
	public boolean isCollidable()
	{
		return true;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return true;
	}

	@Override
	public boolean isBlockBurning(World world, int x, int y, int z)
	{
		return super.isBlockBurning(world, x, y, z);
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
	{
		if (world.isRemote)
		{
			return;
		}

		int meta = world.getBlockMetadata(x, y, z) + 1;
		for (int i = 0; i < meta; i++)
		{
			System.out.println("EXPLODING!");
			int par2 = 6;
			Entity entity = new EntityFlyingFlesh(world);
			entity.setLocationAndAngles(x + .5d, y + 1, z + .5d, 0, 0);
			entity.motionY = world.rand.nextDouble() * 0.1D + .7D;
			entity.motionX += world.rand.nextGaussian() * 0.007499999832361937D * (double) par2;
			entity.motionY += world.rand.nextGaussian() * 0.007499999832361937D * (double) par2;
			entity.motionZ += world.rand.nextGaussian() * 0.007499999832361937D * (double) par2;
			world.spawnEntityInWorld(entity);
		}
		super.onBlockExploded(world, x, y, z, explosion);
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			((EntityPlayer) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 1000));
			((EntityPlayer) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 50, 20));
		}
		super.onEntityWalking(world, x, y, z, entity);
	}
}
