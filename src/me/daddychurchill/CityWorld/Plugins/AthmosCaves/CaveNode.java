package me.daddychurchill.CityWorld.Plugins.AthmosCaves;

import me.daddychurchill.CityWorld.Support.XYZ;

import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 19.07.13
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
public class CaveNode extends XYZ{
    private long localseed;
    private List<CaveNode> neighbours;
    public CaveNode(int x,int y,int z,long localseed){
        this.x = x;
        this.y = y;
        this.z = z;
        this.localseed = localseed;
        neighbours = new Vector<CaveNode>();
    }
    public void addNeighbour(CaveNode node){
        neighbours.add(node);
    }

    public long getLocalseed() {
        return localseed;
    }

    public List<CaveNode> getNeighbours() {
        return neighbours;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof CaveNode){
            CaveNode n = (CaveNode) o;
            return super.equals(n)&&n.localseed==localseed; // if coordinates are equal, seed should be too
        }
        return false;
    }
    @Override
    public int hashCode(){
        return super.hashCode() ^ (int) ( localseed ^ (localseed>>>32));
    }
}