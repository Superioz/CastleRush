package de.superioz.cr.common.arena.raw;

import de.superioz.cr.common.arena.ItemKit;
import de.superioz.cr.common.cache.EditorCache;
import de.superioz.library.java.util.classes.SimplePair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
@Setter
@Getter
public class RawUnpreparedArena {

    protected String name;

    protected List<Location> rawGamePlots;
    protected Location rawGamePlotMarker;

    protected SimplePair<Location, Location> rawGameWalls;
    protected ItemKit itemKit;

    public RawUnpreparedArena(String name){
        this.name = name;
        this.rawGamePlots = new ArrayList<>();
        this.rawGamePlotMarker = null;
        this.rawGameWalls = new SimplePair<>(null, null);
        this.itemKit = null;
    }

    /**
     * Adds location to plot
     *
     * @param loc Location
     *
     * @return Result as boolean
     */
    public boolean addGamePlotLocation(Location loc){
        if(plotPosContains(loc)){
            return false;
        }

        Block b = loc.getBlock();
        rawGamePlots.add(new Location(b.getWorld(), b.getX(), 0, b.getZ()));
        return true;
    }

    /**
     * Checks if the plot contains given location
     *
     * @param loc The location
     *
     * @return The result as boolean
     */
    public boolean plotPosContains(Location loc){
        Block b = loc.getBlock();
        return getRawGamePlots().contains(new Location(loc.getWorld(), b.getX(), 0, b.getZ()));
    }

    /**
     * Checks if this arena is finished
     *
     * @return The result as boolean
     */
    public boolean isFinished(){
        return (getName() != null)
                && (!getName().isEmpty());
    }

    /**
     * Checks if given loc is in the world of the spawn
     * @param loc The location
     * @return The result
     */
    public boolean checkLocation(Player player, Location loc){
        UnpreparedArena unpreparedArena = EditorCache.getLast(player);

        return unpreparedArena != null
                && unpreparedArena.spawnPointExists()
                && loc.getWorld().getName().equals(unpreparedArena.getSpawnPoint().getWorld().getName());
    }

}
