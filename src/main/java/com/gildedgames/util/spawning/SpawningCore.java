package com.gildedgames.util.spawning;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.gildedgames.util.core.ICore;
import com.gildedgames.util.player.PlayerCore;
import com.gildedgames.util.player.common.IPlayerHookPool;
import com.gildedgames.util.player.common.PlayerHookPool;
import com.gildedgames.util.spawning.player.PlayerSpawning;
import com.gildedgames.util.spawning.player.PlayerSpawningFactory;
import com.gildedgames.util.spawning.util.DefaultSpawnSettings;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class SpawningCore implements ICore
{
	private static List<SpawnManager> spawnManagers = new ArrayList<SpawnManager>();

	private IPlayerHookPool<PlayerSpawning> players = new PlayerHookPool<PlayerSpawning>("spawning", new PlayerSpawningFactory(), Side.SERVER);

	private IPlayerHookPool<PlayerSpawning> stub = new PlayerHookPool<PlayerSpawning>("spawning", new PlayerSpawningFactory(), Side.CLIENT);

	public static SpawnManager createAndRegisterSpawnManager(int dimensionId)
	{
		return createAndRegisterSpawnManager(dimensionId, new DefaultSpawnSettings());
	}

	public static SpawnManager createAndRegisterSpawnManager(int dimensionId, SpawnSettings settings)
	{
		SpawnManager old = getSpawnManagerFor(dimensionId);
		if (old != null)
		{
			throw new IllegalStateException("Only one manager can exist per dimension. Please register to the existing manager instead.");
		}
		SpawnManager s = new SpawnManager(dimensionId, settings);
		spawnManagers.add(s);
		return s;
	}

	public static SpawnManager getSpawnManagerFor(int dimensionId)
	{
		for (SpawnManager spawnManager : spawnManagers)
		{
			if (spawnManager.getDimensionId() == dimensionId)
			{
				return spawnManager;
			}
		}
		return null;
	}

	@SubscribeEvent
	public void onTick(WorldTickEvent event)
	{
		if (event.phase == Phase.END)
		{
			World world = event.world;
			if (!world.isRemote)
			{
				SpawnManager spawnManager = getSpawnManagerFor(world.provider.dimensionId);
				if (spawnManager != null)
				{
					spawnManager.tickSpawning(world, this.players.getPlayerHooks());//It's kinda ewwy how it uses GroupCore here admittedly
				}
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		PlayerCore.INSTANCE.registerPlayerPool(this.stub, this.players);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serverAboutToStart(FMLServerAboutToStartEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopping(FMLServerStoppingEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopped(FMLServerStoppedEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStarting(FMLServerStartingEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStarted(FMLServerStartedEvent event)
	{
		// TODO Auto-generated method stub

	}

}