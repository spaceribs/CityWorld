package me.daddychurchill.CityWorld.Plugins;

import java.util.HashSet;
import java.util.Set;

import me.daddychurchill.CityWorld.Support.ByteChunk;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.XYZ;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * BlockPopulator for snake-based caves.
 * 
 * @author Pandarr
 * modified by simplex
 */

public class CaveProvider {
	
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	public void populate(final World world, final Odds odds, ByteChunk source) {

		if (odds.getRandomInt(100) < 3) {
			final int x = 4 + odds.getRandomInt(8) + source.chunkX * 16;
			final int z = 4 + odds.getRandomInt(8) + source.chunkZ * 16;
			int maxY = world.getHighestBlockYAt(x, z);
			if (maxY < 16) {
				maxY = 32;
			}

			final int y = odds.getRandomInt(maxY);
			Set<XYZ> snake = selectBlocksForCave(world, odds, x, y, z);
			buildCave(world, snake.toArray(new XYZ[0]));
			for (XYZ block : snake) {
				world.unloadChunkRequest(block.x / 16, block.z / 16);
			}
		}
	}

	static Set<XYZ> selectBlocksForCave(World world, Odds odds, int blockX, int blockY, int blockZ) {
		Set<XYZ> snakeBlocks = new HashSet<XYZ>();

		int airHits = 0;
		XYZ block = new XYZ();
		while (true) {
			if (airHits > 1200) {
				break;
			}

			if (odds.getRandomInt(20) == 0) {
				blockY++;
			} 
			else if (world.getBlockTypeIdAt(blockX, blockY + 2, blockZ) == 0) {
				blockY += 2;
			} 
			else if (world.getBlockTypeIdAt(blockX + 2, blockY, blockZ) == 0) {
				blockX++;
			} 
			else if (world.getBlockTypeIdAt(blockX - 2, blockY, blockZ) == 0) {
				blockX--;
			} 
			else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 2) == 0) {
				blockZ++;
			} 
			else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 2) == 0) {
				blockZ--;
			} 
			else if (world.getBlockTypeIdAt(blockX + 1, blockY, blockZ) == 0) {
				blockX++;
			} 
			else if (world.getBlockTypeIdAt(blockX - 1, blockY, blockZ) == 0) {
				blockX--;
			} 
			else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 1) == 0) {
				blockZ++;
			} 
			else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 1) == 0) {
				blockZ--;
			} 
			else if (odds.flipCoin()) {
				if (odds.flipCoin()) {
					blockX++;
				} else {
					blockZ++;
				}
			} else {
				if (odds.flipCoin()) {
					blockX--;
				} else {
					blockZ--;
				}
			}

			if (world.getBlockTypeIdAt(blockX, blockY, blockZ) != 0) {
				int radius = 1 + odds.getRandomInt(2);
				int radius2 = radius * radius + 1;
				for (int x = -radius; x <= radius; x++) {
					for (int y = -radius; y <= radius; y++) {
						for (int z = -radius; z <= radius; z++) {
							if (x * x + y * y + z * z <= radius2 && y >= 0&& y < 128) {
								if (world.getBlockTypeIdAt(blockX + x, blockY+ y, blockZ + z) == 0) {
									airHits++;
								} else {
									block.x = blockX + x;
									block.y = blockY + y;
									block.z = blockZ + z;
									if (snakeBlocks.add(block)) {
										block = new XYZ();
									}
								}
							}
						}
					}
				}
			} else {
				airHits++;
			}
		}

		return snakeBlocks;
	}

	static void buildCave(World world, XYZ[] snakeBlocks) {
		for (XYZ loc : snakeBlocks) {
			Block block = world.getBlockAt(loc.x, loc.y, loc.z);
			if (!block.isEmpty() && !block.isLiquid()&& block.getType() != Material.BEDROCK) {
				block.setType(Material.AIR);
			}
		}
	}
}
