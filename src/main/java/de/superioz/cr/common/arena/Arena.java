package de.superioz.cr.common.arena;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.GameWall;
import de.superioz.library.java.util.list.ListUtils;
import de.superioz.library.minecraft.server.util.serialize.LocationSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class Arena {

    protected String name;
    protected List<Location> spawnPoints;
    protected List<GamePlot> gamePlots;
    protected List<GameWall> gameWalls;
    protected ItemKit itemKit;

    public Arena(String name, List<Location>
            spawnPoints, List<GamePlot> gamePlots, List<GameWall> gameWalls, ItemKit itemKit){
        this.gamePlots = gamePlots;
        this.gameWalls = gameWalls;
        this.itemKit = itemKit;
        this.name = name;
        this.spawnPoints = spawnPoints;
    }

    public List<GameWall> getGameWalls(){
        return gameWalls;
    }

    public ItemKit getItemKit(){
        return itemKit;
    }

    public List<GamePlot> getGamePlots(){
        return gamePlots;
    }

    public String getName(){
        return name;
    }

    public List<Location> getSpawnPoints(){
        return spawnPoints;
    }

    public String getPattern(int playersSize){
        return ChatColor.BLUE + "Size: " + playersSize;
    }

    @Override
    public String toString(){
        String name = this.name;
        String[] spawnpoints = new String[this.spawnPoints.size()];
        for(int i = 0; i < spawnpoints.length; i++){
            spawnpoints[i] = new LocationSerializer(this.spawnPoints.get(i)).serialize();
        }

        String[] gamePlots = new String[this.gamePlots.size()];
        for(int i = 0; i < gamePlots.length; i++){
            gamePlots[i] = this.gamePlots.get(i).toString();
        }

        String[] gameWalls = new String[this.gameWalls.size()];
        for(int i = 0; i < gameWalls.length; i++){
            gameWalls[i] = this.gameWalls.get(i).toString();
        }

        String gameKit = itemKit.toString();

        return name + "&"
                + "[" + ListUtils.insert(spawnpoints, ",") + "]" + "&"
                + "[" + ListUtils.insert(gamePlots, "%") + "]" + "&"
                + "[" + ListUtils.insert(gameWalls, "%") + "]" + "&"
                + gameKit;
    }

    public static Arena fromString(String s){
        String[] array = s.split("&");
        String name = array[0];

        String spawnPointString = array[1];
        spawnPointString = spawnPointString.replace("[", "").replace("]", "");
        String[] spawnPointArray = spawnPointString.split(",");
        List<Location> spawnPoints = new ArrayList<>();

        for(String sp : spawnPointArray){
            Location loc = new LocationSerializer(null).deserialize(sp);
            spawnPoints.add(loc);
        }

        String gamePlotsString = array[2];
        gamePlotsString = gamePlotsString.replace("[", "").replace("]", "");

        String[] gamePlotsArray = gamePlotsString.split("%");
        List<GamePlot> gamePlots = new ArrayList<>();

        for(String sp : gamePlotsArray){
            gamePlots.add(GamePlot.fromString(sp));
        }

        String gameWallsString = array[3];
        gameWallsString = gameWallsString.replace("[", "").replace("]", "");
        String[] gameWallsArray = gameWallsString.split("%");
        List<GameWall> gameWalls = new ArrayList<>();

        for(String sp : gameWallsArray){
            gameWalls.add(GameWall.fromString(sp));
        }

        String itemKitString = array[4];
        ItemKit itemKit = ItemKit.fromString(itemKitString);

        return new Arena(name, spawnPoints, gamePlots, gameWalls, itemKit);
    }

}
