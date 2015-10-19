package de.superioz.cr.common.arena;

import de.superioz.cr.common.ItemKit;
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
        super(name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
    }

    public boolean isFinished(){
        return getNotFinishedReason().equalsIgnoreCase("unknown");
    }

    public String getNotFinishedReason(){
        if((spawnPoints.size() < 2))
            return "too few spawnpoints";
        else if((gamePlots.size() < 2))
            return "too few gameplots";
        else if((gameWalls.size() < 1))
            return "too few gamewalls";
        else if(itemKit == null)
            return "itemkit equals null";
        else if(gamePlots.size() < spawnPoints.size())
            return "too few gameplots";
        else if(!ArenaManager.checkArenaName(name))
            return "invalid arenaname";
        else
            return "unknown";
    }


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

    public void setSpawnPoints(List<Location> spawnPoints){
        this.spawnPoints = spawnPoints;
    }

    public void addSpawnpoint(Location loc){
        this.spawnPoints.add(loc);
    }

    public void removeSpawnpoint(Location loc){
        this.spawnPoints.remove(loc);
    }

}
