package com.github.ubiquitousspice.mobjam.blocks;

import com.github.ubiquitousspice.mobjam.MobJam;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ZombieBeacon extends BlockBeacon
{
	public ZombieBeacon(int par1)
	{
		super(par1);
		this.setCreativeTab(null);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8,
									float par9)
	{
		genFort(par1World, par2, getHighestBlock(par1World, par2, par4), par4);
		return true;
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
		world.setBlock(cx,highestBlock+1,cz, MobJam.zombieBeacon.blockID);
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

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new ZombieBeaconTE();
	}

	public static class ZombieBeaconTE extends TileEntityBeacon
	{
		public void updateEntity()
		{
			ObfuscationReflectionHelper.setPrivateValue(TileEntityBeacon.class, this, true, "isBeaconActive", "field_82135_d");
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ZombieBeaconTESR extends TileEntitySpecialRenderer
	{
		private static final ResourceLocation texture = new ResourceLocation("textures/entity/beacon_beam.png");

		private void render(ZombieBeaconTE entity, double x, double y, double z, float thing)
		{
			float brightness = 1f;//entity.getBrightness();

			// stolen from the Beacon code

			Tessellator tessellator = Tessellator.instance;
			this.bindTexture(texture);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			float f2 = (float) entity.getWorldObj().getTotalWorldTime() + thing;
			float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
			byte b0 = 1;
			double d3 = (double) f2 * 0.025D * (1.0D - (double) (b0 & 1) * 2.5D);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA(255, 255, 255, 32);
			double d4 = (double) b0 * 0.2D;
			double d5 = 0.5D + Math.cos(d3 + 2.356194490192345D) * d4;
			double d6 = 0.5D + Math.sin(d3 + 2.356194490192345D) * d4;
			double d7 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * d4;
			double d8 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * d4;
			double d9 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * d4;
			double d10 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * d4;
			double d11 = 0.5D + Math.cos(d3 + 5.497787143782138D) * d4;
			double d12 = 0.5D + Math.sin(d3 + 5.497787143782138D) * d4;
			double d13 = (double) (256.0F * brightness);
			double d14 = 0.0D;
			double d15 = 1.0D;
			double d16 = (double) (-1.0F + f3);
			double d17 = (double) (256.0F * brightness) * (0.5D / d4) + d16;
			tessellator.addVertexWithUV(x + d5, y + d13, z + d6, d15, d17);
			tessellator.addVertexWithUV(x + d5, y, z + d6, d15, d16);
			tessellator.addVertexWithUV(x + d7, y, z + d8, d14, d16);
			tessellator.addVertexWithUV(x + d7, y + d13, z + d8, d14, d17);
			tessellator.addVertexWithUV(x + d11, y + d13, z + d12, d15, d17);
			tessellator.addVertexWithUV(x + d11, y, z + d12, d15, d16);
			tessellator.addVertexWithUV(x + d9, y, z + d10, d14, d16);
			tessellator.addVertexWithUV(x + d9, y + d13, z + d10, d14, d17);
			tessellator.addVertexWithUV(x + d7, y + d13, z + d8, d15, d17);
			tessellator.addVertexWithUV(x + d7, y, z + d8, d15, d16);
			tessellator.addVertexWithUV(x + d11, y, z + d12, d14, d16);
			tessellator.addVertexWithUV(x + d11, y + d13, z + d12, d14, d17);
			tessellator.addVertexWithUV(x + d9, y + d13, z + d10, d15, d17);
			tessellator.addVertexWithUV(x + d9, y, z + d10, d15, d16);
			tessellator.addVertexWithUV(x + d5, y, z + d6, d14, d16);
			tessellator.addVertexWithUV(x + d5, y + d13, z + d6, d14, d17);
			tessellator.draw();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthMask(false);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA(255, 255, 255, 32);
			double d18 = 0.2D;
			double d19 = 0.2D;
			double d20 = 0.8D;
			double d21 = 0.2D;
			double d22 = 0.2D;
			double d23 = 0.8D;
			double d24 = 0.8D;
			double d25 = 0.8D;
			double d26 = (double) (256.0F * brightness);
			double d27 = 0.0D;
			double d28 = 1.0D;
			double d29 = (double) (-1.0F + f3);
			double d30 = (double) (256.0F * brightness) + d29;
			tessellator.addVertexWithUV(x + d18, y + d26, z + d19, d28, d30);
			tessellator.addVertexWithUV(x + d18, y, z + d19, d28, d29);
			tessellator.addVertexWithUV(x + d20, y, z + d21, d27, d29);
			tessellator.addVertexWithUV(x + d20, y + d26, z + d21, d27, d30);
			tessellator.addVertexWithUV(x + d24, y + d26, z + d25, d28, d30);
			tessellator.addVertexWithUV(x + d24, y, z + d25, d28, d29);
			tessellator.addVertexWithUV(x + d22, y, z + d23, d27, d29);
			tessellator.addVertexWithUV(x + d22, y + d26, z + d23, d27, d30);
			tessellator.addVertexWithUV(x + d20, y + d26, z + d21, d28, d30);
			tessellator.addVertexWithUV(x + d20, y, z + d21, d28, d29);
			tessellator.addVertexWithUV(x + d24, y, z + d25, d27, d29);
			tessellator.addVertexWithUV(x + d24, y + d26, z + d25, d27, d30);
			tessellator.addVertexWithUV(x + d22, y + d26, z + d23, d28, d30);
			tessellator.addVertexWithUV(x + d22, y, z + d23, d28, d29);
			tessellator.addVertexWithUV(x + d18, y, z + d19, d27, d29);
			tessellator.addVertexWithUV(x + d18, y + d26, z + d19, d27, d30);
			tessellator.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);
		}

		@Override
		public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
		{
			render((ZombieBeaconTE) tileentity, d0, d1, d2, f);
		}
	}
}
