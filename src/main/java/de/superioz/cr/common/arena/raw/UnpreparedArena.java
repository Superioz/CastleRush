package de.superioz.cr.common.arena.raw;

import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.ItemKit;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.GameWall;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class UnpreparedArena extends Arena {

    public UnpreparedArena(String name){
        super(name, null, new ArrayList<>(), new ArrayList<>(), null);
    }

    /**
     * Check if this arena is finished
     *
     * @return Result as boolean
     */
    public boolean isFinished(){
        return getNotFinishedReason().equalsIgnoreCase("unknown");
    }

    /**
     * If not finished this method will return the reason
     *
     * @return The reason as string
     */
    public String getNotFinishedReason(){
        if(getSpawnPoint() == null)
            return "no spawnpoint";
        else if((gamePlots.size() < 2))
            return "too few gameplots";
        else if((gameWalls.size() < 1))
            return "too few gamewalls";
        else if(itemKit == null)
            return "itemkit equals null";
        else if(!ArenaManager.checkArenaName(name))
            return "invalid arenaname";
        else
            return "unknown";
    }

    /**
     * Check if the spawnpoint exists
     * @return The result
     */
    public boolean spawnPointExists(){
        return this.spawnPoint != null;
    }

    // -- Intern methods

    public void setGamePlots(List<GamePlot> gamePlots){
        this.gamePlots = gamePlots;
    }

    public void addGamePlot(GamePlot plot){
        this.gamePlots.add(plot);
    }

    public void removeGamePlot(GamePlot plot){
        this.gamePlots.remove(plot);
    }

    public void setGameWalls(List<GameWall> gameWalls){
        this.gameWalls = gameWalls;
    }

    public void addGameWall(GameWall wall){
        this.gameWalls.add(wall);
    }

    public void removeGameWall(GameWall wall){
        this.gameWalls.remove(wall);
    }

    public void setItemKit(ItemKit itemKit){
        this.itemKit = itemKit;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSpawn(Location loc){
        this.spawnPoint = loc;
    }

}
