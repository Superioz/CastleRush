package de.superioz.cr.common.event;

import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.game.GameType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameCreateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Arena arena;
    private GameType type;
    private Player player;

    public GameCreateEvent(Arena arena, GameType type, Player player){
        this.arena = arena;
        this.type = type;
        this.player = player;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public Arena getArena(){
        return arena;
    }

    public Player getPlayer(){
        return player;
    }

    public GameType getType(){
        return type;
    }
}
