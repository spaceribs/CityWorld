package me.daddychurchill.CityWorld.Plugins.AthmosCaves;

import me.daddychurchill.CityWorld.Support.XYZ;
import me.daddychurchill.CityWorld.WorldGenerator;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.07.13
 * Time: 02:35
 * To change this template use File | Settings | File Templates.
 *
 * TODO:
 * - Link with Perlin/Fractal lines
 * - determine wich nodes to link
 * - use the ellipsoids
 * - tweak factors
 *
 */
public class NodeCaves {
    private static final int CHUNKDIMENSIONS = 16;

    // Play with those constants to change generation:
    private static final int MINY=5; // Minimum height of a node
    private static final int MAXY=50; // maximum height of a node
    private static final int MINNODESPERCHUNK=0;
    private static final int MAXNODESPERCHUNK=4;
    private static final int NODECONNECTPROBABILITY=4;
    private static final int MAXCONNECTPERNODE=6;
    private static final int MAXNODESRADIUS = 4;

    private static class TunnelSet{

        private CaveNode n1;
        private CaveNode n2;
        private Set<XYZ> blocks;
        public TunnelSet(CaveNode n1,CaveNode n2,Set<XYZ> blocks){
            this.n1=n1;
            this.n2=n2;
            this.blocks = blocks;
        }
        public TunnelSet(CaveNode n1,CaveNode n2){
            this.n1=n1;
            this.n2=n2;
        }

        @Override
        public int hashCode() {
            return (n1.hashCode()*991)^n2.hashCode();    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public boolean equals(Object obj) {
            if(obj==null) return false;
            if(obj instanceof TunnelSet){
                TunnelSet ts = (TunnelSet) obj;
                return ts.n1.equals(this.n1) && ts.n2.equals(this.n2);
            }
            return false;    //To change body of overridden methods use File | Settings | File Templates.
        }

        private Set<XYZ> getBlocks() {
            return blocks;
        }

        private void setBlocks(Set<XYZ> blocks) {
            this.blocks = blocks;
        }
    }

    private class TunnelTask implements Callable<TunnelSet>{
        private TunnelSet tunnel;

        public TunnelTask(TunnelSet tunnel){
            this.tunnel=tunnel;
        }

        @Override
        public TunnelSet call() throws Exception {
            log.info("generating tunnel");
            tunnel.setBlocks(FractTunnel.genTunnel(tunnel.n1, tunnel.n2));
            return  tunnel; //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private static NodeCaves _instance = new NodeCaves();

    //Memoisation
    private static Map<Long,Set> cached_findNodesOfChunk = new HashMap<Long, Set>();
    private static Map<TunnelSet,TunnelSet> cached_tunnels = new HashMap();



    public static boolean isCave(WorldGenerator generator, int blockX, int blockY, int blockZ){
        return _instance.calcIsCave(generator,blockX,blockY,blockZ);
    }

    private long seed;
    private Set<XYZ> cached_caveBlocks = new HashSet<XYZ>();
    private Logger log;

    private boolean calcIsCave(WorldGenerator generator, int blockX, int blockY, int blockZ) {
        log = generator.getPlugin().getLogger();
//        if(cached_caveBlocks.contains(new XYZ(blockX,blockY,blockZ))){
//            log.info("L1 hit");
//            return true;
//        }
        this.seed = generator.getWorldSeed();
        int chunkX = blockX/CHUNKDIMENSIONS;
        int chunkZ = blockZ/CHUNKDIMENSIONS;
        Set<XYZ> caves = genCave(chunkX,chunkZ);
        //cached_caveBlocks.addAll(caves);
        return caves.contains(new XYZ(blockX,blockY,blockZ));  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Set<XYZ> genCave(int chunkX,int chunkZ){
        //find all relevant neighbouring nodes
        Set<CaveNode> allNodes = findAllNeighbourNodes(chunkX,chunkZ,MAXNODESRADIUS);
        //find the nodes which should be connected by every node
        //log.info("selected neighbour nodes: "+nodes.size());
        //now I should connect the nodes and cut out blocks


        Set<XYZ> caves = new HashSet<XYZ>();
        int RADIUS = 2;
        for(CaveNode n : allNodes){
            for(int x=-5;x<6;x++){
                for(int y=-5;y<6;y++){
                    for(int z=-5;z<6;z++){
                        if(Math.sqrt(x*x+y*y+z*z)< RADIUS){
                            caves.add(new XYZ(x+n.x,y+n.y,z+n.z));
                        }
                    }
                }
            }
        }
//        ExecutorService executor = Executors.newCachedThreadPool();
//        CompletionService cs = new ExecutorCompletionService<TunnelSet>(executor);
//        List<Future<TunnelSet>> tunnelSets = new ArrayList<Future<TunnelSet>>();
//        TunnelSet ts;
//        for(CaveNode n : allNodes){
//
//            for(CaveNode m : n.getNeighbours()){
//                ts = new TunnelSet(n,m);
//                if(cached_tunnels.containsKey(ts)){
//                    caves.addAll(cached_tunnels.get(ts).getBlocks());
//                }else {
//                    //log.info(n.toString() + " gen tunnel ");
//
//                    Set<XYZ> oneTunnel = FractTunnel.genTunnel(n, m);
//                    tunnelSets.add(cs.submit(new TunnelTask(ts)));
//                }
//            }
//        }
//        executor.shutdown(); // wait for all caves to generate
//        try {
//            executor.awaitTermination(1000,TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            executor.shutdownNow();
//        }
//        for(Future<TunnelSet> f : tunnelSets){
//            try {
//                ts = f.get();
//                cached_tunnels.put(ts,ts);
//                caves.addAll(ts.getBlocks());
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (ExecutionException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
        return caves;
    }

    /**
     * Determines several points which mark nodes from
     * where tunnels connect other nodes
     * @param chunkX x Coordinate of a chunk
     * @param chunkZ z Coordinate of a chunk
     * @return set with all nodes in the particular chunk
     */
    private Set<CaveNode> findNodesOfChunk(int chunkX,int chunkZ){
        //XYZ chunk = new XYZ(chunkX,0,chunkZ);
        //log.info("findNodesOfChunk");
        long localSeed = localSeed(this.seed,chunkX,chunkZ); // generate deterministic seed out of coordinates and worldseed
        if(cached_findNodesOfChunk.containsKey(localSeed)){            // Memoisation, already randomised for that seed?
            //log.info("Cache hit");
            return cached_findNodesOfChunk.get(localSeed);
        }else {
            Set<CaveNode> nodes = new HashSet<CaveNode>();
            Random random = new Random(localSeed);

            int numberOfNodes = random.nextInt(MAXNODESPERCHUNK-MINNODESPERCHUNK)+MINNODESPERCHUNK;
            CaveNode node;
            int x,y,z;
            for(int i=0;i<numberOfNodes;i++){
                x=random.nextInt(CHUNKDIMENSIONS);
                z=random.nextInt(CHUNKDIMENSIONS);
                y=random.nextInt(MAXY-MINY)+MINY;
                node = new CaveNode(x,y,z,localSeed);
                nodes.add(node);
            }
            cached_findNodesOfChunk.put(localSeed,nodes);
            return nodes;
        }
    }

    /**
     * Returns a set of all relevant nodes for a given chunk and distance around that chunk
     * @param chunkX x coord of the chunk
     * @param chunkZ y coord of the chunk
     * @param distance maximum distance from the centered chunk (maximum norm used)
     * @return all nodes which are in question to intersect our chunk
     */
    private Set<CaveNode> findAllNeighbourNodes(int chunkX,int chunkZ, int distance){
        //log.info("findAllNeighbourNodes");
        Set<CaveNode> nodes = new HashSet<CaveNode>();
        for(int x=chunkX-distance;x<(chunkX+distance);x++){
            for(int z=chunkZ-distance;z<(distance+chunkZ);z++){// max norm
                nodes.addAll(findNodesOfChunk(x,z)); // add nodes per chunk
            }
        }
        nodes = selectNeighbours(nodes);
        return nodes;
    }

    private Set<CaveNode> selectNeighbours(Set<CaveNode> nodes){
        //log.info("selectNeighbours");
        for(CaveNode n:nodes){
            Random rand = new Random(n.getLocalseed()^(n.y << 16)); // nodes in same chunk shouldn't have same seed
            for(CaveNode m : nodes){
                if(n.getNeighbours().size()>=MAXCONNECTPERNODE) break;
                if(m.getLocalseed()>n.getLocalseed()){ // only check for connections in one direction //TODO no connection to nodes in the same chunk!
                    //log.info("Random: "+rand.nextInt(NODECONNECTPROBABILITY));
                    if(rand.nextInt(NODECONNECTPROBABILITY)==0){    // with a probability it will connect 2 nodes
                        log.info("made connection");
                        n.addNeighbour(m);
                    }
                }
            }
        }
        return  nodes;
    }


    /**
     * Generates a seed depending on a global seed and chunk coordinates
     * @param seed world seed
     * @param X chunk x coordinate
     * @param Z chunk z corrdinate
     * @return
     */
    private static final long localSeed(long seed, int X, int Z){
        return seed ^ X ^ (Z << 32);
    }
}
