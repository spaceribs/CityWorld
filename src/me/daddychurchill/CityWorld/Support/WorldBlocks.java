package me.daddychurchill.CityWorld.Support;

import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Context.DataContext;

public class WorldBlocks extends SupportChunk {

	private boolean doPhysics;
	WorldGenerator generator;
	Odds odds;
	
	public WorldBlocks(WorldGenerator generator, Odds odds) {
		super(generator);
		
		doPhysics = false;
		this.generator = generator;
		this.odds = odds;
	}

	public boolean getDoPhysics() {
		return doPhysics;
	}
	
	public void setDoPhysics(boolean dophysics) {
		doPhysics = dophysics;
	}

	public Block getActualBlock(int x, int y, int z) {
		return world.getBlockAt(x, y, z);
	}
	
	@Override
	public boolean isType(int x, int y, int z, int type) {
		return world.getBlockAt(x, y, z).getTypeId() == type;
	}

	@Override
	public boolean isEmpty(int x, int y, int z) {
		return world.getBlockAt(x, y, z).isEmpty();
	}
	
	@Override
	public void setBlock(int x, int y, int z, byte materialId) {
		world.getBlockAt(x, y, z).setTypeIdAndData(materialId, (byte) 0, doPhysics);
	}

	@Override
	public void setBlocks(int x1, int x2, int y, int z1, int z2, byte materialId) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				world.getBlockAt(x, y, z).setTypeIdAndData(materialId, (byte) 0, doPhysics);
			}
		}
	}
	
	@Override
	public void setBlocks(int x, int y1, int y2, int z, byte type) {
		for (int y = y1; y < y2; y++)
			world.getBlockAt(x, y, z).setTypeIdAndData(type, (byte) 0, doPhysics);
	}

	public void setBlock(int x, int y, int z, Material material) {
		world.getBlockAt(chunkX + x, y, chunkZ + z).setTypeId(material.getId(), doPhysics);
	}

	public void setBlock(int x, int y, int z, int type, byte data) {
		world.getBlockAt(chunkX + x, y, chunkZ + z).setTypeIdAndData(type, data, doPhysics);
	}
	
	public void setBlock(int x, int y, int z, Material material, boolean aDoPhysics) {
		world.getBlockAt(chunkX + x, y, chunkZ + z).setTypeId(material.getId(), aDoPhysics);
	}

	public void setBlock(int x, int y, int z, int type, byte data, boolean aDoPhysics) {
		world.getBlockAt(chunkX + x, y, chunkZ + z).setTypeIdAndData(type, data, aDoPhysics);
	}
	
	public void setBlocks(int x, int y1, int y2, int z, Material material) {
		for (int y = y1; y < y2; y++)
			world.getBlockAt(x, y, z).setType(material);
	}

	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, Material material) {
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
					world.getBlockAt(x, y, z).setType(material);
				}
			}
		}
	}

	public void setBlocks(int x, int y1, int y2, int z, Material material, byte data) {
		for (int y = y1; y < y2; y++)
			world.getBlockAt(x, y, z).setTypeIdAndData(material.getId(), data, doPhysics);
	}

	public void setBlocks(int x, int y1, int y2, int z, Material material, byte data, boolean aDoPhysics) {
		for (int y = y1; y < y2; y++)
			world.getBlockAt(x, y, z).setTypeIdAndData(material.getId(), data, aDoPhysics);
	}

	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, Material material, byte data, boolean aDoPhysics) {
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
					world.getBlockAt(x, y, z).setTypeIdAndData(material.getId(), data, aDoPhysics);
				}
			}
		}
	}

	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, Material material, byte data) {
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
					world.getBlockAt(x, y, z).setTypeIdAndData(material.getId(), data, doPhysics);
				}
			}
		}
	}

	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, int type, byte data) {
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
					world.getBlockAt(x, y, z).setTypeIdAndData(type, data, doPhysics);
				}
			}
		}
	}
	
	@Override
	public void clearBlock(int x, int y, int z) {
		world.getBlockAt(x, y, z).setType(Material.AIR);
	}

	@Override
	public void clearBlocks(int x, int y1, int y2, int z) {
		for (int y = y1; y < y2; y++) {
			world.getBlockAt(x, y, z).setTypeIdAndData(airId, (byte) 0, doPhysics);
		}
	}

	@Override
	public void clearBlocks(int x1, int x2, int y1, int y2, int z1, int z2) {
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				for (int z = z1; z < z2; z++) {
					world.getBlockAt(x, y, z).setTypeIdAndData(airId, (byte) 0, doPhysics);
				}
			}
		}
	}
	public void setBlocks(int x1, int x2, int y, int z1, int z2, Material material) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				world.getBlockAt(x, y, z).setTypeId(material.getId(), doPhysics);
			}
		}
	}

	public void setBlocks(int x1, int x2, int y, int z1, int z2, Material material, boolean aDoPhysics) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				world.getBlockAt(x, y, z).setTypeId(material.getId(), aDoPhysics);
			}
		}
	}

	public void setBlocks(int x1, int x2, int y, int z1, int z2, Material material, byte data, boolean aDoPhysics) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				world.getBlockAt(x, y, z).setTypeIdAndData(material.getId(), data, aDoPhysics);
			}
		}
	}

	public void setBlocks(int x1, int x2, int y, int z1, int z2, Material material, byte data) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				world.getBlockAt(x, y, z).setTypeIdAndData(material.getId(), data, doPhysics);
			}
		}
	}

	public void setWalls(int x1, int x2, int y1, int y2, int z1, int z2, Material material) {
		setBlocks(x1, x2, y1, y2, z1, z1 + 1, material);
		setBlocks(x1, x2, y1, y2, z2 - 1, z2, material);
		setBlocks(x1, x1 + 1, y1, y2, z1 + 1, z2 - 1, material);
		setBlocks(x2 - 1, x2, y1, y2, z1 + 1, z2 - 1, material);
	}
	
	public void setWalls(int x1, int x2, int y1, int y2, int z1, int z2, int type, byte data) {
		setBlocks(x1, x2, y1, y2, z1, z1 + 1, type, data);
		setBlocks(x1, x2, y1, y2, z2 - 1, z2, type, data);
		setBlocks(x1, x1 + 1, y1, y2, z1 + 1, z2 - 1, type, data);
		setBlocks(x2 - 1, x2, y1, y2, z1 + 1, z2 - 1, type, data);
	}
	
	public boolean setEmptyBlock(int x, int y, int z, Material material) {
		Block block = world.getBlockAt(x, y, z);
		if (block.isEmpty()) {
			block.setTypeId(material.getId(), doPhysics);
			return true;
		} else
			return false;
	}

	public boolean setEmptyBlock(int x, int y, int z, int type, byte data) {
		Block block = world.getBlockAt(x, y, z);
		if (block.isEmpty()) {
			block.setTypeIdAndData(type, data, doPhysics);
			return true;
		} else
			return false;
	}
	
	public boolean setEmptyBlock(int x, int y, int z, Material material, boolean aDoPhysics) {
		Block block = world.getBlockAt(x, y, z);
		if (block.isEmpty()) {
			block.setTypeId(material.getId(), aDoPhysics);
			return true;
		} else
			return false;
	}

	public boolean setEmptyBlock(int x, int y, int z, int type, byte data, boolean aDoPhysics) {
		Block block = world.getBlockAt(x, y, z);
		if (block.isEmpty()) {
			block.setTypeIdAndData(type, data, aDoPhysics);
			return true;
		} else
			return false;
	}
	
	public void setEmptyBlocks(int x1, int x2, int y, int z1, int z2, Material material) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				Block block = world.getBlockAt(x, y, z);
				if (block.isEmpty())
					block.setType(material);
			}
		}
	}
	
	public void setEmptyBlocks(int x1, int x2, int y, int z1, int z2, int type, byte data, boolean aDoPhysics) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				Block block = world.getBlockAt(x, y, z);
				if (block.isEmpty())
					block.setTypeIdAndData(type, data, aDoPhysics);
			}
		}
	}
	
	public int findLastEmptyAbove(int x, int y, int z) {
		int y1 = y;
		while (y1 < height - 1 && world.getBlockAt(x, y1 + 1, z).isEmpty()) {
			y1++;
		}
		return y1;
	}
	
	public int findLastEmptyBelow(int x, int y, int z) {
		int y1 = y;
		while (y1 > 0 && world.getBlockAt(x, y1 - 1, z).isEmpty()) {
			y1--;
		}
		return y1;
	}
	
	public int setLayer(int blocky, Material material) {
		setBlocks(0, width, blocky, blocky + 1, 0, width, material);
		return blocky + 1;
	}

	public int setLayer(int blocky, int height, Material material) {
		setBlocks(0, width, blocky, blocky + height, 0, width, material);
		return blocky + height;
	}

	public int setLayer(int blocky, int height, int inset, Material material) {
		setBlocks(inset, width - inset, blocky, blocky + height, inset, width - inset, material);
		return blocky + height;
	}
	
	public boolean isPlantable(int x, int y, int z) {
		return world.getBlockAt(x, y, z).getTypeId() == grassId;
	}
	
	public boolean isSupporting(Block block) {
		return (
			block.getType() != Material.LEAVES 
			&& block.getType() != Material.VINE
			&& block.getType() != Material.LOG
			&& !block.isEmpty()
		);
	}
	
	public void destroyWithin(int x1, int x2, int y1, int y2, int z1, int z2) {
		
		double holeScale = 1.0 / 20.0;
		double leavesScale = 1.0 / 10.0;
		double weatheringScale = 1.0 / 5.0;
		
		long seed = generator.getWorldSeed();
		SimplexOctaveGenerator noiseGen = new SimplexOctaveGenerator(seed,2);
		
		for(int z=z1;z<z2;z++){ 
			for(int x=x1;x<x2;x++){
				for(int y=y1;y<y2;y++) {
					
					double holeNoise = noiseGen.noise(x * holeScale, y * holeScale, z * holeScale, 0.3D, 0.6D, true);
					double leavesNoise = noiseGen.noise(x * leavesScale, y * leavesScale, z * leavesScale, 0.3D, 0.6D, false);
					double weatheringNoise = noiseGen.noise(x * weatheringScale, y * weatheringScale, z * weatheringScale, 0.6D, 0.6D, true);
					
					Block block = world.getBlockAt(x, y, z);
					
					if (!block.isEmpty() && ( holeNoise > 0.5D || weatheringNoise > 0.7D ) ) {
						block.setType(Material.AIR);
					} else if ( holeNoise > 0.30D || weatheringNoise > 0.5D ) {
						switch(block.getType()) {
							case STONE:
								if(odds.flipCoin())
									block.setType(Material.COBBLESTONE);
								else
									block.setType(Material.MOSSY_COBBLESTONE);
								break;
							case SANDSTONE:
								block.setTypeIdAndData(Material.SANDSTONE_STAIRS.getId(), odds.getRandomByte(4), true);
								break;
							case BRICK:
								block.setTypeIdAndData(Material.BRICK_STAIRS.getId(), odds.getRandomByte(4), true);
								break;
							case COBBLESTONE:
								block.setTypeIdAndData(Material.MOSSY_COBBLESTONE.getId(), odds.getRandomByte(4), true);
								break;
							case SMOOTH_BRICK:
								block.setTypeIdAndData(Material.SMOOTH_BRICK.getId(), odds.getRandomByte(3), true);
								break;
							case WOOD:
								switch(block.getData()){
									case 0:
										block.setTypeIdAndData(Material.WOOD_STAIRS.getId(), odds.getRandomByte(4), true);
										break;
									case 1:
										block.setTypeIdAndData(Material.SPRUCE_WOOD_STAIRS.getId(), odds.getRandomByte(4), true);
										break;
									case 2:
										block.setTypeIdAndData(Material.BIRCH_WOOD_STAIRS.getId(), odds.getRandomByte(4), true);
										break;
									default:
										block.setTypeIdAndData(Material.JUNGLE_WOOD_STAIRS.getId(), odds.getRandomByte(4), true);
										break;
								}
								break;
							default:
								block.setType(Material.AIR);
								break;
						}
						
						Block[] neighbors = {
							block.getRelative(0, 1, 0),
							block.getRelative(0, 0, -1),
							block.getRelative(0, 0, 1),
							block.getRelative(1, 0, 0),
							block.getRelative(-1, 0, 0),
							block.getRelative(0, -1, 0)
						};
						
						if ( leavesNoise > 0.1D && holeNoise > 0.30D && block.isEmpty() ) {
							int support = 0;
							
							for(int n=0;n<neighbors.length;n++)
								support += this.isSupporting(neighbors[n]) ? 1 : 0;
							
							if (support > 0)
								block.setTypeIdAndData(Material.LEAVES.getId(), odds.getRandomByte(4) , true);
						}
						if ( block.isEmpty() && odds.flipCoin() ) {
							
							byte vineMeta = 0;
							
							if( !neighbors[1].isEmpty() && neighbors[1].getType() != Material.VINE )
								vineMeta += 4;
							if( !neighbors[2].isEmpty() && neighbors[2].getType() != Material.VINE )
								vineMeta += 1;
							if( !neighbors[3].isEmpty() && neighbors[3].getType() != Material.VINE )
								vineMeta += 8;
							if( !neighbors[4].isEmpty() && neighbors[4].getType() != Material.VINE )
								vineMeta += 2;
							
							block.setTypeIdAndData(Material.VINE.getId(), vineMeta, true);
						}
					}
				}
			}
		}
		
//		int count = Math.max(1, (y2 - y1) / DataContext.FloorHeight);
//		
//		// now destroy it
//		while (count > 0) {
//			
//			// find a place
//			int cx = getBlockX(odds.getRandomInt(x2 - x1) + x1);
//			int cz = getBlockZ(odds.getRandomInt(z2 - z1) + z1);
//			int cy = odds.getRandomInt(Math.max(1, y2 - y1)) + y1;
//			int radius = odds.getRandomInt(3) + 3;
//			
//			// make it go away
//			desperseArea(cx, cy, cz, radius);
//			
//			// done with this round
//			count--;
//		}
	}
	
	private static class debrisItem {
		int typeId;
		byte data;
		
		public debrisItem(int typeId, byte data) {
			this.typeId = typeId;
			this.data = data;
		}
	}
	
	private void disperseLine(int x1, int x2, int y, int z1, int z2, Stack<debrisItem> debris) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				
				Block block = world.getBlockAt(x, y, z);
				
				int blockId = block.getTypeId();
				if ( blockId != glassId && blockId != thinGlassId ) {
					
					if (!block.isEmpty()) {
						debris.push(new debrisItem(blockId, block.getData()));
						
						if( odds.playOdds( DataContext.oddsExtremelyUnlikely ) ) {
							block.setTypeId(airId);
						} else {
							if (( x == x1 || x == x2-1 ) || ( z == z1 || z == z2-1 ))
								block.setTypeIdAndData(Material.AIR.getId(), (byte) odds.getRandomInt(3) , true);
						}
					}
				} else {
					block.setTypeId(airId);
				}
			}
		}
	}
	
	private void disperseCircle(int cx, int cz, int r, int y, Stack<debrisItem> debris) {

		// Ref: Notes/BCircle.PDF
		int x = r;
		int z = 0;
		int xChange = 1 - 2 * r;
		int zChange = 1;
		int rError = 0;
		
		while (x >= z) {
			
			disperseLine(cx - x - 1, cx - x, y, cz - z - 1, cz + z + 1, debris); // point in octant 5
			disperseLine(cx - z - 1, cx - z, y, cz - x - 1, cz + x + 1, debris); // point in octant 6
			disperseLine(cx + z, cx + z + 1, y, cz - x - 1, cz + x + 1, debris); // point in octant 7
			disperseLine(cx + x, cx + x + 1, y, cz - z - 1, cz + z + 1, debris); // point in octant 8
			
			z++;
			rError += zChange;
			zChange += 2;
			if (2 * rError + xChange > 0) {
				x--;
				rError += xChange;
				xChange += 2;
			}
		}
	}
	
	//TODO while is approximates a sphere it isn't really a good one
	private void desperseSphere(int cx, int cy, int cz, int r, Stack<debrisItem> debris) {
		// for each slice of the sphere
		for (int r1 = 1; r1 < r; r1++) {
			disperseCircle(cx, cz, r - r1, cy + r1, debris);
			disperseCircle(cx, cz, r - r1, cy - r1, debris);
		}
		disperseCircle(cx, cz, r, cy, debris);
	}
	
	private final static double oddsOfDebris = DataContext.oddsPrettyLikely;
	
	private void sprinkleDebris(int cx, int cy, int cz, int radius, Stack<debrisItem> debris) {

		// calculate a few things
		int r2 = radius * 2;
		int r4 = r2 * 2;
		int x1 = cx - r2;
		int z1 = cz - r2;
		
		// while there is still something left to do
		while (!debris.empty()) {
			
			// grab the next one
			debrisItem item = debris.pop();
			
			// do this one?
			if (odds.playOdds(oddsOfDebris)) {
				
				// where do we drop it?
				int x = x1 + odds.getRandomInt(r4);
				int z = z1 + odds.getRandomInt(r4);
				int y = findLastEmptyBelow(x, cy, z);
				
				// look out for half blocks
				Block block = getActualBlock(x, y - 1, z);
				int blockId = block.getTypeId();
					
				// partial blocks
				if (blockId == stepId || blockId == snowId)
					block.setTypeIdAndData(item.typeId, item.data, false);
				
				// other blocks?
				else {
					
					// find the bottom of the pool
					if (block.isLiquid()) {
						do {
							y--;
							block = getActualBlock(x, y - 1, z);
						} while (block.isLiquid());
					}
					
					// place the block
					setBlock(x, y, z, item.typeId, item.data, false);
				}
			}
		}
	}

	public void desperseArea(int x, int y, int z, int radius) {
		
		// debris
		Stack<debrisItem> debris = new Stack<debrisItem>();
		
		// clear out the space
		desperseSphere(x, y, z, radius, debris);
		
		// now sprinkle blocks around
		if (odds.playOdds(DataContext.oddsVeryUnlikely)) {
			sprinkleDebris(x, y, z, radius, debris);
		}
	}
}
