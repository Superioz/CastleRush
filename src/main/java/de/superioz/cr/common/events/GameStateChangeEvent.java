package de.superioz.cr.common.events;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.division.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameStateChangeEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private GameState gameState;

    public GameStateChangeEvent(Game game, GameState newGameState){
        this.game = game;
        this.gameState = newGameState;
    }

    public GameState getGameState(){
        return gameState;
    }

    public Game getGame(){
        return game;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
