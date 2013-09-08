package me.daddychurchill.CityWorld.Plugins.WorldEdit;

import java.io.File;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Clipboard.Clipboard;
import me.daddychurchill.CityWorld.Support.Direction;
import me.daddychurchill.CityWorld.Support.RealChunk;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.CuboidClipboard.FlipDirection;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class Clipboard_WorldEdit extends Clipboard {

	private BaseBlock[][][][] blocks;
	private int facingCount;
	private boolean flipableX = false;
	private boolean flipableZ = false;
//  private boolean Rotatable = false;
//	private boolean ScalableXZ = false;
//	private boolean ScalableY = false;
//	private int FloorHeightY = DataContext.FloorHeight;

	private final static String metaExtension = ".yml";
	private final static String tagGroundLevelY = "GroundLevelY";
	private final static String tagFlipableX = "FlipableX";
	private final static String tagFlipableZ = "FlipableZ";
//	private final static String tagScalableX = "ScalableX";
//	private final static String tagScalableZ = "ScalableZ";
//	private final static String tagScalableY = "ScalableY";
//	private final static String tagFloorHeightY = "FloorHeightY";
	private final static String tagOddsOfAppearance = "OddsOfAppearance";
	private final static String tagBroadcastLocation = "BroadcastLocation";
	private final static String tagDecayable = "Decayable";
	private final static String tagChestName = "ChestName";
	private final static String tagChestOdds = "ChestOdds";
	private final static String tagSpawnerType = "SpawnerType";
	private final static String tagSpawnerOdds = "SpawnerOdds";
    //private final static String tagEntranceFacing = "EntranceFacing";
	
	public Clipboard_WorldEdit(WorldGenerator generator, File file) throws Exception {
		super(generator, file);
	}
	
	@Override
	protected void load(WorldGenerator generator, File file) throws Exception {
		
		// prepare to read the meta data
		YamlConfiguration metaYaml = new YamlConfiguration();
		metaYaml.options().header("CityWorld/WorldEdit schematic configuration");
		metaYaml.options().copyDefaults(true);
		
		// add the defaults
		metaYaml.addDefault(tagGroundLevelY, groundLevelY);
		metaYaml.addDefault(tagFlipableX, flipableX);
		metaYaml.addDefault(tagFlipableZ, flipableZ);
//		metaYaml.addDefault(tagScalableX, ScalableX);
//		metaYaml.addDefault(tagScalableZ, ScalableZ);
//		metaYaml.addDefault(tagScalableY, ScalableY);
//		metaYaml.addDefault(tagFloorHeightY, FloorHeightY);
		metaYaml.addDefault(tagOddsOfAppearance, oddsOfAppearance);
		metaYaml.addDefault(tagBroadcastLocation, broadcastLocation);
		metaYaml.addDefault(tagDecayable, decayable);
		metaYaml.addDefault(tagChestName, chestName);
		metaYaml.addDefault(tagChestOdds, chestOdds);
		metaYaml.addDefault(tagSpawnerType, spawnerType);
		metaYaml.addDefault(tagSpawnerOdds, spawnerOdds);
        //metaYaml.addDefault(tagEntranceFacing, entranceFacing);
		
		// start reading it
		File metaFile = new File(file.getAbsolutePath() + metaExtension);
		if (metaFile.exists()) {
			metaYaml.load(metaFile);
			groundLevelY = Math.max(0, metaYaml.getInt(tagGroundLevelY, groundLevelY));
			flipableX = metaYaml.getBoolean(tagFlipableX, flipableX);
			flipableZ = metaYaml.getBoolean(tagFlipableZ, flipableZ);
//			ScalableX = metaYaml.getBoolean(tagScalableX, ScalableX) && sizeX == 3;
//			ScalableZ = metaYaml.getBoolean(tagScalableZ, ScalableZ) && sizeZ == 3;
//			ScalableY = metaYaml.getBoolean(tagScalableY, ScalableY);
//			FloorHeightY = Math.max(2, Math.min(16, metaYaml.getInt(tagFloorHeightY, FloorHeightY)));
			oddsOfAppearance = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagOddsOfAppearance, oddsOfAppearance)));
			broadcastLocation = metaYaml.getBoolean(tagBroadcastLocation, broadcastLocation);
			decayable = metaYaml.getBoolean(tagDecayable, decayable);
			chestName = metaYaml.getString(tagChestName, chestName);
			chestOdds = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagChestOdds, chestOdds)));
			spawnerType = metaYaml.getString(tagSpawnerType, spawnerType);
			spawnerOdds = Math.max(0.0, Math.min(1.0, metaYaml.getDouble(tagSpawnerOdds, spawnerOdds)));
            //entranceFacing = metaYaml.getString(tagEntranceFacing,entranceFacing);
		}

		// load the actual blocks
		CuboidClipboard cuboid = SchematicFormat.getFormat(file).load(file);
		
		// how big is it?
		sizeX = cuboid.getWidth();
		sizeZ = cuboid.getLength();
		sizeY = cuboid.getHeight();
		
		//TODO Validate the size
		
		// try and save the meta data if we can
		try {
			metaYaml.save(metaFile);
		} catch (IOException e) {
			
			// we can recover from this... so eat it!
			generator.reportException("[WorldEdit] Could not resave " + metaFile.getAbsolutePath(), e);
		}
		
		// grab the edge block
		BaseBlock edge = cuboid.getPoint(new Vector(0, groundLevelY, 0));
		edgeType = Material.getMaterial(edge.getType());
		edgeData = (byte) edge.getData(); //TODO I think that data can be integers... one of these days
		//edgeData = (byte)((edge.getData() & 0x000000ff)); // this would make overflows not error out but let's not do that
		edgeRise = generator.oreProvider.surfaceId == edgeType.getId() ? 0 : 1;
		
		// allocate the blocks
		facingCount = 1;
		if (flipableX)
			facingCount *= 2;
		if (flipableZ)
			facingCount *= 2;
//        if (sizeX==sizeZ)
//            facingCount =4; // we can rotate it
		
		//TODO we should allocate only facing count, then allocate the size based on what comes out of the rotation.. once I do rotation
		// allocate room
		blocks = new BaseBlock[facingCount][sizeX][sizeY][sizeZ];
		
		// copy the cubes for each direction
		copyCuboid(cuboid, 0); // normal one

		if (flipableX) {
			cuboid.flip(FlipDirection.WEST_EAST);
			copyCuboid(cuboid, 1);
			
			// z too? if so then make two more copies
			if (flipableZ) {
				cuboid.flip(FlipDirection.NORTH_SOUTH);
				copyCuboid(cuboid, 3);
				cuboid.flip(FlipDirection.WEST_EAST);
				copyCuboid(cuboid, 2);
			}
		
		// just z
		} else if (flipableZ) {
			cuboid.flip(FlipDirection.NORTH_SOUTH);
			copyCuboid(cuboid, 1);
		}else {
//            if(sizeX==sizeZ){ // Yey, we can rotate it easily!
//                try {
//                    blocks[1] = rotateSquare90(blocks[0]); // rotated once...   90deg
//                    blocks[2] = rotateSquare90(blocks[1]); // twice             180deg
//                    blocks[3] = rotateSquare90(blocks[2]); // triple            270deg
//                }catch (Exception e){
//                    generator.reportException("Unable to rotate build.",e);
//                    facingCount=1;
//                }
//
//            }
        }
	}

    /**
     * Rotates an cuboid with a square footprint or do nothing
     * if not square
     * @param blocks array of all the blocks of the cuboid
     * @return same blocks, but footprint rotated by 90 degrees
     */
    private BaseBlock[][][] rotateSquare90(BaseBlock[][][] blocks){
        if(blocks.length!=blocks[0][0].length){

            throw new RuntimeException("Tried to rotate non-quadratic schematic, sorry I panicked :(");
        }
        int size = blocks.length;
        BaseBlock[][][] rotatedBlocks = new BaseBlock[size][blocks[0].length][size];

        //first transpose
        for(int x=0;x<size;x++){
            for(int z=0;z<size;z++){
                moveRotColumn(blocks, rotatedBlocks, x, z, z, x);
            }
        }
        //then reverse columns
        for(int x=0;x<size;x++){
            for(int z=0;z<(size/2);z++){
                swapColumn(rotatedBlocks,z,x,size-z-1,x);
            }
        }
        return rotatedBlocks;
    }

    /**
     * Swaps a column in the array of blocks
     * @param blocks
     * @param x1    x position of first column
     * @param z1    z position of first column
     * @param x2    x position of second column
     * @param z2    z position of second column
     */
    private void swapColumn(BaseBlock[][][] blocks,int x1,int z1,int x2, int z2){
        BaseBlock temp;
        for(int y=0;y< blocks[0].length;y++){
            temp = blocks[x1][y][z1];
            blocks[x1][y][z1] = blocks[x2][y][z2]; // FIXME
            blocks[x2][y][z2] = temp;
        }
    }

    /**
     * Moves and rotates a column by 90deg in the array of blocks
     * @param blocks1 source array
     * @param blocks2 destination array
     * @param x1    x position of source column
     * @param z1    z position of source column
     * @param x2    x position of destination column
     * @param z2    z position of destination column
     */
    private void moveRotColumn(BaseBlock[][][] blocks1, BaseBlock[][][] blocks2, int x1, int z1, int x2, int z2){
        BaseBlock temp;
        for(int y=0;y< blocks1[0].length;y++){
            temp = blocks1[x1][y][z1];
            temp.setData(temp.rotate90()); // TODO does this work?
            blocks2[x2][y][z2] = temp;
        }
    }
	
	private void copyCuboid(CuboidClipboard cuboid, int facing) {
	    for (int x = 0; x < sizeX; x++)
	        for (int y = 0; y < sizeY; y++)
	          for (int z = 0; z < sizeZ; z++)
	        	  blocks[facing][x][y][z] = cuboid.getPoint(new Vector(x, y, z));
	}
	
	private EditSession getEditSession(WorldGenerator generator) {
		return new EditSession(new BukkitWorld(generator.getWorld()), blockCount);
	}
	
	private int getFacingIndex(Direction.Facing facing) {
		int result;
		switch (facing) {
		case SOUTH:
			result = 0;
			break;
		case WEST:
			result = 1;
			break;
		case NORTH:
			result = 2;
			break;
        case EAST:
            result = 3; //fixme
            break;
		default:
			result = 2;
			break;
		}
		return Math.min(facingCount - 1, result);
	}

    private int getFacingIndex(String facing) {
        int result=2;
        facing.toLowerCase();

        if(facing.equals("south")){
            result=0;
        }else if(facing.equals("west")){
            result=1;
        }else if(facing.equals("north")){
            result=2;
        }else if(facing.equals("east")){
            result=3;
        }
        return Math.min(facingCount - 1, result);
    }
	
	@Override
	public void paste(WorldGenerator generator, RealChunk chunk, Direction.Facing facing, int blockX, int blockY, int blockZ) {
		Vector at = new Vector(blockX, blockY, blockZ);
		try {
			EditSession editSession = getEditSession(generator);
			//editSession.setFastMode(true);
			place(editSession, getFacingIndex(facing), at, true);
		} catch (Exception e) {
			generator.reportException("[WorldEdit] Place schematic " + name + " at " + at + " failed", e);
		}
	}

	
//	@Override
//	public void paste(WorldGenerator generator, RealChunk chunk, Direction.Facing facing, 
//			int blockX, int blockY, int blockZ,
//			int x1, int x2, int y1, int y2, int z1, int z2) {
//		
////		generator.reportMessage("Partial paste: origin = " + at + " min = " + min + " max = " + max);
//		
//		try {
//			int iFacing = getFacingIndex(facing);
//			EditSession editSession = getEditSession(generator);
//			//editSession.setFastMode(true);
//			for (int x = x1; x < x2; x++)
//				for (int y = y1; y < y2; y++)
//					for (int z = z1; z < z2; z++) {
////						generator.reportMessage("facing = " + iFacing + 
////								" x = " + x +
////								" y = " + y + 
////								" z = " + z);
//						if (blocks[iFacing][x][y][z].isAir()) {
//							continue;
//						}
//						editSession.setBlock(new Vector(x, y, z).add(blockX, blockY, blockZ), 
//								blocks[iFacing][x][y][z]);
//					}
//		} catch (Exception e) {
//			e.printStackTrace();
//			generator.reportException("[WorldEdit] Partial place schematic " + name + " failed", e);
//		}
//	}

	//TODO remove the editSession need by directly setting the blocks in the chunk
	@Override
	public void paste(WorldGenerator generator, RealChunk chunk, Direction.Facing facing, 
			int blockX, int blockY, int blockZ,
			int x1, int x2, int y1, int y2, int z1, int z2) {
		Vector at = new Vector(blockX, blockY, blockZ);
//		Vector min = new Vector(x1, y1, z1);
//		Vector max = new Vector(x2, y2, z2);
//		generator.reportMessage("Partial paste: origin = " + at + " min = " + min + " max = " + max);

		try {
			EditSession editSession = getEditSession(generator);
			//editSession.setFastMode(true);
			place(editSession, getFacingIndex(facing), at, true, x1, x2, y1, y2, z1, z2);
		} catch (Exception e) {
			generator.reportException("[WorldEdit] Partial place schematic " + name + " at " + at + " failed", e);
			generator.reportMessage("Info: " + 
									" facing = " + facing + 
									" size = " + sizeX + ", " + sizeZ + 
									" chunk = " + chunkX + ", " + chunkZ + 
//									" origin = "+ blockX + ", " + blockY + ", " + blockZ + 
									" min = " + x1 + ", "+ y1 + ", "+ z1 + 
									" max = " + x2 + ", "+ y2 + ", "+ z2);

			e.printStackTrace();
		}
	}

	//TODO Pilfered from WorldEdit's CuboidClipboard... I need to remove this once the other Place function is used
	private void place(EditSession editSession, int facing, Vector pos, boolean noAir)
			throws MaxChangedBlocksException {
		for (int x = 0; x < sizeX; x++)
			for (int y = 0; y < sizeY; y++)
				for (int z = 0; z < sizeZ; z++) {
					if ((noAir) && (blocks[facing][x][y][z].isAir())) {
						continue;
					}
					editSession.setBlock(new Vector(x, y, z).add(pos),
							blocks[facing][x][y][z]);
				}
	}

	//TODO if WorldEdit ever gets this functionality I need to remove the modified code
	private void place(EditSession editSession, int facing, Vector pos, boolean noAir,
			int x1, int x2, int y1, int y2, int z1, int z2) throws MaxChangedBlocksException {
		x1 = Math.max(x1, 0);
		x2 = Math.min(x2, sizeX);
		y1 = Math.max(y1, 0);
		y2 = Math.min(y2, sizeY);
		z1 = Math.max(z1, 0);
		z2 = Math.min(z2, sizeZ);
		for (int x = x1; x < x2; x++)
			for (int y = y1; y < y2; y++)
				for (int z = z1; z < z2; z++) {
					if ((noAir) && (blocks[facing][x][y][z].isAir())) {
						continue;
					}
					editSession.setBlock(new Vector(x, y, z).add(pos), blocks[facing][x][y][z]);
				}
	}	
}
