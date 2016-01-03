package de.superioz.cr.common.timer;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.event.GameLeaveEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.common.runnable.SuperDelayer;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class PlayerLeftTimer {

    protected SuperDelayer delayer;

    /**
     * Run method
     *
     * @param player The player
     */
    public void run(WrappedGamePlayer player){
        delayer = new SuperDelayer(PluginSettings.REJOIN_TIME * 20);

        if(player.hasLeft()){
            delayer.run(bukkitRunnable -> {
                if(player.hasLeft()){
                    Game game = player.getGame();
                    if(game != null
                            && game.isRunning()){
                        SuperLibrary.callEvent(new GameLeaveEvent(game, player,
                                GameLeaveEvent.Type.REJOIN_TIME_RUNS_OUT));
                    }
                }
            });
        }
    }

    public SuperDelayer getDelayer(){
        return delayer;
    }
}
