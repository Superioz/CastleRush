package de.superioz.cr.common.game;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.events.GamePlayersAmountChangeEvent;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.WorldBackup;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.util.geometry.GeometryUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameManager {

    private static List<Game> runningGames = new ArrayList<>();
    public static List<Material> allowedBlocks = Arrays.asList(
            Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.LEVER);

    public static void addGameInQueue(Game game){
        if(!runningGames.contains(game)){
            runningGames.add(game);
            game.registerBackup();
        }
    }

    public static void removeGameFromQueue(Game game){

        if(runningGames.contains(game)){
            runningGames.remove(game);
            game.unregisterBackup();
        }
    }

    public static boolean containsGameInQueue(Arena arena){
        return getGame(arena) != null;
    }

    public static Game getGame(Arena arena){
        for(Game g : runningGames){
            if(g.getArena().getArena().getName().equalsIgnoreCase(arena.getName()))
                return g;
        }
        return null;
    }

    public static void stopArenas(){
        runningGames.forEach(GameManager.Game::stop);
    }

    public static Game getGame(Player player){
        if(isIngame(player)){
            for(Game g : runningGames){
                if(isIngame(player, g))
                    return g;
            }
        }
        return null;
    }

    public static boolean isIngame(Player player){
        for(Game g : runningGames){
            if(isIngame(player, g))
                return true;
        }
        return false;
    }

    public static boolean isIngame(Player player, Game game){
        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            Player p = pl.getPlayer();

            if(p.getUniqueId().equals(player.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    public static WrappedGamePlayer getWrappedGamePlayer(Player player){
        WrappedGamePlayer gp = null;

        if(!isIngame(player))
            return gp;

        Game game = getGame(player);
        assert game != null;

        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            Player p = pl.getPlayer();

            if(p.getUniqueId().equals(player.getUniqueId())){
                gp = pl;
            }
        }
        return gp;
    }

    public enum State {

        LOBBY(ChatColor.GREEN + "lobby"),
        INGAME(ChatColor.DARK_RED + "ingame"),
        WAITING(ChatColor.GOLD + "waiting");


        String n;

        State(String name){
            this.n = name;
        }

        public String getSpecifier(){
            return n.toUpperCase();
        }

    }

    public static class Game {

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

            if(!isIngame(player))
                arena.players.add(wrappedGamePlayer);

            CastleRush.getPluginManager()
                    .callEvent(new GamePlayersAmountChangeEvent(this));
        }

        public void leave(WrappedGamePlayer player){
            if(isIngame(player.getPlayer())){
                arena.players.remove(player);

                CastleRush.getPluginManager()
                        .callEvent(new GamePlayersAmountChangeEvent(this));
            }
        }

        public int getRandomIndex(){
            int i = getArena().getPlayers().size();

            if(i % 2 == 0){
                return 0;
            }
            else{
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
            this.getArena().setGameState(State.LOBBY);
        }

        public void leaveAll(){
            this.arena.players = new ArrayList<>();

            CastleRush.getPluginManager()
                    .callEvent(new GamePlayersAmountChangeEvent(this));
        }

        public void registerBackup(){
            this.backup = new WorldBackup(getArena().getArena()
                    .getSpawnPoints().get(0).getWorld());
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

                p.setGameMode(GameMode.SURVIVAL);
                gamePlayer.clearInventory();
                itemKit.setFor(p);

                p.teleport(gamePlayer.getPlot().getTeleportPoint());
            }

            timeStamp = System.currentTimeMillis();
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

}
