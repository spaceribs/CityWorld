package me.daddychurchill.CityWorld.Clipboard;

import me.daddychurchill.CityWorld.CityWorld;
import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Plats.RoadLot;
import me.daddychurchill.CityWorld.Support.PlatMap;
import me.daddychurchill.CityWorld.WorldGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 10.09.13
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class ClipboardExpandLot {
    private int originChunkX;
    private int originChunkZ;
    private int sizeX;
    private int sizeZ;
    private PlatMap platMap;

    /**
     *
     */
    public ClipboardExpandLot(int chunkX, int chunkZ, PlatMap platMap){
        sizeX=1;
        sizeZ=1;
        originChunkX = chunkX;
        originChunkZ = chunkZ;
        this.platMap = platMap;
    }

    public void populate(WorldGenerator generator, PlatMap platmap,ClipboardList clipboardList){
        this.platMap=platmap;
        expandLot(); // find biggest rectangle
        CityWorld.log.info("ExpandLot size="+this.sizeX+"/"+this.sizeZ);
        ClipboardPartitionLot partitionLot = new ClipboardPartitionLot(originChunkX,originChunkZ,sizeX,sizeZ);
        partitionLot.populate(generator,platmap,clipboardList);

    }

    private void expandLot(){
        boolean expanded;
        do{
            expanded = false;
            if(isExpandableXnegative()){
                sizeX++;
                originChunkX--;
                expanded=true;
            }

            if(isExpandableXpositive()){
                sizeX++;
                expanded=true;
            }

            if(isExpandableZnegative()){
                sizeZ++;
                originChunkZ--;
                expanded=true;
            }

            if(isExpandableZpositive()){
                sizeZ++;
                expanded=true;
            }

        }while (expanded);
    }


    private boolean isExpandableXpositive(){
        if(originChunkX+sizeX==PlatMap.Width) return false;
        int newX = originChunkX+sizeX+1;
        PlatLot lot;
        for(int i=originChunkZ;i<originChunkZ+sizeZ;i++){
            try {
                return !platMap.isExistingRoad(newX,i);
            }catch (IndexOutOfBoundsException e){ // out of bounds?
               return false;
            }
        }
        return true;
    }

    private boolean isExpandableXnegative(){
        if(originChunkX<=0) return false;
        int newX = originChunkX-1;
        PlatLot lot;
        for(int i=originChunkZ;i<originChunkZ+sizeZ;i++){
            try {
                return !platMap.isExistingRoad(newX,i);
            }catch (IndexOutOfBoundsException e){ // out of bounds?
                return false;
            }
        }
        return true;
    }

    private boolean isExpandableZpositive(){
        if(originChunkZ+sizeZ==PlatMap.Width) return false;
        int newZ = originChunkZ+sizeZ+1;
        PlatLot lot;
        for(int i=originChunkX;i<originChunkX+sizeX;i++){
            try {
                return !platMap.isExistingRoad(newZ,i);
            }catch (IndexOutOfBoundsException e){ // out of bounds?
                return false;
            }
        }
        return true;
    }

    private boolean isExpandableZnegative(){
        if(originChunkZ<=0) return false;
        int newZ = originChunkZ-1;
        PlatLot lot;
        for(int i=originChunkX;i<originChunkX+sizeX;i++){
            try {
                return !platMap.isExistingRoad(newZ,i);
            }catch (IndexOutOfBoundsException e){ // out of bounds?
                return false;
            }
        }
        return true;
    }



}
