package de.superioz.cr.common.game;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.PlayableArena;
import de.superioz.cr.common.event.GamePhaseEvent;
import de.superioz.cr.common.game.team.Team;
import de.superioz.cr.common.game.team.TeamColor;
import de.superioz.cr.common.game.team.TeamManager;
import de.superioz.cr.common.settings.GameSettings;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.common.timer.BuildCountdown;
import de.superioz.cr.common.timer.GameCountdown;
import de.superioz.cr.common.tool.GameMasterSettingsTool;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.PluginItems;
import de.superioz.cr.util.TimeType;
import de.superioz.cr.util.WorldBackup;
import de.superioz.library.main.SuperLibrary;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Getter
@Setter
public class Game {

    protected PlayableArena arena;
    protected long timeStamp;
    protected WorldBackup backup;
    public WrappedGamePlayer winner;
    public TeamManager teamManager;
    public WrappedGamePlayer gameMaster;
    public GameType type;
    public GameSettings settings;

    protected BuildCountdown buildCountdown;
    protected GameCountdown gameCountdown;
    protected GameScoreboard scoreboard;

    public Game(PlayableArena arena, GameType type){
        this.arena = arena;
        this.teamManager = new TeamManager(this);
        this.buildCountdown = new BuildCountdown(this);
        this.gameCountdown = new GameCountdown(this);
        this.scoreboard = new GameScoreboard(this);
        this.settings = new GameSettings();
        this.type = type;
    }

    /**
     * Let given player join the game
     * @param player The player
     * @param loc The game
     */
    public void join(Player player, Location loc){
        WrappedGamePlayer wrappedGamePlayer = new WrappedGamePlayer(this, player, loc, getRandomIndex());

        if(!GameManager.isIngame(player.getUniqueId())){
            if(arena.getCurrentPlayerSize() == 0){
                this.setGameMaster(wrappedGamePlayer);
            }
            arena.getPlayers().add(wrappedGamePlayer);
        }
    }

    /**
     * Let given player leave the game
     * @param player The player
     */
    public void leave(WrappedGamePlayer player){
        if(GameManager.isIngame(player.getUuid())){
            arena.getPlayers().remove(player);
            getTeamManager().remove(player);
        }
    }

    /**
     * Start the game
     */
    public void start(){
        SuperLibrary.callEvent(new GamePhaseEvent(this, GamePhase.LOBBY));
    }

    /**
     * Stop the game
     */
    public void stop(){
        SuperLibrary.callEvent(new GamePhaseEvent(this, GamePhase.FINISH));
    }

    /**
     * Gets a random index
     * @return The index
     */
    public int getRandomIndex(){
        int i = getArena().getPlayers().size();

        if(i % 2 == 0){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * Give gamemaster the tool
     */
    public void giveGameMasterTool(){
        getGameMaster().getPlayer().getInventory().setItem(GameMasterSettingsTool.SLOT,
                PluginItems.GAMEMASTER_SETTINGS_TOOL.getWrappedStack());
    }

    /**
     * Gets a new game master
     */
    public void newGameMaster(){
        setGameMaster(getArena().getPlayers().get(0));

        // Check game state
        if(getArena().getGameState() == GameState.LOBBY){
            giveGameMasterTool();

            // Send message
            if(getType() == GameType.PRIVATE){
                ChatManager.info().write(LanguageManager.get("youAreTheNewGamemaster"), getGameMaster().getPlayer());
            }
        }
    }

    /**
     * Gets the team for given plot
     * @param plot The plot
     * @return The team
     */
    public Team getTeam(GamePlot plot){
        for(WrappedGamePlayer gp : getArena().getPlayers()){
            if(gp.getPlot().equals(plot)){
                return gp.getTeam();
            }
        }
        return null;
    }

    /**
     * Clears the arena
     */
    public void leaveAll(){
        this.arena.setPlayers(new ArrayList<>());
        this.getTeamManager().reset();
    }

    /**
     * Registers the backup
     */
    public void registerBackup(){
        this.backup = new WorldBackup(this);
    }

    /**
     * Unregister backup
     */
    public void unregisterBackup(){
        new BukkitRunnable() {
            @Override
            public void run(){
                backup.rollback();
            }
        }.runTaskLater(CastleRush.getInstance(), 2L);
    }

    /**
     * Broadcasts a message to every player
     * @param message The message
     */
    public void broadcast(String message){
        getArena().getPlayers().stream().filter(gp ->
                !gp.hasLeft()).forEach(gp -> ChatManager.game().write(message, gp.getPlayer()));
    }

    /**
     * Checks if enough players are online
     * @return The result
     */
    public boolean enoughPlayers(){
        return getArena().getPlayers().size() >= 2;
    }

    /**
     * Checks if the game isnt in the given world
     * @param world The world
     * @return The result
     */
    public boolean inAnotherWorld(World world){
        return this.getArena().getArena().getWorld()
                != world;
    }

    /**
     * Checks if given player is the gamemaster
     * @param player The player
     * @return The result
     */
    public boolean isGamemaster(Player player){
        return getGameMaster().getUuid().equals(player.getUniqueId());
    }

    /**
     * Get min size of players
     * @return The size as integer
     */
    public int getMinSize(){
        return getTeamManager().getTeams().size();
    }

    /**
     * Check if player can join this game
     * @param player The playr
     * @return The result as string
     */
    public String checkJoinable(Player player){
        if(getArena().getPlayers().size() == TeamColor.values().length
                && !PluginSettings.TEAM_MODE){
            return "arena full";
        }

        String s = GameManager.checkInventory(player);
        if(!s.isEmpty())
            return s;

        return getArena().getArena().checkJoinable(player);
    }

    /**
     * Updates the world of this game
     */
    public void updateWorld(){
        new BukkitRunnable() {
            @Override
            public void run(){
                ArenaManager.loadAgain(getArena().getArena());
            }
        }.runTaskLater(CastleRush.getInstance(), 1L);
    }

    /**
     * Gets the world from this game
     * @return The world
     */
    public World getWorld(){
        return loadWorld(this.arena.getArena().getWorld());
    }

    /**
     * Load given world
     * @param world The world
     * @return The world (loaded)
     */
    public World loadWorld(World world){
        if(world == null){
            this.updateWorld();
            this.arena.setArena(ArenaManager.get(this.arena.getArena().getName()));
            return this.getWorld();
        }
        return world;
    }

    /**
     * Get array of time (hours,minutes,seconds)
     * @return The time
     */
    public String[] getTime(){
        int counter = 0;
        GamePhase phase = getArena().getGamePhase();

        if(phase == GamePhase.BUILD){
            counter = getBuildCountdown().getRepeater().getCounter();
        }
        else if(phase == GamePhase.CAPTURE){
            long timeStamp = System.currentTimeMillis();
            long oldTimeStamp = getTimeStamp();

            counter = (int) ((timeStamp-oldTimeStamp)/1000);
        }

        int seconds = counter % 60;
        int minutes = counter / 60;
        int hours = minutes / 60;

        String secondsString = seconds < 10 ? ""+0+seconds : seconds+"";
        String minutesString = minutes < 10 ? ""+0+minutes : minutes+"";
        String hoursString = hours < 10 ? ""+0+hours : hours+"";

        return new String[]{hoursString, minutesString, secondsString};
    }

    /**
     * Gets the time of this game
     * @param type The time type
     * @return The time
     */
    public String getTime(TimeType type){
        return getTime()[type.getIndex()];
    }

    /**
     * Set the gamephase
     * @param phase The phase
     */
    public void setPhase(GamePhase phase){
        SuperLibrary.callEvent(new GamePhaseEvent(this, phase));
    }

    /**
     * Check if this game is running
     * @return The result
     */
    public boolean isRunning(){
        return GameManager.getRunningGames().contains(this);
    }

    // -- Intern methodsW

    public GameType getType(){
        return type;
    }

    public void setGameMaster(WrappedGamePlayer gp){
        this.gameMaster = gp;
    }

    public WrappedGamePlayer getGameMaster(){
        return this.gameMaster;
    }

    public PlayableArena getArena(){
        return arena;
    }

    public void setArena(PlayableArena arena){
        this.arena = arena;
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    public WorldBackup getBackup(){
        return backup;
    }

    public void setBackup(WorldBackup backup){
        this.backup = backup;
    }

    public WrappedGamePlayer getWinner(){
        return winner;
    }

    public void setWinner(WrappedGamePlayer winner){
        this.winner = winner;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }

    public BuildCountdown getBuildCountdown(){
        return buildCountdown;
    }

    public void setBuildCountdown(BuildCountdown buildCountdown){
        this.buildCountdown = buildCountdown;
    }

    public GameCountdown getGameCountdown(){
        return gameCountdown;
    }

    public void setTeamManager(TeamManager teamManager){
        this.teamManager = teamManager;
    }

}
