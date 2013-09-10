package me.daddychurchill.CityWorld.Clipboard;

import me.daddychurchill.CityWorld.Plats.PlatLot;
import me.daddychurchill.CityWorld.Support.PlatMap;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 10.09.13
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class ExpandLot {
    private int originChunkX;
    private int originChunkZ;
    private int sizeX;
    private int sizeZ;
    private PlatMap platMap;

    /**
     *
     */
    public ExpandLot(int chunkX, int chunkZ, PlatMap platMap){
        sizeX=1;
        sizeZ=1;
        originChunkX = chunkX;
        originChunkZ = chunkZ;
        this.platMap = platMap;
        expandLot(); // find biggest rectangle
    }

    private void expandLot(){
        boolean expanded = false;
        do{
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
        int newX = originChunkX+sizeX+1;
        PlatLot lot;
        for(int i=originChunkZ;i<originChunkZ+sizeZ;i++){
            try {
                lot = platMap.getMapLot(newX, i);
                if(lot!=null){ //lot free?
                    return false;
                }
            }catch (IndexOutOfBoundsException e){ // out of bounds?
               return false;
            }
        }
        return true;
    }

    private boolean isExpandableXnegative(){
        int newX = originChunkX-1;
        PlatLot lot;
        for(int i=originChunkZ;i<originChunkZ+sizeZ;i++){
            try {
                lot = platMap.getMapLot(newX, i);
                if(lot!=null){ //lot free?
                    return false;
                }
            }catch (IndexOutOfBoundsException e){ // out of bounds?
                return false;
            }
        }
        return true;
    }

    private boolean isExpandableZpositive(){
        int newZ = originChunkZ+sizeZ+1;
        PlatLot lot;
        for(int i=originChunkX;i<originChunkX+sizeX;i++){
            try {
                lot = platMap.getMapLot(newZ, i);
                if(lot!=null){ //lot free?
                    return false;
                }
            }catch (IndexOutOfBoundsException e){ // out of bounds?
                return false;
            }
        }
        return true;
    }

    private boolean isExpandableZnegative(){
        int newZ = originChunkZ-1;
        PlatLot lot;
        for(int i=originChunkX;i<originChunkX+sizeX;i++){
            try {
                lot = platMap.getMapLot(newZ, i);
                if(lot!=null){ //lot free?
                    return false;
                }
            }catch (IndexOutOfBoundsException e){ // out of bounds?
                return false;
            }
        }
        return true;
    }



}
