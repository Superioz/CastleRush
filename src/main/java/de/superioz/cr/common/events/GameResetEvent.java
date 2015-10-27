package de.superioz.cr.common.events;

import de.superioz.cr.common.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameResetEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private GameManager.Game game;

    public GameResetEvent(GameManager.Game game, Player winner){
        this.game = game;
    }

    public GameManager.Game getGame(){
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
