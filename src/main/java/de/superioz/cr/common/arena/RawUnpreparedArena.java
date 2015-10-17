package de.superioz.cr.common.arena;

import de.superioz.cr.common.ItemKit;
import org.bukkit.Location;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class RawUnpreparedArena {

    protected String name;
    protected List<Location> spawnPoints;
    protected List<Location> rawGamePlots;
    protected List<Location> rawGameWalls;
    protected ItemKit itemKit;

    public RawUnpreparedArena(String name){
        this.name = name;
    }

    public void addSpawnpoint(Location loc){
        spawnPoints.add(loc);
    }

    public void addGamePlotLocation(Location loc){
        rawGamePlots.add(loc);
    }

    public void addGameWallsLocation(Location loc){
        rawGameWalls.add(loc);
    }

    public ItemKit getItemKit(){
        return itemKit;
    }

    public void setItemKit(ItemKit itemKit){
        this.itemKit = itemKit;
    }

    public void setName(String name){
        this.name = name;
    }

    public List<Location> getRawGamePlots(){
        return rawGamePlots;
    }

    public void setRawGamePlots(List<Location> rawGamePlots){
        this.rawGamePlots = rawGamePlots;
    }

    public List<Location> getSpawnPoints(){
        return spawnPoints;
    }

    public void setSpawnPoints(List<Location> spawnPoints){
        this.spawnPoints = spawnPoints;
    }

    public List<Location> getRawGameWalls(){
        return rawGameWalls;
    }

    public void setRawGameWalls(List<Location> rawGameWalls){
        this.rawGameWalls = rawGameWalls;
    }

    public String getName(){
        return name;
    }
}
