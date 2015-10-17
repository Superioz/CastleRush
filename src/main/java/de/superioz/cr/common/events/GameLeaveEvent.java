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
public class GameLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameManager.Game game;
    private Player player;

    public GameLeaveEvent(GameManager.Game game, Player player){
        this.game = game;
        this.player = player;
    }

    public GameManager.Game getGame(){
        return game;
    }

    public Player getPlayer(){
        return player;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
