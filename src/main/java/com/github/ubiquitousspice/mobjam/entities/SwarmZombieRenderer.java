package com.github.ubiquitousspice.mobjam.entities;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SwarmZombieRenderer extends RenderBiped
{
	private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
	private static final ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
	private ModelBiped field_82434_o;
	private ModelZombieVillager zombieVillagerModel;
	protected ModelBiped field_82437_k;
	protected ModelBiped field_82435_l;
	protected ModelBiped field_82436_m;
	protected ModelBiped field_82433_n;
	private int field_82431_q = 1;

	public SwarmZombieRenderer()
	{
		super(new ModelZombie(), 0.5F, 1.0F);
		this.field_82434_o = this.modelBipedMain;
		this.zombieVillagerModel = new ModelZombieVillager();
	}

	protected void func_82421_b()
	{
		this.field_82423_g = new ModelZombie(1.0F, true);
		this.field_82425_h = new ModelZombie(0.5F, true);
		this.field_82437_k = this.field_82423_g;
		this.field_82435_l = this.field_82425_h;
		this.field_82436_m = new ModelZombieVillager(1.0F, 0.0F, true);
		this.field_82433_n = new ModelZombieVillager(0.5F, 0.0F, true);
	}

	protected int func_82429_a(EntitySwarmZombie par1EntitySwarmZombie, int par2, float par3)
	{
		this.func_82427_a(par1EntitySwarmZombie);
		return super.func_130006_a(par1EntitySwarmZombie, par2, par3);
	}

	public void func_82426_a(EntitySwarmZombie par1EntitySwarmZombie, double par2, double par4, double par6, float par8, float par9)
	{
		this.func_82427_a(par1EntitySwarmZombie);
		super.doRenderLiving(par1EntitySwarmZombie, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation func_110863_a(EntitySwarmZombie par1EntitySwarmZombie)
	{
		return (par1EntitySwarmZombie.isVillager() ? zombieVillagerTextures : zombieTextures);
	}

	protected void func_82428_a(EntitySwarmZombie par1EntitySwarmZombie, float par2)
	{
		this.func_82427_a(par1EntitySwarmZombie);
		super.func_130005_c(par1EntitySwarmZombie, par2);
	}

	private void func_82427_a(EntitySwarmZombie par1EntitySwarmZombie)
	{
		if (par1EntitySwarmZombie.isVillager())
		{
			if (this.field_82431_q != this.zombieVillagerModel.func_82897_a())
			{
				this.zombieVillagerModel = new ModelZombieVillager();
				this.field_82431_q = this.zombieVillagerModel.func_82897_a();
				this.field_82436_m = new ModelZombieVillager(1.0F, 0.0F, true);
				this.field_82433_n = new ModelZombieVillager(0.5F, 0.0F, true);
			}

			this.mainModel = this.zombieVillagerModel;
			this.field_82423_g = this.field_82436_m;
			this.field_82425_h = this.field_82433_n;
		}
		else
		{
			this.mainModel = this.field_82434_o;
			this.field_82423_g = this.field_82437_k;
			this.field_82425_h = this.field_82435_l;
		}

		this.modelBipedMain = (ModelBiped)this.mainModel;
	}

	protected void func_82430_a(EntitySwarmZombie par1EntitySwarmZombie, float par2, float par3, float par4)
	{
		if (par1EntitySwarmZombie.isConverting())
		{
			par3 += (float)(Math.cos((double)par1EntitySwarmZombie.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(par1EntitySwarmZombie, par2, par3, par4);
	}

	protected void func_130005_c(EntityLiving par1EntityLiving, float par2)
	{
		this.func_82428_a((EntitySwarmZombie)par1EntityLiving, par2);
	}

	protected ResourceLocation func_110856_a(EntityLiving par1EntityLiving)
	{
		return this.func_110863_a((EntitySwarmZombie)par1EntityLiving);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
	{
		this.func_82426_a((EntitySwarmZombie)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	protected int func_130006_a(EntityLiving par1EntityLiving, int par2, float par3)
	{
		return this.func_82429_a((EntitySwarmZombie)par1EntityLiving, par2, par3);
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
	{
		return this.func_82429_a((EntitySwarmZombie)par1EntityLivingBase, par2, par3);
	}

	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2)
	{
		this.func_82428_a((EntitySwarmZombie)par1EntityLivingBase, par2);
	}

	protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
	{
		this.func_82430_a((EntitySwarmZombie)par1EntityLivingBase, par2, par3, par4);
	}

	public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
	{
		this.func_82426_a((EntitySwarmZombie)par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return this.func_110863_a((EntitySwarmZombie)par1Entity);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.func_82426_a((EntitySwarmZombie)par1Entity, par2, par4, par6, par8, par9);
	}
}
