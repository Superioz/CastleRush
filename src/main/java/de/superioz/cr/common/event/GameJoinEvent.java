package de.superioz.cr.common.event;

import de.superioz.cr.common.arena.Arena;
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
    private Arena arena;
    private Player player;
    private Location loc;

    public GameJoinEvent(Arena arena, Player player, Location loc){
        this.arena = arena;
        this.player = player;
        this.loc = loc;
    }

    // -- Intern methods


    public Arena getArena(){
        return arena;
    }

    public Player getPlayer(){
        return player;
    }

    public Location getLoc(){
        return loc;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
