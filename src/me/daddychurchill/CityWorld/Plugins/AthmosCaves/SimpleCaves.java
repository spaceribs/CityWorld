package me.daddychurchill.CityWorld.Plugins.AthmosCaves;

import me.daddychurchill.CityWorld.Support.XYZ;
import me.daddychurchill.CityWorld.WorldGenerator;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.08.13
 * Time: 13:24
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCaves implements CaveGenerator {

    private static long current_chunk = 0;
    private static HashMap<Long,Set> cache = new HashMap<Long, Set>();

    private long seed=1;

    private final void initRandom(long seed){
        this.seed = seed;
    }

    private final long randomLong() {
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        return seed;
    }

    private final int randomInt() {
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        return (int) seed;
    }

    @Override
    public boolean isCave(WorldGenerator generator, int blockX, int blockY, int blockZ) {
        int chunkX = blockX >> 4; // div16
        int chunkZ = blockZ >> 4;
        XYZ block = new XYZ(blockX,blockY,blockZ);
        long seed = generator.getWorldSeed();
        long localseed = (chunkX << 32) ^ chunkZ ^ seed;    // calculate a deterministic seed depending on worldseed and chunk
        localseed = localseed==0 ? 1 : localseed;           // seed should not be zero
        current_chunk = localseed;

        //==== Do we already have generated caves for this seed?
        if(cache.containsKey(localseed)){
            return cache.get(localseed).contains(block);
        }

        //==== Generate a few nodes and cut out a sphere around them
        initRandom(localseed);
        Set caves = new HashSet();
        int MAXNODES = 5;
        int RADIUS = 10;
        for(int i=0;i<MAXNODES;i++){
            XYZ node = new XYZ(randomInt()&0xF,randomInt()&0x3F,randomInt()&0xF);
            caves.addAll(makeSphere(node,RADIUS));
        }
        cache.put(localseed,caves);
        return caves.contains(block);  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Set<XYZ> makeSphere(XYZ p, int r){
        return makeSphere(p.x,p.y,p.z,r);
    }

    private Set<XYZ> makeSphere(int x, int y, int z, int r){
        HashSet<XYZ> blocks = new HashSet();
        for(int dx=-r;dx<r;dx++){
            for(int dy=-r;dy<r;dy++){
                for(int dz=-r;dz<r;dz++){
                   if(Math.sqrt(dx*dx+dz*dz+dy*dy)<=r){ //TODO there are faster algorithms
                       blocks.add(new XYZ(x+dx,y+dy,z+dz));
                   }
                }
            }
        }
        return blocks;
    }
}
