package de.superioz.cr.common.game.objects;

import de.superioz.library.java.util.list.ListUtil;
import de.superioz.library.minecraft.server.util.SerializeUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GamePlot {

    protected List<Location> locations;
    protected Location teleportPoint;

    public GamePlot(List<Location> locations, Location teleportPoint){
        this.locations = locations;
        this.teleportPoint = teleportPoint;
    }

    public List<Location> getLocations(){
        return locations;
    }

    public boolean isPart(Location foreignLoc){
        for(Location loc : locations){
            foreignLoc = new Location(foreignLoc.getWorld(), foreignLoc.getX(), 0, foreignLoc.getZ());

            if(foreignLoc.equals(loc))
                return true;
        }
        return false;
    }

    public Location getTeleportPoint(){
        return teleportPoint;
    }

    public static GamePlot fromString(String s){
        s = s.replace("[", "").replace("]", "");

        String[] array = s.split("#");
        String[] spawnPointArray = array[0].split(",");
        List<Location> spawnPoints = new ArrayList<>();

        for(String sp : spawnPointArray){
            Location loc = SerializeUtil.locFromString(sp);
            spawnPoints.add(loc);
        }

        return new GamePlot(spawnPoints, SerializeUtil.locFromString(array[1]));
    }

    @Override
    public String toString(){
        String[] spawnpoints = new String[this.locations.size()];
        for(int i = 0; i < spawnpoints.length; i++){
            spawnpoints[i] = SerializeUtil.toString(this.locations.get(i));
        }

        return "[" + ListUtil.insert(spawnpoints, ",") + "#" + SerializeUtil.toString(this.teleportPoint) + "]";
    }
}
