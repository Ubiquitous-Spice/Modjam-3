package com.github.ubiquitousspice.mobjam;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
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
			System.out.println("GENNING");
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
		//gen floor
		for (int x = cx - RADIUS; x <= cx + RADIUS; x++)
		{
			for (int z = cz - RADIUS; z <= cz + RADIUS; z++)
			{
				world.setBlock(x, highestBlock, z, Block.stoneBrick.blockID);
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
						world.setBlock(cx + (RADIUS * i), y, z, Block.stoneBrick.blockID);
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
						world.setBlock(x, y, cz + (RADIUS * i), Block.stoneBrick.blockID);
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
		//beacon
		world.setBlock(cx, highestBlock + 1, cz, MobJam.zombieBeacon.blockID);
	}

	protected static int getHighestBlock(final World world, final int cx, final int cz)
	{
		int highest = -1;
		for (int x = cx - RADIUS; x <= cx + RADIUS; x++)
		{
			for (int z = cz - RADIUS; z <= cz + RADIUS; z++)
			{
				int y = 255;
				while (world.getBlockId(x, y, z) == 0 && y > highest)
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
}
