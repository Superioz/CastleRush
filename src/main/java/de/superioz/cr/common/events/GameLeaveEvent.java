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
public class GameLeaveEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private WrappedGamePlayer player;

    public GameLeaveEvent(Game game, WrappedGamePlayer player){
        this.game = game;
        this.player = player;
    }

    public Game getGame(){
        return game;
    }

    public WrappedGamePlayer getPlayer(){
        return player;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
