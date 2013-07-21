package me.daddychurchill.CityWorld.Plats.SnowDunes;

import org.bukkit.Material;

import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Plats.RoadLot;
import me.daddychurchill.CityWorld.Support.PlatMap;

public class SnowDunesRoadLot extends RoadLot {

	public SnowDunesRoadLot(PlatMap platmap, int chunkX, int chunkZ,
			long globalconnectionkey, boolean roundaboutPart) {
		super(platmap, chunkX, chunkZ, globalconnectionkey, roundaboutPart);
		// TODO Auto-generated constructor stub
	}


	@Override
	public PlatLot newLike(PlatMap platmap, int chunkX, int chunkZ) {
		return new SnowDunesRoadLot(platmap, chunkX, chunkZ, connectedkey, roundaboutRoad);
	}
	
	@Override
	protected byte getSidewalkId() {
		return (byte) Material.DOUBLE_STEP.getId();
	}
	
	@Override
	protected int getSidewalkLevel(WorldGenerator generator) {
		return generator.streetLevel;
	}
}
