package me.daddychurchill.CityWorld.Plugins.AthmosCaves;

import me.daddychurchill.CityWorld.WorldGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.07.13
 * Time: 02:30
 * To change this template use File | Settings | File Templates.
 */
public interface CaveGenerator {
    /**
     * Determines if a given block is part of a Cave, meaning
     * that it should be air
     * @param generator the WorldGenerator to use
     * @param blockX x Coordinates of block
     * @param blockY y Coordinates of block
     * @param blockZ z Coordinates of block
     * @return
     */
    public boolean isCave(WorldGenerator generator, int blockX, int blockY, int blockZ);
}
