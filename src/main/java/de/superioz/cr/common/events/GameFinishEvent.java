package de.superioz.cr.common.events;

import de.superioz.cr.common.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameFinishEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private GameManager.Game game;
    private Player winner;

    public GameFinishEvent(GameManager.Game game, Player winner){
        this.game = game;
        this.winner = winner;
    }

    public Player getWinner(){
        return winner;
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
