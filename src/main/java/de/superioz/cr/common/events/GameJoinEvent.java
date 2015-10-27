package de.superioz.cr.common.events;

import de.superioz.cr.common.game.GameManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameJoinEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private GameManager.Game game;
    private Player player;
    private Location loc;

    public GameJoinEvent(GameManager.Game game, Player player, Location loc){
        this.game = game;
        this.player = player;
        this.loc = loc;
    }

    public Location getLoc(){
        return loc;
    }

    public GameManager.Game getGame(){
        return game;
    }

    public Player getPlayer(){
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
