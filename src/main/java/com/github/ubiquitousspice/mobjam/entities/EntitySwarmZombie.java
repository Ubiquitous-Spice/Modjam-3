package com.github.ubiquitousspice.mobjam.entities;

import com.github.ubiquitousspice.mobjam.navigation.EntityAISwarmSpawn;
import com.github.ubiquitousspice.mobjam.navigation.SwarmPathNavigate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDummyContainer;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class EntitySwarmZombie extends EntityMob
{
	protected static final Attribute field_110186_bp =
			(new RangedAttribute("zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).func_111117_a("Spawn Reinforcements Chance");
	private static final UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5D, 1);

	/**
	 * Ticker used to determine the time remaining for this zombie to convert into a villager when cured.
	 */
	private int conversionTime;

	protected SwarmPathNavigate navigator;

	public EntitySwarmZombie(World par1World)
	{
		this(par1World, true);
	}

	public EntitySwarmZombie(World world, boolean genClones)
	{
		super(world);
		navigator = new SwarmPathNavigate(this, world);
		this.getNavigator().setBreakDoors(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		//this.tasks.addTask(1, new EntityAIBreakBlocks(this));
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
		this.tasks.addTask(3, new EntityAISwarmSpawn(this));
		this.tasks.addTask(4, new EntityAIWander(this, 0.5D));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));//todo: target the beacon
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));

		if (genClones && world != null && false)// TODO: FIX
		{
			int day = (int) world.getTotalWorldTime() / 24000;
			int tospawn = 20;//(int) (20D / (1D + Math.pow(Math.E, (4D - day) / 2D))); //20/(1+e^((4-x)/2))

			while (tospawn > 0)
			{
				Entity ent = new EntitySwarmZombie(world, false);
				ent.setPosition(this.posX, this.posY, this.posZ);
				world.joinEntityInSurroundings(ent);
				tospawn--;
			}
		}
	}

	Vec3 lastpos = Vec3.createVectorHelper(0, 0, 0);
	int timeStopped = 0;
	List<int[]> activeBlocks = new LinkedList<int[]>();

	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();
		Vec3 pos = Vec3.createVectorHelper(posX, posY, posZ);
		if (pos.distanceTo(lastpos) < 0.05D)
		{
			timeStopped++;
		}
		else
		{
			timeStopped = 0;
			for (int[] coords : activeBlocks)
			{
				worldObj.destroyBlockInWorldPartially(entityId, coords[0], coords[1], coords[2], 0);
			}
			activeBlocks.clear();
		}
		if (timeStopped == 200)
		{

			for (int y : new int[] {0, 1})
			{
				for (int x : new int[] {-1, 1})
				{
					if (!worldObj.isAirBlock(MathHelper.floor_double(posX + x), MathHelper.floor_double(posY + y), MathHelper.floor_double(posZ)))
					{
						System.out.println(
								MathHelper.floor_double(posX + x) + " " + MathHelper.floor_double(posY + y) + " " + MathHelper.floor_double(posZ));
						activeBlocks
								.add(new int[] {MathHelper.floor_double(posX + x), MathHelper.floor_double(posY + y), MathHelper.floor_double(posZ)});
					}
				}
				for (int z : new int[] {-1, 1})
				{
					if (!worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY + y), MathHelper.floor_double(posZ + z)))
					{
						System.out.println(
								MathHelper.floor_double(posX) + " " + MathHelper.floor_double(posY + y) + " " + MathHelper.floor_double(posZ + z));
						activeBlocks
								.add(new int[] {MathHelper.floor_double(posX), MathHelper.floor_double(posY + y), MathHelper.floor_double(posZ + z)});
					}
				}
			}
		}
		else if (timeStopped > 200)
		{
			if (activeBlocks.isEmpty())
			{
				timeStopped = 0;
			}
			else
			{
				int breakingTime = timeStopped - 200;
				int i = (int) ((float) breakingTime / 240.0F * 10.0F);


				for (int[] coords : activeBlocks)
				{
					worldObj.destroyBlockInWorldPartially(entityId, coords[0], coords[1], coords[2], i);
				}


				if (breakingTime == 240)
				{
					for (int[] coords : activeBlocks)
					{
						worldObj.setBlockToAir(coords[0], coords[1], coords[2]/*, true*/);
						worldObj.playAuxSFX(1012, coords[0], coords[1], coords[2], 0);
						timeStopped = 0;
					}
					activeBlocks.clear();
				}
			}

		}
		lastpos = pos;
		/*for (int[] coords : activeBlocks)
		{
			System.out.println(coords[0] + " " + coords[1] + " " + coords[2]);
		}*/
//		System.out.println(timeStopped);
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0D);
		this.getAttributeMap().func_111150_b(field_110186_bp).setAttribute(this.rand.nextDouble() * ForgeDummyContainer.zombieSummonBaseChance);
	}

	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(12, Byte.valueOf((byte) 0));
		this.getDataWatcher().addObject(13, Byte.valueOf((byte) 0));
		this.getDataWatcher().addObject(14, Byte.valueOf((byte) 0));
	}

	/**
	 * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue()
	{
		int i = super.getTotalArmorValue() + 2;

		if (i > 20)
		{
			i = 20;
		}

		return i;
	}

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	protected boolean isAIEnabled()
	{
		return true;
	}

	/**
	 * If Animal, checks if the age timer is negative
	 */
	public boolean isChild()
	{
		return this.getDataWatcher().getWatchableObjectByte(12) == 1;
	}

	/**
	 * Set whether this zombie is a child.
	 */
	public void setChild(boolean par1)
	{
		this.getDataWatcher().updateObject(12, Byte.valueOf((byte) (par1 ? 1 : 0)));

		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			AttributeInstance attributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			attributeinstance.removeModifier(babySpeedBoostModifier);

			if (par1)
			{
				attributeinstance.applyModifier(babySpeedBoostModifier);
			}
		}
	}

	/**
	 * Return whether this zombie is a villager.
	 */
	public boolean isVillager()
	{
		return this.getDataWatcher().getWatchableObjectByte(13) == 1;
	}

	/**
	 * Set whether this zombie is a villager.
	 */
	public void setVillager(boolean par1)
	{
		this.getDataWatcher().updateObject(13, Byte.valueOf((byte) (par1 ? 1 : 0)));
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void onLivingUpdate()
	{
		if (this.worldObj.isDaytime() && !this.worldObj.isRemote && !this.isChild())
		{
			float f = this.getBrightness(1.0F);

			if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj
					.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)))
			{
				boolean flag = true;
				ItemStack itemstack = this.getCurrentItemOrArmor(4);

				if (itemstack != null)
				{
					if (itemstack.isItemStackDamageable())
					{
						itemstack.setItemDamage(itemstack.getItemDamageForDisplay() + this.rand.nextInt(2));

						if (itemstack.getItemDamageForDisplay() >= itemstack.getMaxDamage())
						{
							this.renderBrokenItemStack(itemstack);
							this.setCurrentItemOrArmor(4, null);
						}
					}

					flag = false;
				}

				if (flag)
				{
					this.setFire(8);
				}
			}
		}

		super.onLivingUpdate();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		if (!this.worldObj.isRemote && this.isConverting())
		{
			int i = this.getConversionTimeBoost();
			this.conversionTime -= i;

			if (this.conversionTime <= 0)
			{
				this.convertToVillager();
			}
		}

		super.onUpdate();
	}

	public boolean attackEntityAsMob(Entity par1Entity)
	{
		boolean flag = super.attackEntityAsMob(par1Entity);

		if (flag && this.getHeldItem() == null && this.isBurning() && this.rand.nextFloat() < (float) this.worldObj.difficultySetting * 0.3F)
		{
			par1Entity.setFire(2 * this.worldObj.difficultySetting);
		}

		return flag;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound()
	{
		return "mob.zombie.say";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound()
	{
		return "mob.zombie.hurt";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound()
	{
		return "mob.zombie.death";
	}

	/**
	 * Plays step sound at given x, y, z for the entity
	 */
	protected void playStepSound(int par1, int par2, int par3, int par4)
	{
		this.playSound("mob.zombie.step", 0.15F, 1.0F);
	}

	/**
	 * Returns the item ID for the item the mob drops on death.
	 */
	protected int getDropItemId()
	{
		return Item.rottenFlesh.itemID;
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.UNDEAD;
	}

	protected void dropRareDrop(int par1)
	{
		switch (this.rand.nextInt(3))
		{
			case 0:
				this.dropItem(Item.ingotIron.itemID, 1);
				break;
			case 1:
				this.dropItem(Item.carrot.itemID, 1);
				break;
			case 2:
				this.dropItem(Item.potato.itemID, 1);
		}
	}

	/**
	 * Makes entity wear random armor based on difficulty
	 */
	protected void addRandomArmor()
	{
		super.addRandomArmor();

		if (this.rand.nextFloat() < (this.worldObj.difficultySetting == 3 ? 0.05F : 0.01F))
		{
			int i = this.rand.nextInt(3);

			if (i == 0)
			{
				this.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
			}
			else
			{
				this.setCurrentItemOrArmor(0, new ItemStack(Item.shovelIron));
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);

		if (this.isChild())
		{
			par1NBTTagCompound.setBoolean("IsBaby", true);
		}

		if (this.isVillager())
		{
			par1NBTTagCompound.setBoolean("IsVillager", true);
		}

		par1NBTTagCompound.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.getBoolean("IsBaby"))
		{
			this.setChild(true);
		}

		if (par1NBTTagCompound.getBoolean("IsVillager"))
		{
			this.setVillager(true);
		}

		if (par1NBTTagCompound.hasKey("ConversionTime") && par1NBTTagCompound.getInteger("ConversionTime") > -1)
		{
			this.startConversion(par1NBTTagCompound.getInteger("ConversionTime"));
		}
	}

	/**
	 * This method gets called when the entity kills another one.
	 */
	public void onKillEntity(EntityLivingBase par1EntityLivingBase)
	{
		super.onKillEntity(par1EntityLivingBase);

		if (this.worldObj.difficultySetting >= 2 && par1EntityLivingBase instanceof EntityVillager)
		{
			if (this.worldObj.difficultySetting == 2 && this.rand.nextBoolean())
			{
				return;
			}

			EntityZombie entityzombie = new EntityZombie(this.worldObj);
			entityzombie.copyLocationAndAnglesFrom(par1EntityLivingBase);
			this.worldObj.removeEntity(par1EntityLivingBase);
			entityzombie.onSpawnWithEgg(null);
			entityzombie.setVillager(true);

			if (par1EntityLivingBase.isChild())
			{
				entityzombie.setChild(true);
			}

			this.worldObj.spawnEntityInWorld(entityzombie);
			this.worldObj.playAuxSFXAtEntity(null, 1016, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
		}
	}

	public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData)
	{
		Object par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
		float f = this.worldObj.getLocationTensionFactor(this.posX, this.posY, this.posZ);
		this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);

		this.addRandomArmor();
		this.enchantEquipment();

		if (this.getCurrentItemOrArmor(4) == null)
		{
			Calendar calendar = this.worldObj.getCurrentDate();

			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F)
			{
				this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Block.pumpkinLantern : Block.pumpkin));
				this.equipmentDropChances[4] = 0.0F;
			}
		}

		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
			.applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05000000074505806D, 0));
		this.getEntityAttribute(SharedMonsterAttributes.followRange)
			.applyModifier(new AttributeModifier("Random zombie-spawn bonus", this.rand.nextDouble() * 1.5D, 2));

		if (this.rand.nextFloat() < f * 0.05F)
		{
			this.getEntityAttribute(field_110186_bp)
				.applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25D + 0.5D, 0));
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0D + 1.0D, 2));
		}

		return (EntityLivingData) par1EntityLivingData1;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer par1EntityPlayer)
	{
		ItemStack itemstack = par1EntityPlayer.getCurrentEquippedItem();

		if (itemstack != null && itemstack.getItem() == Item.appleGold && itemstack.getItemDamage() == 0 && this.isVillager() && this.isPotionActive(
				Potion.weakness))
		{
			if (!par1EntityPlayer.capabilities.isCreativeMode)
			{
				--itemstack.stackSize;
			}

			if (itemstack.stackSize <= 0)
			{
				par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, null);
			}

			if (!this.worldObj.isRemote)
			{
				this.startConversion(this.rand.nextInt(2401) + 3600);
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Starts converting this zombie into a villager. The zombie converts into a villager after the specified time in
	 * ticks.
	 */
	protected void startConversion(int par1)
	{
		this.conversionTime = par1;
		this.getDataWatcher().updateObject(14, Byte.valueOf((byte) 1));
		this.removePotionEffect(Potion.weakness.id);
		this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, par1, Math.min(this.worldObj.difficultySetting - 1, 0)));
		this.worldObj.setEntityState(this, (byte) 16);
	}

	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte par1)
	{
		if (par1 == 16)
		{
			this.worldObj.playSound(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "mob.zombie.remedy", 1.0F + this.rand.nextFloat(),
									this.rand.nextFloat() * 0.7F + 0.3F, false);
		}
		else
		{
			super.handleHealthUpdate(par1);
		}
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	protected boolean canDespawn()
	{
		return !this.isConverting();
	}

	/**
	 * Returns whether this zombie is in the process of converting to a villager
	 */
	public boolean isConverting()
	{
		return this.getDataWatcher().getWatchableObjectByte(14) == 1;
	}

	/**
	 * Convert this zombie into a villager.
	 */
	protected void convertToVillager()
	{
		EntityVillager entityvillager = new EntityVillager(this.worldObj);
		entityvillager.copyLocationAndAnglesFrom(this);
		entityvillager.onSpawnWithEgg(null);
		entityvillager.func_82187_q();

		if (this.isChild())
		{
			entityvillager.setGrowingAge(-24000);
		}

		this.worldObj.removeEntity(this);
		this.worldObj.spawnEntityInWorld(entityvillager);
		entityvillager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
		this.worldObj.playAuxSFXAtEntity(null, 1017, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
	}

	/**
	 * Return the amount of time decremented from conversionTime every tick.
	 */
	protected int getConversionTimeBoost()
	{
		int i = 1;

		if (this.rand.nextFloat() < 0.01F)
		{
			int j = 0;

			for (int k = (int) this.posX - 4; k < (int) this.posX + 4 && j < 14; ++k)
			{
				for (int l = (int) this.posY - 4; l < (int) this.posY + 4 && j < 14; ++l)
				{
					for (int i1 = (int) this.posZ - 4; i1 < (int) this.posZ + 4 && j < 14; ++i1)
					{
						int j1 = this.worldObj.getBlockId(k, l, i1);

						if (j1 == Block.fenceIron.blockID || j1 == Block.bed.blockID)
						{
							if (this.rand.nextFloat() < 0.3F)
							{
								++i;
							}

							++j;
						}
					}
				}
			}
		}

		return i;
	}
}
