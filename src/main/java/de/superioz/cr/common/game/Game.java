package de.superioz.cr.common.game;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.object.PlayableArena;
import de.superioz.cr.common.events.GamePlayersAmountChangeEvent;
import de.superioz.cr.common.game.division.GamePhase;
import de.superioz.cr.common.game.division.GameState;
import de.superioz.cr.common.game.objects.GameWall;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.WorldBackup;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.util.geometry.GeometryUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
        CastleRush.getPluginManager().registerEvents(this.backup, CastleRush.getInstance());
        this.backup.setFlag(true);
    }

    public void restoreBackup(){
        this.backup.restoreBlocks();
    }

    public void unregisterBackup(){
        this.backup.setFlag(false);
        this.restoreBackup();
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
        this.arena.getArena().getSpawnPoints().get(0).getWorld().setAutoSave(false);

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
        return this.getArena().getArena().getSpawnPoints().get(0).getWorld()
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

    public long getTimeStamp(){
        return timeStamp;
    }

}
