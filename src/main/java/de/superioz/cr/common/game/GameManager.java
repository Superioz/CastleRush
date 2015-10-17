package de.superioz.cr.common.game;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.events.GamePlayersAmountChangeEvent;
import de.superioz.cr.main.CastleRush;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameManager {

    private static List<Game> runningGames = new ArrayList<>();

    public static void addGameInQueue(Game game){
        if(!runningGames.contains(game))
            runningGames.add(game);
    }

    public static void removeGameFromQueue(Game game){
        if(runningGames.contains(game))
            runningGames.remove(game);
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

    public static Game getGame(Player player){
        if(isIngame(player)){
            for(Game g : runningGames){
                if(g.getArena().getPlayers().contains(new WrappedGamePlayer(g, player)))
                    return g;
            }
        }
        return null;
    }

    public static boolean isIngame(Player player){
        return getGame(player) != null;
    }

    public enum State {

        LOBBY("lobby"),
        FULL("full"),
        INGAME("ingame"),
        WAITING("waiting");


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

        public Game(PlayableArena arena){
            this.arena = arena;
        }

        public PlayableArena getArena(){
            return arena;
        }

        public void join(Player player){
            WrappedGamePlayer wrappedGamePlayer = new WrappedGamePlayer(this, player);

            if(!arena.players.contains(wrappedGamePlayer))
                arena.players.add(wrappedGamePlayer);

            CastleRush.getPluginManager()
                    .callEvent(new GamePlayersAmountChangeEvent(this));
        }

        public void leave(Player player){
            WrappedGamePlayer wrappedGamePlayer = new WrappedGamePlayer(this, player);

            if(arena.players.contains(wrappedGamePlayer))
                arena.players.remove(wrappedGamePlayer);

            CastleRush.getPluginManager()
                    .callEvent(new GamePlayersAmountChangeEvent(this));
        }

        public void broadcast(String message){
            for(WrappedGamePlayer gp : getArena().getPlayers()){
                CastleRush.getChatMessager().send(message, gp.getPlayer());
            }
        }

        public void clear(Player player){
            player.setHealth(20D);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);

            clearInv(player);
        }

        public void clearInv(Player player){
            player.getInventory().setContents(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
        }

        public void prepareGame(){
            for(WrappedGamePlayer gamePlayer : getArena().getPlayers()){
                Player p = gamePlayer.getPlayer();

                clearInv(p);
                p.setGameMode(GameMode.CREATIVE);
            }
        }

        public void prepareNextState(){
            ItemKit itemKit = getArena().getArena().getItemKit();

            // Players teleport to his spawnpoint
            for(WrappedGamePlayer gamePlayer : getArena().getPlayers()){
                Player p = gamePlayer.getPlayer();

                clearInv(p);
                itemKit.setFor(p);
                p.setGameMode(GameMode.SURVIVAL);

                p.teleport(new WrappedGamePlayer(this, p).getPlot().getTeleportPoint());
            }

        }

    }

}
