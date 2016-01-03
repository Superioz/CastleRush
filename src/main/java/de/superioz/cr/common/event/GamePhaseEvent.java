package de.superioz.cr.common.event;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GamePhase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GamePhaseEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private GamePhase gameState;

    public GamePhaseEvent(Game game, GamePhase newGamePhase){
        this.game = game;
        this.gameState = newGamePhase;
    }

    // -- Intern methods


    public Game getGame(){
        return game;
    }

    public GamePhase getGamePhase(){
        return gameState;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
