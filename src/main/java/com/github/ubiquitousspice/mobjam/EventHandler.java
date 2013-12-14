package com.github.ubiquitousspice.mobjam;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class EventHandler
{
	@ForgeSubscribe
	public void mobSpawnEvent(LivingSpawnEvent event)
	{
		Entity entity = event.entity;
		if (entity instanceof EntityZombie)
		{
			event.setCanceled(true);
			//spawn our own
		}
	}
}
