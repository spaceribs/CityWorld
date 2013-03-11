package me.daddychurchill.CityWorld.Rooms;

import me.daddychurchill.CityWorld.Support.Direction.Facing;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.RealChunk;

public class DividedEllRoom extends FilledRoom {

	public DividedEllRoom() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Draw(RealChunk chunk, Odds odds, 
			int floor, int x, int y, int z, int width, int height, 
			int depth, Facing sideWithWall, byte wallId, byte glassId) {
		switch (sideWithWall) {
		case NORTH:
			chunk.setBlocks(x, x + 1, y, y + height, z, z + depth, wallId, glassId);
			chunk.setBlocks(x + 1, x + width, y, y + height, z + depth - 1, z + depth, wallId, glassId);
			break;
		case SOUTH:
			chunk.setBlocks(x + width - 1, x + width, y, y + height, z, z + depth, wallId, glassId);
			chunk.setBlocks(x, x + width - 1, y, y + height, z, z + 1, wallId, glassId);
			break;
		case WEST:
			chunk.setBlocks(x, x + width, y, y + height, z + depth - 1, z + depth, wallId, glassId);
			chunk.setBlocks(x + width - 1, x + width, y, y + height, z, z + depth - 1, wallId, glassId);
			break;
		case EAST:
			chunk.setBlocks(x, x + width, y, y + height, z, z + 1, wallId, glassId);
			chunk.setBlocks(x, x + 1, y, y + height, z + 1, z + depth, wallId, glassId);
			break;
		}
	}

}
