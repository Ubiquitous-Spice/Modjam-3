package com.github.ubiquitousspice.mobjam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Corpses extends Block
{
	private final float MAX_META = 7F;

	public Corpses(int par1)
	{
		super(par1, Material.sponge);
		this.setCreativeTab(CreativeTabs.tabBlock);
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
		float height = (1 + meta) / (MAX_META);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, height, 1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		float height = (1 + meta) / (MAX_META);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, height, 1.0F);
		return AxisAlignedBB.getAABBPool().getAABB((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX,
												   (double) (y + height), (double) z + this.maxZ);
	}

}
