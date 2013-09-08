package me.daddychurchill.CityWorld.Plugins.WorldEdit;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.CuboidClipboard.FlipDirection;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import me.daddychurchill.CityWorld.Clipboard.Clipboard;
import me.daddychurchill.CityWorld.Support.DecayOption;
import me.daddychurchill.CityWorld.Support.Direction;
import me.daddychurchill.CityWorld.Support.RealChunk;
import me.daddychurchill.CityWorld.WorldGenerator;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clipboard_WorldEdit_Facing extends Clipboard {

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
//    private final static String tagDecayOptionFullThreshold = "DecayFullThreshold";
//    private final static String tagDecayOptionPartialThreshold = "DecayPartialThreshold";
//    private final static String tagDecayOptionLeavesThreshold = "DecayLeavesThreshold";
//    private final static String tagDecayOptionHoleScale = "DecayHoleScale";
//    private final static String tagDecayOptionLeavesScale = "DecayLeavesScale";
    private final static String tagDecayIntensity = "DecayIntensity";
    //private final static String tagEntranceFacing = "EntranceFacing";

    private final static String ext_south = "_south";
    private final static String ext_west = "_west";
    private final static String ext_north = "_north";
    private final static String ext_east = "_east";
    private final static String fileext = ".schematic";


	public Clipboard_WorldEdit_Facing(WorldGenerator generator, File file) throws Exception {
		super(generator, file);
	}

    /**
     * Checks if the filename has the 'pre-rotated schem' file ending
     * FIXME
     * @param filename
     * @return
     */
    public static boolean isPrerotated(String filename){
        String regex = "(.*)_(south|west|north|east).schematic$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }

    /**
     * Checks if the filename has the 'pre-rotated south schem' file ending
     * FIXME
     * @param filename
     * @return
     */
    public static boolean isPrerotatedSouth(String filename){
        String regex = "(.*)_south.schematic$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }
	
	@Override
	protected void load(WorldGenerator generator, File schemfile) throws Exception {
		// FIXME Ugly hack

        String schemname = schemfile.getAbsolutePath();
        String regex = "(.*)_south.schematic$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(schemname);
        matcher.find();
        String basePath = matcher.group(1);

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
        // DecayOptions, Thresholds
        metaYaml.addDefault(tagDecayIntensity, DecayOption.getDefaultDecayIntensity());
		
		// start reading it
		File metaFile = new File(basePath + metaExtension);
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

            //Decay Options
            double intensity = metaYaml.getDouble(tagDecayIntensity);
            decayOptions = new DecayOption(intensity);
		}

        // try and save the meta data if we can
        try {
            metaYaml.save(metaFile);
        } catch (IOException e) {
            // we can recover from this... so eat it!
            generator.reportException("[WorldEdit] Could not resave " + metaFile.getAbsolutePath(), e);
        }

        facingCount = 4; // TODO we assume all 4 files exsist...
        for(int facing=0;facing<4;facing++){
            String filename = basePath;
            switch (facing){
                case 0:
                    filename = basePath+ext_south+fileext;
                    break;
                case 1:
                    filename = basePath+ext_west+fileext;
                    break;
                case 2:
                    filename = basePath+ext_north+fileext;
                    break;
                case 3:
                    filename = basePath+ext_east+fileext;
                    break;
            }

            // load the actual blocks
            CuboidClipboard cuboid= null;
            try {
                File file = new File(filename);
                cuboid = SchematicFormat.getFormat(file).load(file);
            }catch (Exception e){
                generator.reportException("Can't load pre-rotated schems.",e);
                continue;
            }

            // how big is it?
            sizeX = cuboid.getWidth();
            sizeZ = cuboid.getLength();
            sizeY = cuboid.getHeight();

            if(sizeX!=sizeZ){
                throw new RuntimeException("Schematic is not quadratic! But has pre-rotated file extensions");
            }
            // grab the edge block
            BaseBlock edge = cuboid.getPoint(new Vector(0, groundLevelY, 0));
            edgeType = Material.getMaterial(edge.getType());
            edgeData = (byte) edge.getData(); //TODO I think that data can be integers... one of these days
            //edgeData = (byte)((edge.getData() & 0x000000ff)); // this would make overflows not error out but let's not do that
            edgeRise = generator.oreProvider.surfaceId == edgeType.getId() ? 0 : 1;


            //TODO Validate the size
            if(facing==0){ //only allocate the first time (better do some loop unfolding, but meh)
                //TODO we should allocate only facing count, then allocate the size based on what comes out of the rotation.. once I do rotation
                // allocate room
                blocks = new BaseBlock[facingCount][sizeX][sizeY][sizeZ];
            }

            // copy the cubes for each direction
            copyCuboid(cuboid, facing);
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
