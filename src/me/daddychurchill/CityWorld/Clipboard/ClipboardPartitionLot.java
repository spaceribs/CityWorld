package me.daddychurchill.CityWorld.Clipboard;

import me.daddychurchill.CityWorld.CityWorld;
import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Support.Odds;
import me.daddychurchill.CityWorld.Support.PlatMap;
import me.daddychurchill.CityWorld.WorldGenerator;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 10.09.13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public class ClipboardPartitionLot {
    private int originChunkX;
    private int originChunkZ;
    private int sizeX;
    private int sizeZ;

    public ClipboardPartitionLot(int originChunkX, int originChunkZ, int sizeX, int sizeZ){
        this.originChunkX = originChunkX;
        this.originChunkZ = originChunkZ;
        this.sizeZ=sizeZ;
        this.sizeX=sizeX;
    }

    public void populate(WorldGenerator generator, PlatMap platmap,ClipboardList clipboardList){
        Odds odds = platmap.getOddsGenerator();
        if(odds.playOdds(.8)){ // try to place a schem FIXME only 50% chance is a bit lousy
            //place a schem
            List<Clipboard> schems = clipboardList.getAll(sizeX,sizeZ);
            CityWorld.log.info("Want to place schem: size="+this.sizeX + "/"+this.sizeZ);
            if(schems!=null&&schems.size()>0){
                CityWorld.log.info("Placing schem."); //+" schems="+schems.toString());
                platmap.placeSpecificClipAndCheck(generator, odds, schems.get(odds.getRandomInt(schems.size())), originChunkX, originChunkZ); //FIXME odds of appearance!
                return;
            }
        }

        if((sizeX<=1)&&(sizeZ<=1)){ //no more iterations
            return;
        }

        // Failed? partition into 2 sub lots
        if(sizeX>sizeZ){//if(sizeX>sizeZ){ // cut longer half, might prevent certain sizes to occure
            int cut = odds.getRandomInt(sizeX-1)+1;
            new ClipboardPartitionLot(originChunkX,originChunkZ,cut,sizeZ).populate(generator,platmap,clipboardList);
            new ClipboardPartitionLot(originChunkX+cut,originChunkZ,sizeX-cut,sizeZ).populate(generator,platmap,clipboardList);
        }else {
            int cut = odds.getRandomInt(sizeZ-1)+1;
            new ClipboardPartitionLot(originChunkX,originChunkZ,sizeX,cut).populate(generator,platmap,clipboardList); //FIXME endless loop!!!!
            new ClipboardPartitionLot(originChunkX,originChunkZ+cut,sizeX,sizeZ-cut).populate(generator,platmap,clipboardList);
        }
    }

}
