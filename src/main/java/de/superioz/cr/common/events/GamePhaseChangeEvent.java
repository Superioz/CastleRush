package de.superioz.cr.common.events;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.division.GamePhase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GamePhaseChangeEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private GamePhase gameState;

    public GamePhaseChangeEvent(Game game, GamePhase newGamePhase){
        this.game = game;
        this.gameState = newGamePhase;
    }

    public GamePhase getGamePhase(){
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
