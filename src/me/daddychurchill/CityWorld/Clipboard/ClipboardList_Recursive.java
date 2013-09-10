package me.daddychurchill.CityWorld.Clipboard;

import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.PlatMap;
import me.daddychurchill.CityWorld.WorldGenerator;

import java.util.*;


public class ClipboardList_Recursive implements Iterable<Clipboard> {

	public ClipboardList_Recursive() {
		super();

		list = new HashMap<String, Clipboard>();
	}

	private HashMap<String, Clipboard> list;

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Clipboard get(String key) {
		return list.get(key);
	}

	public Clipboard put(Clipboard value) {
		list.put(value.name, value);
		return value;
	}

	@Override
	public Iterator<Clipboard> iterator() {
        return list.values().iterator();
	}

	public int count() {
		return list.size();
	}

	public void populate(WorldGenerator generator, PlatMap platmap) {



        //TODO Overhaul this!
		// grab platmap's random
		Odds odds = platmap.getOddsGenerator();



        // shuffle list, we don't want the same sequence over and over
        ArrayList<Clipboard> schems = new ArrayList<Clipboard>(list.values());
        Collections.shuffle(schems,new Random(odds.getRandomLong())); //FIXME potentially very slow with too many schems, better pick some at random

		// for each schematic
		for (Clipboard clip: schems) {

			// that succeeds the OddsOfAppearance
			if (odds.playOdds(clip.oddsOfAppearance)) {
				platmap.placeSpecificClip(generator, odds, clip);
			}
		}

	}

	public Clipboard getSingleLot(WorldGenerator generator, PlatMap platmap, Odds odds, int placeX, int placeZ) {

		// for each schematic
		for (Clipboard clip: this) {

			// that succeeds the OddsOfAppearance
			if (clip.chunkX == 1 && clip.chunkZ == 1 && odds.playOdds(clip.oddsOfAppearance))
				return clip;
		}
		
		// assume failure then
		return null;
	}
}
