package com.github.ubiquitousspice.mobjam.blocks;

import com.github.ubiquitousspice.mobjam.Constants;
import com.github.ubiquitousspice.mobjam.MobJam;
import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ZombieBeacon extends BlockContainer
{
	public static class PillarBrick extends BlockStoneBrick
	{
		public PillarBrick(int par1)
		{
			super(par1);
			this.setBlockUnbreakable();
			this.setResistance(6000000.0F);
		}

		@Override
		public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
		{
			if (!par1World.isRemote)
			{
				if (par5Entity instanceof EntitySwarmZombie)
				{
					par1World.setBlockToAir(par2, par3, par4);
					par1World.setBlockToAir(par2, par3 + 1, par4);
					par1World.createExplosion(par5Entity, par2, par3, par4, 50, true);

					// game has ended
					if (FMLCommonHandler.instance().getSidedDelegate().getServer().isSinglePlayer())
					{
						Minecraft.getMinecraft().thePlayer.attackEntityFrom(null, 10000f);
					}
					else
					{
						MinecraftServer.getServer().initiateShutdown();
					}
				}
			}
		}

		@Override
		public void onEntityWalking(World world, int x, int y, int z, Entity entity)
		{
			if (!world.isRemote)
			{
				if (entity instanceof EntitySwarmZombie)
				{
					world.setBlockToAir(x, y, z);
					world.setBlockToAir(x, y + 1, z);
					world.createExplosion(entity, x, y, z, 50, true);

					// game has ended
					if (FMLCommonHandler.instance().getSidedDelegate().getServer().isSinglePlayer())
					{
						Minecraft.getMinecraft().thePlayer.attackEntityFrom(null, 10000f);
					}
					else
					{
						MinecraftServer.getServer().initiateShutdown();
					}
				}
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		onEntityWalking(par1World, par2, par3, par4, par5Entity);
	}

	public ZombieBeacon(int par1)
	{
		super(par1, Material.iron);
		this.setCreativeTab(null);
		this.setLightValue(15f);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);

		float f = .0652f;
		this.setBlockBounds(f, 0, f, 1 - f, .5f, 1 - f);
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

		@Override
		public void validate()
		{
			MobJam.BEACONS.put(this.worldObj.provider.dimensionId, new ChunkPosition(this.xCoord, this.yCoord, this.zCoord));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ZombieBeaconTESR extends TileEntitySpecialRenderer
	{
		private static final ResourceLocation beamTexture = new ResourceLocation("textures/entity/beacon_beam.png");
		private static final ResourceLocation texture = new ResourceLocation(Constants.MODID.toLowerCase(), "textures/beacon.png");
		//private static final ResourceLocation texture = new ResourceLocation("textures/entity/chest/christmas.png");
		ZombieBeaconModel model = new ZombieBeaconModel();
		float[] colors = new float[] {0f, 0f, 0f};
		int multiplier = 1;
		float min = 0;

		private void updateColors(ZombieBeaconTE entity, float partial)
		{
			if (entity.getWorldObj().getTotalWorldTime() % 24000 >= 12000)
			{
				min = .5f;
			}
			else
			{
				min = 0;
			}

			float timeChange = (float) entity.getWorldObj().getTotalWorldTime() + partial;

			// colors
			if (isAllTop())
			{
				multiplier = -1;
			}
			else if (isAllBottom())
			{
				multiplier = 1;
			}

			for (int i = 0; i < colors.length; i++)
			{
				if (multiplier == 1 && colors[i] >= 1f)
				{
					continue;
				}
				else if ((multiplier == -1 && colors[i] <= min))
				{
					continue;
				}

				colors[i] += timeChange * (1d / 255d) * multiplier * 0.001;
				break;
			}
		}

		boolean isAllTop()
		{
			return colors[0] >= 1f && colors[1] >= 1f && colors[2] >= 1f;
		}

		boolean isAllBottom()
		{
			return colors[0] <= min && colors[1] <= min && colors[2] <= min;
		}


		private void renderBlock(ZombieBeaconTE entity, double x, double y, double z, float partial)
		{
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);

			this.bindTexture(texture);

			float timeChange = (float) entity.getWorldObj().getTotalWorldTime() + partial;
			float offset = (float) Math.sin(Math.PI / 46 * timeChange) * 0.05f;

			GL11.glColor3f(colors[0], colors[1], colors[2]);

			// rotations
			model.floater.rotateAngleZ = (float) Math.PI / 32 * timeChange;
			model.floater.rotateAngleY = (float) Math.PI / 32 * timeChange;
			model.floater.rotateAngleX = (float) Math.PI / 128 * timeChange;

			// render floater
			model.floater.offsetY += offset;
			model.floater.render(0.0625f);
			model.floater.offsetY -= offset;

			// base
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			model.base.render(0.0625f);

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		private void renderBeam(ZombieBeaconTE entity, double x, double y, double z, float partial)
		{
			float intensity = entity.getBeamIntensity();

			// offset y a bit higher...
			int opacity = 32;


			// stolen from the Beacon code

			Tessellator tessellator = Tessellator.instance;
			this.bindTexture(beamTexture);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glColor3f(colors[0], colors[1], colors[2]);

			float f2 = (float) entity.getWorldObj().getTotalWorldTime() + partial;
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
			tessellator.setColorRGBA((int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255), opacity);
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

			//GL11.glEnable(GL11.GL_BLEND);
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
			tessellator.setColorRGBA((int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255), opacity);
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

			GL11.glColor3f(1, 1, 1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);
		}

		@Override
		public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
		{
			updateColors((ZombieBeaconTE) tileentity, f);
			renderBlock((ZombieBeaconTE) tileentity, d0, d1, d2, f);
			renderBeam((ZombieBeaconTE) tileentity, d0, d1, d2, f);
		}
	}

	private static class ZombieBeaconModel extends ModelBase
	{
		public ModelRenderer base;
		public ModelRenderer floater;

		public ZombieBeaconModel()
		{
			floater = new ModelRenderer(this, 0, 0);
			floater.setTextureSize(64, 64);
			floater.addBox(-1.5F, -1.5F, -1.5F, 1, 1, 1, 0f);
			floater.addBox(-1.5F, -1.5F, .5F, 1, 1, 1, 0f);
			floater.addBox(-1.5F, .5F, -1.5F, 1, 1, 1, 0f);
			floater.addBox(-1.5F, .5F, .5F, 1, 1, 1, 0f);
			floater.addBox(.5F, -1.5F, -1.5F, 1, 1, 1, 0f);
			floater.addBox(.5F, -1.5F, .5F, 1, 1, 1, 0f);
			floater.addBox(.5F, .5F, -1.5F, 1, 1, 1, 0f);
			floater.addBox(.5F, .5F, .5F, 1, 1, 1, 0f);
			floater.offsetX = .5f;
			floater.offsetY = .4f;
			floater.offsetZ = .5f;

			base = new ModelRenderer(this, 0, 5);
			base.setTextureSize(64, 64);
			base.addBox(0.0F, 0.0F, 0.0F, 14, 5, 14, 0.0F);
			base.rotationPointX = 1.0F;
			base.rotationPointY = 11.0F;
			base.rotationPointZ = 1.0F;
		}
	}
}
