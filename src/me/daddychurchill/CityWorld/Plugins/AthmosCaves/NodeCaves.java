package me.daddychurchill.CityWorld.Plugins.AthmosCaves;

import me.daddychurchill.CityWorld.Support.XYZ;
import me.daddychurchill.CityWorld.WorldGenerator;

import java.util.*;

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
public class NodeCaves implements CaveGenerator {
    private static final int CHUNKDIMENSIONS = 16;

    // Play with those constants to change generation:
    private static final int MINY=5; // Minimum height of a node
    private static final int MAXY=50; // maximum height of a node
    private static final int MINNODESPERCHUNK=0;
    private static final int MAXNODESPERCHUNK=4;
    private static final int NODECONNECTPROBABILITY=4;
    private static final int MAXNODESRADIUS = 4;

    //Memoisation
    private static Map<Long,Set> findNodesOfChunk = new HashMap<Long, Set>();


    private long seed;
    private Set<XYZ> caveBlocks = new HashSet<XYZ>();

    @Override
    public boolean isCave(WorldGenerator generator, int blockX, int blockY, int blockZ) {
        this.seed = generator.getWorldSeed();
        int chunkX = blockX/CHUNKDIMENSIONS;
        int chunkZ = blockZ/CHUNKDIMENSIONS;
        genCave(chunkX,chunkZ);
        return caveBlocks.contains(new XYZ(blockX,blockY,blockZ));  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void genCave(int chunkX,int chunkZ){
        //find all relevant neighbouring nodes
        Set<CaveNode> allNodes = findAllNeighbourNodes(chunkX,chunkZ,MAXNODESRADIUS);
        //find the nodes which should be connected by every node
        selectNeighbours(allNodes);
        //now I should connect the nodes and cut out blocks
        Set<XYZ> caves = new HashSet<XYZ>();
        for(CaveNode n : allNodes){
            for(CaveNode m : n.getNeighbours()){
                caves.addAll(FractTunnel.genTunnel(n,m));
            }
        }
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
        long localSeed = localSeed(this.seed,chunkX,chunkZ); // generate deterministic seed out of coordinates and worldseed
        if(findNodesOfChunk.containsKey(localSeed)){            // Memoisation, already randomised for that seed?
            return findNodesOfChunk.get(localSeed);
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

            findNodesOfChunk.put(localSeed,nodes);
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
        Set<CaveNode> nodes = new HashSet<CaveNode>();
        for(int x=-distance;x<distance;x++){
            for(int z=-distance;z<distance;z++){// max norm
                nodes.addAll(findNodesOfChunk(x,z)); // add nodes per chunk
            }
        }
        return nodes;
    }

    private void selectNeighbours(Set<CaveNode> nodes){
        for(CaveNode n : nodes){
            for(CaveNode m : nodes){
                if(m.getLocalseed()>n.getLocalseed()){ // only check for connections in one direction //TODO no connection to nodes in the same chunk!
                    Random rand = new Random(n.getLocalseed());
                    if(rand.nextInt(NODECONNECTPROBABILITY)==0){    // with a probability it will connect 2 nodes
                        n.addNeighbour(m);
                    }
                }
            }
        }
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
