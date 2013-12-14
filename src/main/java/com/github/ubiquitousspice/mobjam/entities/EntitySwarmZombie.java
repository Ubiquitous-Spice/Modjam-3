package com.github.ubiquitousspice.mobjam.entities;

import com.github.ubiquitousspice.mobjam.navigation.SwarmPathNavigate;
import com.google.common.base.Throwables;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class EntitySwarmZombie extends EntityZombie
{
	private static final Field getHackField()
	{
		try
		{
			Field f = PathNavigate.class.getDeclaredFields()[0];
			f.setAccessible(true);
			return f;
		}
		catch (Throwable t)
		{
			Throwables.propagate(t);
		}
		return null;
	}

	private static Field superHackery = getHackField();

	protected SwarmPathNavigate navigate;

	protected static SwarmPathNavigate hackyHackery;

	private EntitySwarmZombie(World par1World)
	{
		super(par1World);
	}

	@Override
	public PathNavigate getNavigator()
	{
		if (navigate == null)
		{
			navigate = hackyHackery;
		}
		return navigate;
	}

	private static void setHackyHackery(World world)
	{
		hackyHackery = new SwarmPathNavigate(new EntityLiving(world)
		{
		}, world);
	}

	private static void finishHackery(EntityLiving ent)
	{
		try
		{
			superHackery.set(hackyHackery, ent);
			hackyHackery = null;
		}
		catch (Throwable t)
		{
			Throwables.propagate(t);
		}
	}

	public static EntitySwarmZombie instanceHacked(World world)
	{
		setHackyHackery(world);
		EntitySwarmZombie ent = new EntitySwarmZombie(world);
		finishHackery(ent);
		return ent;
	}
}
