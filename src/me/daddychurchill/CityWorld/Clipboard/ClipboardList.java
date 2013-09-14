package me.daddychurchill.CityWorld.Clipboard;

import java.util.*;

import me.daddychurchill.CityWorld.WorldGenerator;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.PlatMap;
import org.apache.commons.lang.NotImplementedException;


public class ClipboardList {

    private class Size{
        private int chunkSizeX;
        private int chunkSizeZ;

        private Size(int chunkSizeX, int chunkSizeZ) {
            this.chunkSizeX = chunkSizeX;
            this.chunkSizeZ = chunkSizeZ;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Size size = (Size) o;

            if (chunkSizeX != size.chunkSizeX) return false;
            if (chunkSizeZ != size.chunkSizeZ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = chunkSizeX;
            result = 31 * result + chunkSizeZ;
            return result;
        }
    }

    private HashMap<Size, List<Clipboard>> listOfAll;

	public ClipboardList() {
		super();
		
		listOfAll = new HashMap();
	}
	

	
	public boolean isEmpty() {
		return listOfAll.isEmpty();
	}
    /**
     * Returns all clipboards with a X*Z size
     * @param chunkSizeX
     * @param chunkSizeZ
     * @return list of clipboards or null if none available
     */
	public List<Clipboard> getAll(int chunkSizeX,int chunkSizeZ) {
		return listOfAll.get(new Size(chunkSizeX,chunkSizeZ));
	}
	
	public Clipboard put(Clipboard value) {
		Size size = new Size(value.chunkX,value.chunkZ);
        List<Clipboard> group = listOfAll.get(size);
        if(group==null){ // not initialized yet?
            group = new LinkedList<Clipboard>();
        }
        group.add(value);
        listOfAll.put(size,group);

		return value;
	}

//	@Override
//	public Iterator<Clipboard> iterator() {
//        return listOfAll.values().iterator();
//	}
	
	public int count() {
		return listOfAll.size();
	}

	public void populate(WorldGenerator generator, PlatMap platmap) {

        //TODO Overhaul this!
		// grab platmap's random
		Odds odds = platmap.getOddsGenerator();


        // shuffle listOfAll, we don't want the same sequence over and over
        //ArrayList<Clipboard> schems = new ArrayList<Clipboard>(listOfAll.values());
        //Collections.shuffle(schems,new Random(odds.getRandomLong())); //FIXME potentially very slow with too many schems, better pick some at random

		// for each schematic

        for (int x = 0; x < PlatMap.Width; x++) {
            for (int z = 0; z < PlatMap.Width; z++) {
                if(platmap.isEmptyLot(x,z)){
                    new ClipboardExpandLot(x,z,platmap).populate(generator,platmap,this);
                }
            }
        }

	}

	public Clipboard getSingleLot(WorldGenerator generator, PlatMap platmap, Odds odds, int placeX, int placeZ) {
        List<Clipboard> group = listOfAll.get(new Size(1,1));
		return group.get(odds.getRandomInt(group.size()));
	}
}
