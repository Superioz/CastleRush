package de.superioz.cr.common.game;

import de.superioz.cr.common.arena.Arena;

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

    public static void joinGame(Arena arena){
        Game game = getGame(arena);
    }

    public static Game getGame(Arena arena){
        for(Game g : runningGames){
            if(g.getArena().getArena().getName().equalsIgnoreCase(arena.getName()))
                return g;
        }
        return null;
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

    public class Game {

        protected PlayableArena arena;

        public Game(PlayableArena arena){
            this.arena = arena;
        }

        public PlayableArena getArena(){
            return arena;
        }

    }

}
