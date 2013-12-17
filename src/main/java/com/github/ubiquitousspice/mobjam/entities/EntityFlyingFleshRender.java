package com.github.ubiquitousspice.mobjam.entities;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Random;

public class EntityFlyingFleshRender extends Render
{
	private Random random = new Random();

	public EntityFlyingFleshRender()
	{
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	public void render(EntityFlyingFlesh entity, double x, double y, double z, float f, float f1)
	{
		this.bindEntityTexture(entity);
		this.random.setSeed(187L);
		Item item = Item.rottenFlesh;

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float f4;
		float f5;

		float f8;


		Icon icon1 = item.getIconFromDamage(0);

		int l = item.getColorFromItemStack(null, 0);
		f8 = (float) (l >> 16 & 255) / 255.0F;
		float f9 = (float) (l >> 8 & 255) / 255.0F;
		f5 = (float) (l & 255) / 255.0F;
		f4 = 1.0F;
		renderDroppedItem(item, icon1, 1, f1, f8 * f4, f9 * f4, f5 * f4, 0);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void renderDroppedItem(Item item, Icon icon, int par3, float par4, float par5, float par6, float par7, int pass)
	{
		Tessellator tessellator = Tessellator.instance;

		float f4 = icon.getMinU();
		float f5 = icon.getMaxU();
		float f6 = icon.getMinV();
		float f7 = icon.getMaxV();
		float f8 = 1.0F;
		float f9 = 0.5F;
		float f10 = 0.25F;
		float f11;

		if (this.renderManager.options.fancyGraphics)
		{
			GL11.glPushMatrix();

			float f12 = 0.0625F;
			f11 = 0.021875F;
			byte b0 = 1;

			GL11.glTranslatef(-f9, -f10, -((f12 + f11) * (float) b0 / 2.0F));

			for (int k = 0; k < b0; ++k)
			{
				// Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
				if (k > 0)
				{
					float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					GL11.glTranslatef(x, y, f12 + f11);
				}
				else
				{
					GL11.glTranslatef(0f, 0f, f12 + f11);
				}

				this.bindTexture(TextureMap.locationItemsTexture);

				GL11.glColor4f(par5, par6, par7, 1.0F);
				ItemRenderer.renderItemIn2D(tessellator, f5, f6, f4, f7, icon.getIconWidth(), icon.getIconHeight(), f12);
			}

			GL11.glPopMatrix();
		}
		else
		{
			for (int l = 0; l < par3; ++l)
			{
				GL11.glPushMatrix();

				if (l > 0)
				{
					f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float f16 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float f17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					GL11.glTranslatef(f11, f16, f17);
				}

				GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);

				GL11.glColor4f(par5, par6, par7, 1.0F);
				tessellator.startDrawingQuads();
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV((double) (0.0F - f9), (double) (0.0F - f10), 0.0D, (double) f4, (double) f7);
				tessellator.addVertexWithUV((double) (f8 - f9), (double) (0.0F - f10), 0.0D, (double) f5, (double) f7);
				tessellator.addVertexWithUV((double) (f8 - f9), (double) (1.0F - f10), 0.0D, (double) f5, (double) f6);
				tessellator.addVertexWithUV((double) (0.0F - f9), (double) (1.0F - f10), 0.0D, (double) f4, (double) f6);
				tessellator.draw();
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
	{
		render((EntityFlyingFlesh) entity, d0, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return this.renderManager.renderEngine.getResourceLocation(new ItemStack(Item.rottenFlesh).getItemSpriteNumber());
	}
}
