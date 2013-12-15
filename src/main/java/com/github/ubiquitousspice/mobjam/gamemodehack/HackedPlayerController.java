package com.github.ubiquitousspice.mobjam.gamemodehack;

import com.github.ubiquitousspice.mobjam.MobJam;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumGameType;

@SideOnly(Side.CLIENT)
public class HackedPlayerController extends PlayerControllerMP
{

	public HackedPlayerController(Minecraft par1Minecraft, NetClientHandler par2NetClientHandler)
	{
		super(par1Minecraft, par2NetClientHandler);
	}

	@Override
	public void setGameType(EnumGameType type)
	{
		super.setGameType(type);
		setPlayerCapabilities(Minecraft.getMinecraft().thePlayer);
	}

	public boolean shouldDrawHUD()
	{
		boolean shouldDraw = super.shouldDrawHUD();

		if (!shouldDraw)
		{
			EnumGameType gamemode = getGameMode();
			shouldDraw = gamemode == MobJam.GAMEMODE;
		}

		return shouldDraw;
	}

	@Override
	public void setPlayerCapabilities(EntityPlayer player)
	{
		EnumGameType type = getGameMode();
		type.configurePlayerCapabilities(player.capabilities);
//		if (type == MobJam.GAMEMODE && ObfuscationReflectionHelper/)
//		{
//			player.capabilities.allowFlying = true;
//		}
	}

	private EnumGameType getGameMode()
	{
		return ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, this, "currentGameType", "field_78779_k");
	}

}
