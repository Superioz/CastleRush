package de.superioz.cr.common.events;

import de.superioz.cr.common.game.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameManager.Game game;
    private GameManager.State gameState;

    public GameStateChangeEvent(GameManager.Game game, GameManager.State newGameState){
        this.game = game;
        this.gameState = newGameState;
    }

    public GameManager.State getGameState(){
        return gameState;
    }

    public GameManager.Game getGame(){
        return game;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
