package com.github.ubiquitousspice.mobjam;

import com.github.ubiquitousspice.mobjam.entities.EntitySwarmZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class EventHandler
{
	@ForgeSubscribe
	public void mobSpawnEvent(LivingSpawnEvent.CheckSpawn event)
	{
		Entity entity = event.entity;
		if ((entity instanceof EntityZombie) && !(entity instanceof EntitySwarmZombie))
		{
			Entity swarmZombie = EntitySwarmZombie.instanceHacked(entity.worldObj);
			swarmZombie.posX = entity.posX;
			swarmZombie.posY = entity.posY;
			swarmZombie.posZ = entity.posZ;
			event.setResult(Event.Result.DENY);
		}
	}
}
