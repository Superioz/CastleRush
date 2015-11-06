package de.superioz.cr.common.arena.object;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GamePhaseChangeEvent;
import de.superioz.cr.common.events.GameStateChangeEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.division.GamePhase;
import de.superioz.cr.common.game.division.GameState;
import de.superioz.cr.main.CastleRush;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class PlayableArena {

    protected Arena arena;
    protected GameState gameState;
    protected GamePhase gamePhase;
    protected List<WrappedGamePlayer> players;
    protected ArenaSign sign;

    public PlayableArena(Arena arena, GameState gameState, GamePhase phase, Sign sign){
        this.arena = arena;
        this.gameState = gameState;
        this.gamePhase = phase;
        this.players = new ArrayList<>();
        this.sign = new ArenaSign(this, sign, this);
    }

    public List<WrappedGamePlayer> getPlayers(){
        return players;
    }

    public Arena getArena(){
        return arena;
    }

    public GameState getGameState(){
        return gameState;
    }

    public GamePhase getGamePhase(){
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase){
        this.gamePhase = gamePhase;

        CastleRush.getPluginManager().callEvent(new GamePhaseChangeEvent(
                GameManager.getGame(getArena()), gamePhase
        ));
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;

        // Call event that the gamestate has changed
        CastleRush.getPluginManager()
                .callEvent(new GameStateChangeEvent(GameManager.getGame(getArena()), gameState));
    }

    public int getMaxPlayers(){
        return getArena().getSpawnPoints().size();
    }

    public ArenaSign getSign(){
        return sign;
    }

    public void setPlayers(List<WrappedGamePlayer> players){
        this.players = players;
    }
}
