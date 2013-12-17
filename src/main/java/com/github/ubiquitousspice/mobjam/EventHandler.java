package com.github.ubiquitousspice.mobjam;

import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import org.lwjgl.opengl.GL11;

public class EventHandler
{

	@ForgeSubscribe
	public void spawnClones(EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntitySwarmZombie)
		{
			World world = event.entity.worldObj;
			if (world != null && !event.entity.getEntityData().getBoolean("hasSpawned"))//TODO: finish fixing
			{
				int day = (int) world.getTotalWorldTime() / 24000;
				int tospawn = (int) (20D / (1D + Math.pow(Math.E, (4D - day) / 2D))); //20/(1+e^((4-x)/2))

				while (tospawn > 0)
				{
					System.out.println(tospawn);
					Entity ent = new EntitySwarmZombie(world, false);
					ent.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, 0, 0);
					ent.getEntityData().setBoolean("hasSpawned", true);
					world.spawnEntityInWorld(ent);
					tospawn--;
				}
			}
			event.entity.getEntityData().setBoolean("hasSpawned", true);
		}
	}

	RenderItem renderItem = new RenderItem();
	ItemStack compassStack = new ItemStack(Item.compass);

	@ForgeSubscribe
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			renderItem.zLevel = 200.0F;
			renderItem.renderItemIntoGUI(null, Minecraft.getMinecraft().getTextureManager(), compassStack, 0, 0);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

	@ForgeSubscribe
	public void genSpawn(PopulateChunkEvent.Post event)
	{
		ChunkCoordinates spawn = event.world.getSpawnPoint();
		if (spawn.posX / 16 == event.chunkX && spawn.posZ / 16 == event.chunkZ)
		{
			final World world = event.world;
			final int cx = spawn.posX;
			final int cz = spawn.posZ;
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
