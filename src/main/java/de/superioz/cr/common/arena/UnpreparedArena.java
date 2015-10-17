package de.superioz.cr.common.arena;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.GameWall;
import org.bukkit.Location;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class UnpreparedArena extends Arena {

    public UnpreparedArena(String name){
        super(name, null, null, null, null);
    }

    public boolean isFinished(){
        return !(!(spawnPoints.size() >= 2)
                || !(gamePlots.size() >= 2)
                || !(gameWalls.size() >= 2)
                || (itemKit == null)
                || !(gamePlots.size() >= spawnPoints.size())
                || !ArenaManager.checkArenaName(name));
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
