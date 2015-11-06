package de.superioz.cr.common.events;

import de.superioz.cr.common.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameStartEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;

    public GameStartEvent(Game game){
        this.game = game;
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
