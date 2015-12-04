package de.superioz.cr.common.events;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameFinishEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private WrappedGamePlayer winner;

    public GameFinishEvent(Game game, WrappedGamePlayer winner){
        this.game = game;
        this.winner = winner;
    }

    public WrappedGamePlayer getWinner(){
        return winner;
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
