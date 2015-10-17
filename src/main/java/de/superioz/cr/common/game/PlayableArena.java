package de.superioz.cr.common.game;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class PlayableArena {

    protected Arena arena;
    protected GameManager.State gameState;
    protected List<WrappedGamePlayer> players;

    public PlayableArena(Arena arena, GameManager.State gameState){
        this.arena = arena;
        this.gameState = gameState;
        this.players = new ArrayList<>();
    }

    public List<WrappedGamePlayer> getPlayers(){
        return players;
    }

    public Arena getArena(){
        return arena;
    }

    public GameManager.State getGameState(){
        return gameState;
    }

    public int getMaxPlayers(){
        return getArena().getSpawnPoints().size();
    }

}
