package de.superioz.cr.common.game.countdowns;

import de.superioz.cr.common.events.GameFinishEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.util.task.Countdown;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class BuildCountdown {

    public static Countdown instance;

    public static void run(GameManager.Game game){
        instance = new Countdown(60 * CastleRush.getConfigFile().config().getInt("timer"));

        instance.run(endRunnable -> {
            // What happens at the end
            // Timer runs out - gamestate dont change
            // now the players plays another castle and they have to try to capture the wool

            if(game.getArena().getPlayers().size() < 2){
                CastleRush.getPluginManager().callEvent(new GameFinishEvent(game,
                        game.getArena().getPlayers().get(0)));
                return;
            }

            game.prepareNextState();
            game.broadcast(CastleRush.getProperties().get("startCaptureCastle"));
        }, startRunnable -> {
            int counter = instance.getCounter();

            if(counter % (60*5) == 0){
                game.broadcast(CastleRush.getProperties().get("thereAreMinutesLeft")
                        .replace("%time", (counter / 60) + ""));
            }
            else if(counter <= 10){
                game.broadcast(CastleRush.getProperties().get("thereAreSecondsLeft")
                        .replace("%time", counter + ""));
            }
        });
    }

}
