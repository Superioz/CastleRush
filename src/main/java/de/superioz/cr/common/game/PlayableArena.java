package de.superioz.cr.common.game;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.events.GameStateChangeEvent;
import de.superioz.cr.main.CastleRush;

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

    public void setGameState(GameManager.State gameState){
        this.gameState = gameState;

        // Call event that the gamestate has changed
        CastleRush.getPluginManager()
                .callEvent(new GameStateChangeEvent(GameManager.getGame(getArena()), gameState));
    }

    public int getMaxPlayers(){
        return getArena().getSpawnPoints().size();
    }

}
