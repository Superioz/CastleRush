package de.superioz.cr.common.events;

import de.superioz.cr.common.game.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GamePlayersAmountChangeEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private GameManager.Game game;

    public GamePlayersAmountChangeEvent(GameManager.Game game){
        this.game = game;
    }

    public GameManager.Game getGame(){
        return game;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

}
