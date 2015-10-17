package de.superioz.cr.common;

import de.superioz.cr.common.game.GameManager;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class WrappedGamePlayer {

    protected Player player;
    protected GameManager.Game game;

    public WrappedGamePlayer(GameManager.Game game, Player player){
        this.game = game;
        this.player = player;
    }

    public GameManager.Game getGame(){
        return game;
    }

    public Player getPlayer(){
        return player;
    }
}
