package de.superioz.cr.common.arena;

import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.GameWall;
import de.superioz.library.java.util.list.ListUtil;
import de.superioz.library.minecraft.server.util.SerializeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class Arena {

    protected String name;
    protected String spawnPointString;
    protected Location spawnPoint;
    protected List<GamePlot> gamePlots;
    protected List<GameWall> gameWalls;
    protected ItemKit itemKit;
    protected Sign sign;

    public static final String TEMPLATE_ATTACHMENT = "_template";
    public static final String DEFAULT_SPLITERATOR = "&";
    public static final String GAME_OBJECT_SPLITERATOR = "%";

    public Arena(String name, String loc, List<GamePlot> gamePlots, List<GameWall> gameWalls, ItemKit itemKit){
        this.gamePlots = gamePlots;
        this.gameWalls = gameWalls;
        this.itemKit = itemKit;
        this.name = name;
        this.spawnPointString = loc;
    }

    /**
     * Gets the world of this arena
     *
     * @return The world
     */
    public World getWorld(){
        return this.getSpawnPoint().getWorld();
    }

    /**
     * Checks if given player can join this arena
     *
     * @param player The player
     * @return The result as string
     */
    public String checkJoinable(Player player){
        World world = getWorld();

        if(world.getName().equals(player.getWorld().getName())){
            return "wrong world";
        }
        else if(!checkWorld()){
            return "wrong target world";
        }
        else if(ArenaManager.existInWorld(world, this)){
            return "world occupied";
        }
        else if(!hasTemplateBackup()){
            return "no backup";
        }
        return "";
    }

    /**
     * Checks if this world is ngiven world
     *
     * @param world The other world
     * @return The result as boolean
     */
    public boolean isWorld(World world){
        return this.getWorld()
                == world;
    }

    /**
     * Checks if given world is world, world_the_nether, world_the_end
     *
     * @param world The world
     * @return The result
     */
    public boolean checkWorld(World world){
        return world != Bukkit.getWorlds().get(0);
    }

    /**
     * Checks if this world is good to go
     *
     * @return Result as boolean
     */
    public boolean checkWorld(){
        if(isWorld(Bukkit.getWorlds().get(0))
                || isWorld(Bukkit.getWorlds().get(1))
                || isWorld(Bukkit.getWorlds().get(2))){
            return false;
        }

        for(GamePlot plot : getGamePlots()){
            if(!checkWorld(plot.getTeleportPoint().getWorld())){
                return false;
            }
        }

        for(GameWall wall : getGameWalls()){
            if(!checkWorld(wall.getBoundaries().getType1().getWorld())
                    || !checkWorld(wall.getBoundaries().getType2().getWorld())){
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the index of given plot
     *
     * @param plot The plot
     * @return The index
     */
    public int getIndex(GamePlot plot){
        for(int i = 0; i < getGamePlots().size(); i++){
            GamePlot plot1 = getGamePlots().get(i);

            if(plot1.equals(plot))
                return i;
        }
        return 0;
    }

    /**
     * Check if this map has a map-template for rollback
     *
     * @return Result as boolean
     */
    public boolean hasTemplateBackup(){
        return new File(Bukkit.getWorldContainer(), getWorld().getName() + TEMPLATE_ATTACHMENT).exists();
    }

    /**
     * Gets the spawnpoint
     *
     * @return The spawn as location
     */
    public Location getSpawnPoint(){
        if(this.spawnPoint == null){
            this.spawnPoint = SerializeUtil.locFromString(this.spawnPointString);
        }

        Location spawn = spawnPoint.clone();
        return new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
    }

    /**
     * Gets a plot from given location
     *
     * @param loc The location
     * @return The plot
     */
    public GamePlot getPlot(Location loc){
        for(GamePlot plot : getGamePlots()){
            if(plot.isPart(loc))
                return plot;
        }
        return null;
    }

    /**
     * Get an arena from string
     *
     * @param s The string
     * @return The arena
     */
    public static Arena fromString(String s){
        String[] array = s.split(DEFAULT_SPLITERATOR);
        String name = array[0];

        String spawnPointString = array[1];
        spawnPointString = spawnPointString.replace("[", "").replace("]", "");

        String gamePlotsString = array[2];
        gamePlotsString = gamePlotsString.replace("[", "").replace("]", "");

        String[] gamePlotsArray = gamePlotsString.split(GAME_OBJECT_SPLITERATOR);
        List<GamePlot> gamePlots = new ArrayList<>();

        for(String sp : gamePlotsArray){
            gamePlots.add(GamePlot.fromString(sp));
        }

        String gameWallsString = array[3];
        gameWallsString = gameWallsString.replace("[", "").replace("]", "");
        String[] gameWallsArray = gameWallsString.split(GAME_OBJECT_SPLITERATOR);
        List<GameWall> gameWalls = new ArrayList<>();

        for(String sp : gameWallsArray){
            gameWalls.add(GameWall.fromString(sp));
        }

        String itemKitString = array[4];
        ItemKit itemKit = ItemKit.fromString(itemKitString);

        return new Arena(name, spawnPointString, gamePlots, gameWalls, itemKit);
    }

    @Override
    public String toString(){
        String name = this.name;
        String spawnpoint = SerializeUtil.toString(getSpawnPoint());

        String[] gamePlots = new String[this.gamePlots.size()];
        for(int i = 0; i < gamePlots.length; i++){
            gamePlots[i] = this.gamePlots.get(i).toString();
        }

        String[] gameWalls = new String[this.gameWalls.size()];
        for(int i = 0; i < gameWalls.length; i++){
            gameWalls[i] = this.gameWalls.get(i).toString();
        }

        String gameKit = itemKit.toString();

        return name + DEFAULT_SPLITERATOR
                + "[" + spawnpoint + "]" + DEFAULT_SPLITERATOR
                + "[" + ListUtil.insert(gamePlots, GAME_OBJECT_SPLITERATOR) + "]" + DEFAULT_SPLITERATOR
                + "[" + ListUtil.insert(gameWalls, GAME_OBJECT_SPLITERATOR) + "]" + DEFAULT_SPLITERATOR
                + gameKit;
    }

    // -- Intern methods


    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSpawnPointString(){
        return spawnPointString;
    }

    public void setSpawnPointString(String spawnPointString){
        this.spawnPointString = spawnPointString;
    }

    public Sign getSign(){
        return sign;
    }

    public void setSign(Sign sign){
        this.sign = sign;
    }

    public ItemKit getItemKit(){
        return itemKit;
    }

    public void setItemKit(ItemKit itemKit){
        this.itemKit = itemKit;
    }

    public List<GameWall> getGameWalls(){
        return gameWalls;
    }

    public void setGameWalls(List<GameWall> gameWalls){
        this.gameWalls = gameWalls;
    }

    public List<GamePlot> getGamePlots(){
        return gamePlots;
    }

    public void setGamePlots(List<GamePlot> gamePlots){
        this.gamePlots = gamePlots;
    }

    public void setSpawnPoint(Location spawnPoint){
        this.spawnPoint = spawnPoint;
    }

}
