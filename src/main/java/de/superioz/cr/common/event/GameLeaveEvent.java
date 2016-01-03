package de.superioz.cr.common.event;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameState;
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
    private Type type;
    private GameState state;

    public GameLeaveEvent(Game game, WrappedGamePlayer player, Type type){
        this.game = game;
        this.player = player;
        this.type = type;
        this.state = game.getArena().getGameState();
    }

    // -- Intern methods

    public Game getGame(){
        return game;
    }

    public WrappedGamePlayer getPlayer(){
        return player;
    }

    public Type getType(){
        return type;
    }

    public GameState getState(){
        return state;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public enum Type {

        COMMAND_LEAVE,
        SERVER_LEAVE,
        REJOIN_TIME_RUNS_OUT,
        LOBBY_LEAVE

    }

}
