package de.superioz.cr.common.game;

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
    protected static String world;

    public static final String SPLITERATOR = "#";

    public GamePlot(List<Location> locations, Location teleportPoint){
        this.locations = locations;
        this.teleportPoint = teleportPoint;

        if(teleportPoint != null && teleportPoint.getWorld() != null){
            world = teleportPoint.getWorld().getName();
        }
    }

    /**
     * Get every locations from this plot
     * @return List of locations
     */
    public List<Location> getLocations(){
        for(Location location : locations){
            location.setWorld(GameManager.getLocation(location, world).getWorld());
        }

        return locations;
    }

    /**
     * Checks if given location is part of the plot
     * @param foreignLoc The to-check location
     * @return The result
     */
    public boolean isPart(Location foreignLoc){
        for(Location loc : getLocations()){
            foreignLoc = new Location(foreignLoc.getWorld(), foreignLoc.getBlockX(), 0, foreignLoc.getBlockZ());

            if(foreignLoc.equals(loc))
                return true;
        }
        return false;
    }

    /**
     * The teleport point
     * @return The location
     */
    public Location getTeleportPoint(){
        return GameManager.getLocation(teleportPoint, world);
    }

    /**
     * Returns a gameplot from given string
     * @param s The string
     * @return The gameplot
     */
    public static GamePlot fromString(String s){
        s = s.replace("[", "").replace("]", "");

        String[] array = s.split(SPLITERATOR);
        String[] spawnPointArray = array[0].split(",");
        List<Location> spawnPoints = new ArrayList<>();

        for(String sp : spawnPointArray){
            Location loc = SerializeUtil.locFromString(sp);
            spawnPoints.add(loc);
        }
        world = spawnPointArray[0].split(";")[0];

        return new GamePlot(spawnPoints, SerializeUtil.locFromString(array[1]));
    }

    @Override
    public String toString(){
        String[] spawnpoints = new String[this.locations.size()];
        for(int i = 0; i < spawnpoints.length; i++){
            spawnpoints[i] = SerializeUtil.toString(getLocations().get(i));
        }

        return "[" + ListUtil.insert(spawnpoints, ",") + SPLITERATOR
                + SerializeUtil.toString(getTeleportPoint()) + "]";
    }
}
