package de.superioz.cr.common.event;

import de.superioz.cr.common.game.GameSign;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameSignInteractEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Player player;
    private GameSign.Type type;

    public GameSignInteractEvent(Player player, GameSign.Type type){
        this.player = player;
        this.type = type;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public Player getPlayer(){
        return player;
    }

    public GameSign.Type getType(){
        return type;
    }
}
