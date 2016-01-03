package de.superioz.cr.common.event;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.team.Team;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Getter
public class GameTeamChangeEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private Game game;
    private Team team;

    public GameTeamChangeEvent(Game game, Team team){
        this.game = game;
        this.team = team;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
