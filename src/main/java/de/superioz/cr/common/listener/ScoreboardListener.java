package de.superioz.cr.common.listener;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.event.GameScoreboardUpdateEvent;
import de.superioz.cr.common.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class ScoreboardListener implements Listener {

    @EventHandler
    public void onScoreboard(GameScoreboardUpdateEvent event){
        Game game = event.getGame();
        GameScoreboardUpdateEvent.Reason reason = event.getReason();
        List<WrappedGamePlayer> gamePlayerList = event.getPlayers();

        switch(reason){
            case UPDATE:
                game.getScoreboard().updateScoreboard(game.getArena().getPlayers());
                break;
            case DELETE:
                game.getScoreboard().reset(gamePlayerList, true);
                break;
        }
    }

}
