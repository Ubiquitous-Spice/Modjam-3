package com.github.ubiquitousspice.mobjam;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class WorldGenHandler
{
	@ForgeSubscribe
	public void genSpawn(PopulateChunkEvent.Post event)
	{
		ChunkCoordinates spawn = event.world.getSpawnPoint();
		if (spawn.posX / 16 == event.chunkX && spawn.posZ / 16 == event.chunkZ)
		{
			final World world = event.world;
			final int cx = event.chunkX * 16 + 8;
			final int cz = event.chunkZ * 16 + 8;
			final int highest = getHighestBlock(world, cx, cz);
			genFort(world, cx, highest, cz);
		}
	}

	private static final int RADIUS = 3;

	private static final int HEIGHT = 3;

	private static void genFort(World world, int cx, int highestBlock, int cz)
	{
		int blockid = Block.stoneBrick.blockID;

		//gen floor
		for (int x = cx - RADIUS; x <= cx + RADIUS; x++)
		{
			for (int z = cz - RADIUS; z <= cz + RADIUS; z++)
			{
				world.setBlock(x, highestBlock, z, blockid);
			}
		}
		//gen walls
		{
			//x walls
			for (int i : new int[] {1, -1})
			{
				for (int z = cz - RADIUS; z <= cz + RADIUS; z++)
				{
					for (int y = highestBlock; y <= highestBlock + HEIGHT + 1; y++)
					{
						world.setBlock(cx + (RADIUS * i), y, z, blockid);
					}
				}
			}
			//z walls
			for (int i : new int[] {1, -1})
			{
				for (int x = cx - RADIUS; x <= cx + RADIUS; x++)
				{
					for (int y = highestBlock; y <= highestBlock + HEIGHT + 1; y++)
					{
						world.setBlock(x, y, cz + (RADIUS * i), blockid);
					}
				}
			}
		}
		//holes in walls
		for (int i : new int[] {1, -1})
		{
			world.setBlock(cx, highestBlock + 1, cz + (RADIUS * i), 0);
			world.setBlock(cx, highestBlock + 2, cz + (RADIUS * i), 0);
			world.setBlock(cx + (RADIUS * i), highestBlock + 1, cz, 0);
			world.setBlock(cx + (RADIUS * i), highestBlock + 2, cz, 0);
		}

		// clear insides
		for (int x = cx - RADIUS + 1; x < cx + RADIUS; x++)
		{
			for (int z = cz - RADIUS + 1; z < cz + RADIUS; z++)
			{
				for (int y = highestBlock + 1; y <= highestBlock + HEIGHT + 1; y++)
				{
					world.setBlock(x, y, z, 0);
				}
			}
		}

		//beacon
		world.setBlock(cx, highestBlock + 2, cz, MobJam.zombieBeacon.blockID);
		world.setBlock(cx, highestBlock + 1, cz, blockid);
	}

	protected static int getHighestBlock(final World world, final int cx, final int cz)
	{
		int highest = -1;
		for (int x = cx - RADIUS; x <= cx + RADIUS; x++)
		{
			for (int z = cz - RADIUS; z <= cz + RADIUS; z++)
			{
				int y = 255;
				while (!isValidTop(world, x, y, z) && y > highest)
				{
					y--;
				}
				if ((world.getBlockId(x, y, z) != 0))
				{
					highest = y;
				}
			}
		}
		return highest;
	}

	/**
	 * Returns true if the block is not considered a valid block to gen on. It isnt the ground.
	 */
	protected static boolean isValidTop(World world, int x, int y, int z)
	{
		if (world.isAirBlock(x, y, z))
		{
			return false;
		}

		Block block = Block.blocksList[world.getBlockId(x, y, z)];

		if (block.isLeaves(world, x, y, z) || block.isWood(world, x, y, z) || block.isBlockFoliage(world, x, y, z))
		{
			return false;
		}

		return block.isBlockSolidOnSide(world, x, y, z, ForgeDirection.UP);
	}
}
