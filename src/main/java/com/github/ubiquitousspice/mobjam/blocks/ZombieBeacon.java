package com.github.ubiquitousspice.mobjam.blocks;

import com.github.ubiquitousspice.mobjam.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ZombieBeacon extends BlockContainer
{
	public ZombieBeacon(int par1)
	{
		super(par1, Material.iron);
		this.setCreativeTab(null);
		this.setLightValue(15f);
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public float getEnchantPowerBonus(World world, int x, int y, int z)
	{
		return 100f;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return side == ForgeDirection.DOWN;
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new ZombieBeaconTE();
	}

	public static class ZombieBeaconTE extends TileEntity
	{
		@SideOnly(Side.CLIENT)
		private long totalTime;
		@SideOnly(Side.CLIENT)
		private float changeTime;

		@Override
		public AxisAlignedBB getRenderBoundingBox()
		{
			return TileEntity.INFINITE_EXTENT_AABB;
		}

		@Override
		public double getMaxRenderDistanceSquared()
		{
			return Short.MAX_VALUE;
		}

		@Override
		public boolean canUpdate()
		{
			return false;
		}

		@SideOnly(Side.CLIENT)
		public float getBeamIntensity()
		{
			int i = (int) (this.worldObj.getTotalWorldTime() - this.totalTime);
			this.totalTime = this.worldObj.getTotalWorldTime();

			if (i > 1)
			{
				this.changeTime -= (float) i / 40.0F;

				if (this.changeTime < 0.0F)
				{
					this.changeTime = 0.0F;
				}
			}

			this.changeTime += 0.025F;

			if (this.changeTime > 1.0F)
			{
				this.changeTime = 1.0F;
			}

			return this.changeTime;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ZombieBeaconTESR extends TileEntitySpecialRenderer
	{
		private static final ResourceLocation beamTexture = new ResourceLocation("textures/entity/beacon_beam.png");
		private static final ResourceLocation texture = new ResourceLocation(Constants.MODID.toLowerCase(), "textures/beacon.png");
		//private static final ResourceLocation texture = new ResourceLocation("textures/entity/chest/christmas.png");
		ZombieBeaconModel model = new ZombieBeaconModel();

		private void renderBlock(ZombieBeaconTE entity, double x, double y, double z, float thing)
		{
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(0f, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			this.bindTexture(texture);
			model.renderAll();

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		private void renderBeam(ZombieBeaconTE entity, double x, double y, double z, float thing)
		{
			float intensity = entity.getBeamIntensity();

			// stolen from the Beacon code

			Tessellator tessellator = Tessellator.instance;
			this.bindTexture(beamTexture);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

			float f2 = (float) entity.getWorldObj().getTotalWorldTime() + thing;
			float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
			double d3 = f2 * 0.025D * (-1.5D);

			double d4 = 0.2D;
			double d5 = 0.5D + Math.cos(d3 + 2.356194490192345D) * d4;
			double d6 = 0.5D + Math.sin(d3 + 2.356194490192345D) * d4;
			double d7 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * d4;
			double d8 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * d4;
			double d9 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * d4;
			double d10 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * d4;
			double d11 = 0.5D + Math.cos(d3 + 5.497787143782138D) * d4;
			double d12 = 0.5D + Math.sin(d3 + 5.497787143782138D) * d4;
			double d13 = (double) (256.0F * intensity);
			double d14 = 0.0D;
			double d15 = 1.0D;
			double d16 = (double) (-1.0F + f3);
			double d17 = (double) (256.0F * intensity) * (0.5D / d4) + d16;

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA(255, 255, 255, 32);
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

			double d18 = 0.2D;
			double d19 = 0.2D;
			double d20 = 0.8D;
			double d21 = 0.2D;
			double d22 = 0.2D;
			double d23 = 0.8D;
			double d24 = 0.8D;
			double d25 = 0.8D;
			double d26 = (double) (256.0F * intensity);
			double d27 = 0.0D;
			double d28 = 1.0D;
			double d29 = (double) (-1.0F + f3);
			double d30 = (double) (256.0F * intensity) + d29;

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA(255, 255, 255, 32);
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
			renderBlock((ZombieBeaconTE) tileentity, d0, d1, d2, f);
			renderBeam((ZombieBeaconTE) tileentity, d0, d1, d2, f);
		}
	}

	private static class ZombieBeaconModel extends ModelBase
	{
		public ModelRenderer base;


		public ZombieBeaconModel()
		{
//			this.chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
//			this.chestLid.rotationPointX = 1.0F;
//			this.chestLid.rotationPointY = 7.0F;
//			this.chestLid.rotationPointZ = 15.0F;
//			this.chestKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
//			this.chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
//			this.chestKnob.rotationPointX = 8.0F;
//			this.chestKnob.rotationPointY = 7.0F;
//			this.chestKnob.rotationPointZ = 15.0F;
			this.base = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
			this.base.addBox(0.0F, 0.0F, 0.0F, 14, 5, 14, 0.0F);
			this.base.rotationPointX = 1.0F;
			this.base.rotationPointY = 11.0F;
			this.base.rotationPointZ = 1.0F;
		}

		/**
		 * This method renders out all parts of the chest model.
		 */
		public void renderAll()
		{
			base.render(0.0625F);
		}
	}
}
