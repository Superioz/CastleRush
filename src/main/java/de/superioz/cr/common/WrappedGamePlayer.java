package de.superioz.cr.common;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.team.Team;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.common.stats.PlayerStats;
import de.superioz.cr.common.stats.StatsManager;
import de.superioz.library.minecraft.server.common.view.SuperScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class WrappedGamePlayer {

    protected UUID uuid;
    protected Game game;
    protected Location joinLocation;
    protected int index;
    protected String displayName;
    protected SuperScoreboard scoreboard;

    public WrappedGamePlayer(Game game, Player player, Location joinLocation, int index){
        this.game = game;
        this.uuid = player.getUniqueId();
        this.joinLocation = joinLocation;
        this.index = index;
        this.displayName = player.getDisplayName();
    }

    /**
     * Gets the plot of this player
     * @return The plot
     */
    public GamePlot getPlot(){
        int index = getIndex();
        List<GamePlot> plots = getGame().getArena().getArena().getGamePlots();

        if(index > plots.size() - 1
                || index < 0)
            return null;

        return plots.get(index);
    }

    /**
     * Get the plot where the player is standing on
     * @return The plot
     */
    public GamePlot getPlotStandingOn(){
        for(GamePlot plot : getGame().getArena().getArena().getGamePlots()){
            if(plot.isPart(getPlayer().getLocation())){
                return plot;
            }
        }
        return null;
    }

    /**
     * Get index of team
     * @return The index
     */
    public int getIndex(){
        return getGame().getTeamManager().getIndex(getTeam());
    }

    /**
     * Get spawn location
     * @return The location
     */
    public Location getSpawnLocation(){
        return getGame().getArena().getArena().getSpawnPoint();
    }

    /**
     * Gets the bukkit player
     * @return The player
     */
    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Clears the inventory
     */
    public void clearInventory(){
        getPlayer().getInventory().clear();
        this.clearArmor();
    }

    /**
     * Clears only the armor
     */
    public void clearArmor(){
        Player p = getPlayer();

        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.getInventory().setBoots(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
    }

    public void clear(){
        Player player = getPlayer();

        player.setHealth(20D);
        player.setFoodLevel(20);

        this.setGameMode(GameMode.SURVIVAL);
        this.clearInventory();
    }

    /**
     * Gets the team of this player
     * @return The team
     */
    public Team getTeam(){
        return getGame().getTeamManager().get(this);
    }

    /**
     * Get team mates of this player
     * @return The list of all mates
     */
    public List<WrappedGamePlayer> getTeamMates(){
        if(getTeam() == null)
            return new ArrayList<>();

        return getTeam().getTeamPlayer();
    }

    /**
     * Get the names of his team
     * @return The list of names
     */
    public List<String> getTeamMatesNames(){
        return getTeamMates().stream().map(gp -> gp.getPlayer().getDisplayName())
                .collect(Collectors.toList());
    }

    /**
     * Sets the gamemode of this player
     * @param mode The gamemode
     */
    public void setGameMode(GameMode mode){
        getPlayer().setGameMode(mode);
    }

    /**
     * Get world of this player
     * @return The world
     */
    public World getWorld(){
        return getPlayer().getWorld();
    }

    /**
     * Teleport this player to given location
     * @param location The location to teleport
     */
    public void teleport(Location location){
        getPlayer().teleport(location);
    }

    /**
     * Checks if the player left the game during game
     * @return The result as boolean
     */
    public boolean hasLeft(){
        return GameManager.getLeftPlayer().containsKey(getUuid());
    }

    /**
     * Checks if the player are allowed to rejoin the game after he left it
     * @return The result
     */
    public boolean rejoin(){
        if(!GameManager.getLeftPlayer().containsKey(getPlayer().getUniqueId())){
            return false;
        }

        long old = GameManager.getLeftPlayer().get(getPlayer().getUniqueId()).getObject2();
        long current = System.currentTimeMillis();
        long diff = current - old;
        long allowedDiff = PluginSettings.REJOIN_TIME * 1000;

        return old != -1 && diff < allowedDiff;
    }

    /**
     * Reset his scoreboard
     */
    public void resetScoreboard(){
        getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    /**
     * Update his scoreboard
     */
    public void updateScoreboard(){
        if(getPlayer() != null
                && getPlayer().isOnline()){
            getScoreboard().show(getPlayer());
        }
    }

    /**
     * Get the stats of this player
     * @return The stats as playerstats object
     */
    public PlayerStats getStats(){
        if(!StatsManager.contains(getUuid())){
            StatsManager.add(getPlayer());
        }

        return StatsManager.getStats(getUuid());
    }

    /**
     * Resets the behaviour like clearing the inv and teleport back to spawn
     */
    public void reset(){
        clear();
        teleport(getJoinLocation());
        resetScoreboard();
    }

    /**
     * Checks if the player is on his plot
     * @return The result
     */
    public boolean isOnPlot(){
        return getPlot().isPart(getPlayer().getLocation());
    }

    /**
     * Update the stats of this player
     * @param newStats The new stats
     */
    public void updateStats(PlayerStats newStats){
        StatsManager.updateStats(newStats);
    }

    // -- Intern methods

    public Game getGame(){
        return game;
    }

    public Location getJoinLocation(){
        return joinLocation;
    }

    public SuperScoreboard getScoreboard(){
        return scoreboard;
    }

    public void setScoreboard(SuperScoreboard scoreboard){
        this.scoreboard = scoreboard;
    }

    public UUID getUuid(){
        return uuid;
    }

    public String getDisplayName(){
        return displayName;
    }

}
