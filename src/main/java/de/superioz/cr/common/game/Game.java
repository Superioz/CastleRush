package de.superioz.cr.common.game;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.object.PlayableArena;
import de.superioz.cr.common.events.GamePlayersAmountChangeEvent;
import de.superioz.cr.common.game.division.GamePhase;
import de.superioz.cr.common.game.division.GameState;
import de.superioz.cr.common.game.objects.GameWall;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.WorldBackup;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.util.geometry.GeometryUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class Game {

    protected PlayableArena arena;
    protected long timeStamp;
    protected WorldBackup backup;

    public Game(PlayableArena arena){
        this.arena = arena;
    }

    public PlayableArena getArena(){
        return arena;
    }

    public void join(Player player, Location loc){
        WrappedGamePlayer wrappedGamePlayer = new WrappedGamePlayer(this, player, loc, getRandomIndex());

        if(!GameManager.isIngame(player))
            arena.getPlayers().add(wrappedGamePlayer);
        getWorld().setDifficulty(Difficulty.PEACEFUL);

        CastleRush.getPluginManager()
                .callEvent(new GamePlayersAmountChangeEvent(this));
    }

    public void leave(WrappedGamePlayer player){
        if(GameManager.isIngame(player.getPlayer())){
            arena.getPlayers().remove(player);

            CastleRush.getPluginManager()
                    .callEvent(new GamePlayersAmountChangeEvent(this));
        }
    }

    public int getRandomIndex(){
        int i = getArena().getPlayers().size();

        if(i % 2 == 0){
            return 0;
        }else{
            return 1;
        }
    }

    public void stop(){
        this.setWalls(Material.AIR);

        for(WrappedGamePlayer p : getArena().getPlayers()){
            if(p == null)
                continue;
            p.teleport(p.getJoinLocation());
        }

        this.leaveAll();
        this.getArena().setGameState(GameState.LOBBY);
    }

    public void leaveAll(){
        this.arena.setPlayers(new ArrayList<>());

        CastleRush.getPluginManager()
                .callEvent(new GamePlayersAmountChangeEvent(this));
    }

    public void registerBackup(){
        this.backup = new WorldBackup(this);
    }

    public void unregisterBackup(){
        this.backup.rollback();
    }

    public void broadcast(String message){
        for(WrappedGamePlayer gp : getArena().getPlayers()){
            CastleRush.getChatMessager().send(message, gp.getPlayer());
        }
    }

    public boolean enoughPlayers(){
        return getArena().getPlayers().size() >= 2;
    }

    public void prepareGame(){
        World world = getWorld();
        world.setAutoSave(false);
        world.setTime(0);

        for(WrappedGamePlayer gamePlayer : getArena().getPlayers()){
            Player p = gamePlayer.getPlayer();

            gamePlayer.clearInventory();
            p.setGameMode(GameMode.CREATIVE);
        }
    }

    public void prepareNextState(){
        ItemKit itemKit = getArena().getArena().getItemKit();

        // Players teleport to his spawnpoint
        for(WrappedGamePlayer gamePlayer : getArena().getPlayers()){
            Player p = gamePlayer.getPlayer();

            gamePlayer.clear();
            gamePlayer.clearInventory();
            itemKit.setFor(p);

            p.teleport(gamePlayer.getPlot().getTeleportPoint());
        }

        timeStamp = System.currentTimeMillis();

        // Call event
        getArena().setGamePhase(GamePhase.CAPTURE);
    }

    public boolean inAnotherWorld(World world){
        return this.getArena().getArena().getWorld()
                != world;
    }

    public void setWalls(String name){
        Material mat = Material.getMaterial(name);

        if(mat == null)
            return;

        for(GameWall wall : getArena().getArena().getGameWalls()){
            SimplePair<Location, Location> boundaries = wall.getBoundaries();

            for(Location l : GeometryUtils.cuboid(boundaries.getType1(), boundaries.getType2())){
                l.getBlock().setType(mat);
            }
        }

    }

    public void setWalls(Material mat){
        if(mat == null)
            return;

        for(GameWall wall : getArena().getArena().getGameWalls()){
            SimplePair<Location, Location> boundaries = wall.getBoundaries();

            for(Location l : GeometryUtils.cuboid(boundaries.getType1(), boundaries.getType2())){
                l.getBlock().setType(mat);
            }
        }

    }

    public String checkJoinable(Player player){
        return getArena().getArena().checkJoinable(player);
    }

    public void updateWorld(){
        ArenaManager.loadAgain(this.getArena().getArena());
    }

    public long getTimeStamp(){
        return timeStamp;
    }

    public World getWorld(){
        World world = this.arena.getArena().getWorld();

        if(world == null){
            ArenaManager.loadAgain(this.arena.getArena());
            this.arena.setArena(ArenaManager.get(this.arena.getArena().getName()));
            return this.getWorld();
        }
        return world;
    }

}
