package de.superioz.cr.common.timer;

import de.superioz.cr.common.event.GameScoreboardUpdateEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GamePhase;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.bukkit.BukkitLibrary;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class CaptureTimer {

    /**
     * Run method
     */
    public void run(final Game game){
        new BukkitRunnable() {
            @Override
            public void run(){
                if(game.getArena().getGamePhase() != GamePhase.CAPTURE){
                    this.cancel();
                    return;
                }

                // Set scoreboard
                BukkitLibrary.callEvent(new GameScoreboardUpdateEvent(game, GameScoreboardUpdateEvent.Reason.UPDATE));
            }
        }.runTaskTimer(CastleRush.getInstance(), 0, 20L);
    }

}
