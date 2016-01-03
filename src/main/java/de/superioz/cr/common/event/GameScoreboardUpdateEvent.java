package de.superioz.cr.common.event;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Getter
public class GameScoreboardUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private Reason reason;
    private List<WrappedGamePlayer> players;

    public GameScoreboardUpdateEvent(Game game, Reason reason, WrappedGamePlayer... players){
        this.game = game;
        this.reason = reason;
        this.players = Arrays.asList(players);
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }


    public enum Reason {

        DELETE,
        UPDATE

    }

}
