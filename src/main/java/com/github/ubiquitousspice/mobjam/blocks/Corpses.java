package com.github.ubiquitousspice.mobjam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
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
}
