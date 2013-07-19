package me.daddychurchill.CityWorld.Plugins.AthmosCaves;

import FractCave.FractCave;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import me.daddychurchill.CityWorld.Support.ByteChunk;
import me.daddychurchill.CityWorld.Support.XYZ;
import me.daddychurchill.CityWorld.WorldGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 18.07.13
 * Time: 23:56
 * To change this template use File | Settings | File Templates.
 */
public class FractTunnel {

    private static final int CHUNKDIMENSIONS = 16;
    private int chunkX;
    private int chunkZ;
    private long seed;

    public static Set<XYZ> genTunnel(CaveNode n1, CaveNode n2) {
        Set<XYZ> tunnelBlocks = new HashSet<XYZ>();
        try {
            FractCave fractCave = new FractCave();

            int[] p1 = new int[3];
            int[] p2 = new int[3];

            p1[0] = n1.x;
            p1[1] = n1.y;
            p1[2] = n1.z;

            p2[0] = n2.x;
            p2[1] = n2.y;
            p2[2] = n2.z;

            double roughness = 0.3;
            int samples_exp = 7;

            Object[] output = new Object[1];
            Object[] input = new Object[4];
            input[0] = p1;
            input[1] = p2;
            input[2] = roughness;
            input[3] = samples_exp;
            fractCave.CaveGen(output,input);
            MWNumericArray m = (MWNumericArray) output[0];
            int[][] matrix = (int[][]) m.toIntArray();
            //System.out.println(Arrays.deepToString(matrix));
//            log.info("matlab returned");
            for(int i=0;i< matrix[0].length;i++){
                Set<XYZ> sphere = getSphere(matrix[0][i],matrix[1][i],matrix[2][i],4);
                //log.info("adding "+sphere.size() + " blocks");
                tunnelBlocks.addAll(sphere); //FIXME radius shouldn't be hardcoded...
            }

        } catch (MWException e) {
            System.out.println("Couln't instantiate matlab interface");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return tunnelBlocks;
    }


    public static Set<XYZ> getSphere(int x,int y, int z, int r) {
        Set<XYZ> blocks = new HashSet<XYZ>();
        for(int xi=x-r;xi<=(x+r);xi++){
            for(int yi=y-r;yi<=(y+r);yi++){
                for(int zi=z-r;zi<=(z+r);zi++){
                    if(norm(xi-x,yi-y,zi-z)<=r){
                        blocks.add(new XYZ(xi,yi,zi));
                    }
                }
            }
        }

        return blocks;

    }

    private static double norm(int x,int y, int z){
        return Math.sqrt(x*x+y*y+z*z);
    }

}
