package de.superioz.cr.common.game;

import de.superioz.library.bukkit.util.SerializeUtil;
import de.superioz.library.java.util.classes.SimplePair;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameWall {

    protected SimplePair<Location, Location> boundaries;
    protected static String world;

    public static final String SPLITERATOR = "#";

    public GameWall(SimplePair<Location, Location> boundaries){
        this.boundaries = boundaries;

        if(boundaries != null && boundaries.getType1() != null
                && boundaries.getType1().getWorld() != null){
            world = boundaries.getType1().getWorld().getName();
        }
    }

    /**
     * Reloads the world of every boundaries
     */
    public void reloadBoundaries(){
        Location t1 = boundaries.getType1();
        Location t2 = boundaries.getType2();
        t1.setWorld(Bukkit.getWorld(world));
        t2.setWorld(Bukkit.getWorld(world));

        boundaries.setType1(t1);
        boundaries.setType2(t2);
    }

    /**
     * Gets the gamewall from given string
     * @param s The string
     * @return The gamewall
     */
    public static GameWall fromString(String s){
        String[] arr = s.split(SPLITERATOR);
        world = arr[0].split(";")[0];

        return new GameWall(new SimplePair<>(SerializeUtil.locFromString(arr[0])
            , SerializeUtil.locFromString(arr[1])));
    }

    @Override
    public String toString(){
        return SerializeUtil.toString(GameManager.getLocation(boundaries.getType1(), world)) + SPLITERATOR
                + SerializeUtil.toString(GameManager.getLocation(boundaries.getType2(), world));
    }

    // -- Intern methods

    public SimplePair<Location, Location> getBoundaries(){
        return boundaries;
    }

    public void setBoundaries(SimplePair<Location, Location> boundaries){
        this.boundaries = boundaries;
    }

}
