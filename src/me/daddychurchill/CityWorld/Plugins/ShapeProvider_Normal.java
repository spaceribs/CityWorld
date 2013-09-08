package me.daddychurchill.CityWorld.Plugins;

import me.daddychurchill.CityWorld.Plugins.AthmosCaves.NodeCaves;
import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Context.ConstructionContext;
import me.daddychurchill.CityWorld.Context.DataContext;
import me.daddychurchill.CityWorld.Context.FarmContext;
import me.daddychurchill.CityWorld.Context.HighriseContext;
import me.daddychurchill.CityWorld.Context.IndustrialContext;
import me.daddychurchill.CityWorld.Context.LowriseContext;
import me.daddychurchill.CityWorld.Context.MidriseContext;
import me.daddychurchill.CityWorld.Context.MunicipalContext;
import me.daddychurchill.CityWorld.Context.NatureContext;
import me.daddychurchill.CityWorld.Context.NeighborhoodContext;
import me.daddychurchill.CityWorld.Context.ParkContext;
import me.daddychurchill.CityWorld.Context.RoadContext;
import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Plats.PlatLot.LotStyle;
import me.daddychurchill.CityWorld.Support.ByteChunk;
import me.daddychurchill.CityWorld.Support.CachedYs;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.PlatMap;
import me.daddychurchill.CityWorld.Support.RealChunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class ShapeProvider_Normal extends ShapeProvider {

	public DataContext parkContext;
	public DataContext highriseContext;
	public DataContext constructionContext;
	public DataContext midriseContext;
	public DataContext municipalContext;
	public DataContext industrialContext;
	public DataContext lowriseContext;
	public DataContext neighborhoodContext;
	public DataContext farmContext;
	
	public SimplexOctaveGenerator landShape1;
	public SimplexOctaveGenerator landShape2;
	public SimplexOctaveGenerator seaShape;
	public SimplexOctaveGenerator noiseShape;
	public SimplexOctaveGenerator featureShape;
	public SimplexNoiseGenerator caveShape;
	public SimplexNoiseGenerator mineShape;

	protected int height;
	protected int seaLevel;
	protected int landRange;
	protected int seaRange;
	protected int constructMin;
	protected int constructRange;
	
	public final static int landFlattening = 32;
	public final static int seaFlattening = 4;
	public final static int landFactor1to2 = 3;
	public final static int noiseVerticalScale = 3;
	public final static int featureVerticalScale = 10;
	public final static int fudgeVerticalScale = noiseVerticalScale * landFactor1to2 + featureVerticalScale * landFactor1to2;

	public final static double landFrequency1 = 1.50;
	public final static double landAmplitude1 = 20.0;
	public final static double landHorizontalScale1 = 1.0 / 2048.0;
	public final static double landFrequency2 = 1.0;
	public final static double landAmplitude2 = landAmplitude1 / landFactor1to2;
	public final static double landHorizontalScale2 = landHorizontalScale1 * landFactor1to2;

	public final static double seaFrequency = 1.00;
	public final static double seaAmplitude = 2.00;
	public final static double seaHorizontalScale = 1.0 / 384.0;

	public final static double noiseFrequency = 1.50;
	public final static double noiseAmplitude = 0.70;
	public final static double noiseHorizontalScale = 1.0 / 32.0;
	
	public final static double featureFrequency = 1.50;
	public final static double featureAmplitude = 0.75;
	public final static double featureHorizontalScale = 1.0 / 64.0;
	
	public final static double caveScale = 1.0 / 128.0;
	public final static double caveScaleY = caveScale * 2;
	public final static double caveThreshold = 0.05; // smaller the number the more larger the caves will be
	
	public final static double mineScale = 1.0 / 4.0;
	public final static double mineScaleY = mineScale;

	public ShapeProvider_Normal(WorldGenerator generator, Odds odds) {
		super(generator, odds);
		World world = generator.getWorld();
		long seed = generator.getWorldSeed();
		
		landShape1 = new SimplexOctaveGenerator(seed, 4);
		landShape1.setScale(landHorizontalScale1);
		landShape2 = new SimplexOctaveGenerator(seed, 6);
		landShape2.setScale(landHorizontalScale2);
		seaShape = new SimplexOctaveGenerator(seed + 2, 8);
		seaShape.setScale(seaHorizontalScale);
		noiseShape = new SimplexOctaveGenerator(seed + 3, 16);
		noiseShape.setScale(noiseHorizontalScale);
		featureShape = new SimplexOctaveGenerator(seed + 4, 2);
		featureShape.setScale(featureHorizontalScale);
		
		caveShape = new SimplexNoiseGenerator(seed);
		mineShape = new SimplexNoiseGenerator(seed + 1);
		
		// get ranges
		height = world.getMaxHeight();
		seaLevel = world.getSeaLevel();
		landRange = height - seaLevel - fudgeVerticalScale + landFlattening;
		seaRange = seaLevel - fudgeVerticalScale + seaFlattening;
		constructMin = seaLevel;
		constructRange = height - constructMin;
	}
	
	@Override
	public void populateLots(WorldGenerator generator, PlatMap platmap) {
		try {
			allocateContexts(generator);

			// assume everything is natural for the moment
			platmap.context = natureContext;
			natureContext.populateMap(generator, platmap);
			natureContext.validateMap(generator, platmap);
			
			// place and validate the roads
			if (generator.settings.includeRoads) {
				platmap.populateRoads();
				platmap.validateRoads();
	
				// place the buildings
				if (generator.settings.includeBuildings) {
		
					// recalculate the context based on the "natural-ness" of the platmap
					platmap.context = getContext(platmap);
					platmap.context.populateMap(generator, platmap);
					platmap.context.validateMap(generator, platmap);
				}
				
				// one last check
				validateLots(generator, platmap);
			}
		} catch (Exception e) {
			generator.reportException("NormalMap.populateLots FAILED", e);

		} 
	}
	
	@Override
	protected void validateLots(WorldGenerator generator, PlatMap platmap) {
		// nothing to do in this one
	}
	
	@Override
	protected void allocateContexts(WorldGenerator generator) {
		if (!contextInitialized) {
			natureContext = new NatureContext(generator);
			roadContext = new RoadContext(generator);
			
			parkContext = new ParkContext(generator);
			highriseContext = new HighriseContext(generator);
			constructionContext = new ConstructionContext(generator);
			midriseContext = new MidriseContext(generator);
			municipalContext = new MunicipalContext(generator);
			industrialContext = new IndustrialContext(generator);
			lowriseContext = new LowriseContext(generator);
			neighborhoodContext = new NeighborhoodContext(generator);
			farmContext = new FarmContext(generator);
			
			contextInitialized = true;
		}
	}
	
	private final static double oddsOfCentralPark = DataContext.oddsUnlikely;
	protected DataContext getContext(PlatMap platmap) {
		
		// how natural is this platmap?
		float nature = platmap.getNaturePercent();
		if (nature < 0.7) {
			if (platmap.getOddsGenerator().playOdds(oddsOfCentralPark))
				return parkContext;
			else
				return highriseContext;
		}
		else if (nature < 0.15)
			return constructionContext;
		else if (nature < 0.25)
			return midriseContext;
		else if (nature < 0.37)
			return municipalContext;
		else if (nature < 0.50)
			return industrialContext;
		else if (nature < 0.65)
			return lowriseContext;
		else if (nature < 0.75)
			return neighborhoodContext;
		else if (nature < 0.90 && platmap.generator.settings.includeFarms)
			return farmContext;
		else if (nature < 1.0)
			return neighborhoodContext;
		
		// otherwise just keep what we have
		else
			return natureContext;
	}

	@Override
	public String getCollectionName() {
		return "Normal";
	}
	
	@Override
	protected Biome remapBiome(WorldGenerator generator, PlatLot lot, Biome biome) {
		return generator.oreProvider.remapBiome(biome);
	}

	@Override
	public void preGenerateChunk(WorldGenerator generator, PlatLot lot, ByteChunk chunk, BiomeGrid biomes, CachedYs blockYs) {
		Biome biome = lot.getChunkBiome();
		OreProvider ores = generator.oreProvider;
		boolean surfaceCaves = isSurfaceCaveAt(chunk.chunkX, chunk.chunkZ);
		
		// shape the world
		for (int x = 0; x < chunk.width; x++) {
			for (int z = 0; z < chunk.width; z++) {
				int y = blockYs.getBlockY(x, z);
				
				// buildable?
				if (lot.style == LotStyle.STRUCTURE || lot.style == LotStyle.ROUNDABOUT) {
					generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, generator.streetLevel - 2, ores.subsurfaceId, generator.streetLevel, ores.subsurfaceId, false);
					
				// possibly buildable?
				} else if (y == generator.streetLevel) {
					generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 3, ores.subsurfaceId, y, ores.surfaceId, generator.settings.includeDecayedNature);
				
				// won't likely have a building
				} else {

					// on the beach
					if (y == generator.seaLevel) {
						generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 2, ores.fluidSubsurfaceId, y, ores.fluidSurfaceId, generator.settings.includeDecayedNature);
						biome = Biome.BEACH;

					// we are in the water! ...or are we?
					} else if (y < generator.seaLevel) {
						biome = Biome.DESERT;
						if (generator.settings.includeDecayedNature)
							if (generator.settings.includeAbovegroundFluids && y < generator.deepseaLevel)
								generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 2, ores.fluidSubsurfaceId, y, ores.fluidSurfaceId, generator.deepseaLevel, ores.fluidId, false);
							else
								generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 2, ores.fluidSubsurfaceId, y, ores.fluidSurfaceId, true);
						else 
							if (generator.settings.includeAbovegroundFluids) {
								generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 2, ores.fluidSubsurfaceId, y, ores.fluidSurfaceId, generator.seaLevel, ores.fluidId, false);
								biome = Biome.OCEAN;
							} else
								generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 2, ores.fluidSubsurfaceId, y, ores.fluidSurfaceId, false);

					// we are in the mountains
					} else {

						// regular trees only
						if (y < generator.treeLevel) {
							generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 3, ores.subsurfaceId, y, ores.surfaceId, generator.settings.includeDecayedNature);
							biome = Biome.FOREST;

						// regular trees and some evergreen trees
						} else if (y < generator.evergreenLevel) {
							generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 2, ores.subsurfaceId, y, ores.surfaceId, surfaceCaves);
							biome = Biome.FOREST_HILLS;

						// evergreen and some of fallen snow
						} else if (y < generator.snowLevel) {
							generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 1, ores.subsurfaceId, y, ores.surfaceId, surfaceCaves);
							biome = Biome.TAIGA_HILLS;
							
						// only snow up here!
						} else {
							if (generator.settings.includeAbovegroundFluids && y > generator.snowLevel + 2)
								generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 1, ores.stratumId, y, ores.fluidFrozenId, surfaceCaves);
							else
								generateStratas(generator, lot, chunk, x, z, ores.substratumId, ores.stratumId, y - 1, ores.stratumId, y, ores.stratumId, surfaceCaves);
							biome = Biome.ICE_MOUNTAINS;
						}
					}
				}
				
				// set biome for block
				biomes.setBiome(x, z, remapBiome(generator, lot, biome));
			}
		}	
	}
	
	@Override
	public void postGenerateChunk(WorldGenerator generator, PlatLot lot, ByteChunk chunk, CachedYs blockYs) {
		
		// mines please
		lot.generateMines(generator, chunk);
	}

	@Override
	public void preGenerateBlocks(WorldGenerator generator, PlatLot lot, RealChunk chunk, CachedYs blockYs) {
		// nothing... yet
	}

	@Override
	public void postGenerateBlocks(WorldGenerator generator, PlatLot lot, RealChunk chunk, CachedYs blockYs) {
		
		// put ores in?
		lot.generateOres(generator, chunk);

		// do we do it or not?
		lot.generateMines(generator, chunk);
	}

	@Override
	public int getWorldHeight() {
		return height;
	}

	@Override
	public int getStreetLevel() {
		return seaLevel + 1;
	}

	@Override
	public int getSeaLevel() {
		return seaLevel;
	}

	@Override
	public int getLandRange() {
		return landRange;
	}

	@Override
	public int getSeaRange() {
		return seaRange;
	}

	@Override
	public int getConstuctMin() {
		return constructMin;
	}

	@Override
	public int getConstuctRange() {
		return constructRange;
	}
	
	@Override
	public double findPerciseY(WorldGenerator generator, int blockX, int blockZ) {
		double y = 0;
		
		// shape the noise
		double noise = noiseShape.noise(blockX, blockZ, noiseFrequency, noiseAmplitude, true);
		double feature = featureShape.noise(blockX, blockZ, featureFrequency, featureAmplitude, true);

		double land1 = seaLevel + (landShape1.noise(blockX, blockZ, landFrequency1, landAmplitude1, true) * landRange) + 
				(noise * noiseVerticalScale * landFactor1to2 + feature * featureVerticalScale * landFactor1to2) - landFlattening;
		double land2 = seaLevel + (landShape2.noise(blockX, blockZ, landFrequency2, landAmplitude2, true) * (landRange / (double) landFactor1to2)) + 
				(noise * noiseVerticalScale + feature * featureVerticalScale) - landFlattening;
		
		double landY = Math.max(land1, land2);
		double sea = seaShape.noise(blockX, blockZ, seaFrequency, seaAmplitude, true);
		
		// calculate the Ys
		double seaY = seaLevel + (sea * seaRange) + (noise * noiseVerticalScale) + seaFlattening;

		// land is below the sea
		if (landY <= seaLevel) {

			// if seabed is too high... then we might be buildable
			if (seaY >= seaLevel) {
				y = seaLevel + 1;

				// if we are too near the sea then we must be on the beach
				if (seaY <= seaLevel + 1) {
					y = seaLevel;
				}

			// if land is higher than the seabed use land to smooth
			// out under water base of the mountains 
			} else if (landY >= seaY) {
				y = Math.min(seaLevel, landY + 1);

			// otherwise just take the sea bed as is
			} else {
				y = Math.min(seaLevel, seaY);
			}

		// must be a mountain then
		} else {
			y = Math.max(seaLevel, landY + 1);
		}
		
		// for real?
		if (!generator.settings.includeMountains)
			y = Math.min(seaLevel + 1, y);
		if (!generator.settings.includeSeas)
			y = Math.max(seaLevel + 1, y);

		// range validation
		return Math.min(height - 3, Math.max(y, 3));
	}
	
	@Override
	public boolean isHorizontalNSShaft(int chunkX, int chunkY, int chunkZ) {
		return mineShape.noise(chunkX * mineScale, chunkY * mineScale, chunkZ * mineScale + 0.5) > 0.0;
	}

	@Override
	public boolean isHorizontalWEShaft(int chunkX, int chunkY, int chunkZ) {
		return mineShape.noise(chunkX * mineScale + 0.5, chunkY * mineScale, chunkZ * mineScale) > 0.0;
	}

	@Override
	public boolean isVerticalShaft(int chunkX, int chunkY, int chunkZ) {
		return mineShape.noise(chunkX * mineScale, chunkY * mineScale + 0.5, chunkZ * mineScale) > 0.0;
	}


	@Override
	public boolean notACave(WorldGenerator generator, int blockX, int blockY, int blockZ) {
		if (generator.settings.includeCaves) {
			double cave = caveShape.noise(blockX * caveScale, blockY * caveScale, blockZ * caveScale);
			if(!(Math.abs(cave) < caveThreshold)) // Is it a cave depending on simple noise?
                return false;
            //return !NodeCaves.isCave(generator,blockX,blockY,blockZ);


		}
		return true;
	}
	
	public boolean isSurfaceCaveAt(double chunkX, double chunkZ) {
		return microBooleanAt(chunkX, chunkZ, microSurfaceCaveSlot);
	}

}
