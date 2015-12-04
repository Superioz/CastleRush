package de.superioz.cr.common.game.objects;

import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.util.SerializeUtil;
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
        return SerializeUtil.toString(boundaries.getType1()) + "#"
                + SerializeUtil.toString(boundaries.getType2());
    }

    public static GameWall fromString(String s){
        String[] arr = s.split("#");

        return new GameWall(new SimplePair<>(SerializeUtil.locFromString(arr[0])
            , SerializeUtil.locFromString(arr[1])));
    }


}
