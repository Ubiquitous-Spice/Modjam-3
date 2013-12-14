package com.github.ubiquitousspice.mobjam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ZombieBeacon extends Block implements ITileEntityProvider
{
	public ZombieBeacon(int par1)
	{
		super(par1, Material.rock);
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new ZombieBeaconTE();
	}

	public static class ZombieBeaconTE extends TileEntity
	{
	}

	public static class ZombieBeaconTESR extends TileEntitySpecialRenderer
	{
		@Override
		public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
		{

		}
	}
}