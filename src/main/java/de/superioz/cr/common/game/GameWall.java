package de.superioz.cr.common.game;

import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.util.serialize.LocationSerializer;
import org.bukkit.Location;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameWall {

    protected SimplePair<Location, Location> boundaries;

    public GameWall(SimplePair<Location, Location> boundaries){
        this.boundaries = boundaries;
    }

    public SimplePair<Location, Location> getBoundaries(){
        return boundaries;
    }

    @Override
    public String toString(){
        return new LocationSerializer(boundaries.getType1()).serialize() + "-"
                + new LocationSerializer(boundaries.getType2()).serialize();
    }

    public static GameWall fromString(String s){
        System.out.println(s);
        String[] arr = s.split("-");

        return new GameWall(new SimplePair<>(new LocationSerializer(null).deserialize(arr[0])
            , new LocationSerializer(null).deserialize(arr[1])));
    }


}
