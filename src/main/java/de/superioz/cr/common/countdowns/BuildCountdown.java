package de.superioz.cr.common.countdowns;

import de.superioz.cr.common.events.GameFinishEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.common.runnable.SuperRepeater;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class BuildCountdown {

    private static SuperRepeater repeater = new SuperRepeater();

    public static void run(Game game){

        repeater.run(endRunnable -> {
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
            int counter = repeater.getCounter();

            if(counter % (60 * 5) == 0){
                game.broadcast(CastleRush.getProperties().get("thereAreMinutesLeft")
                        .replace("%time", (counter / 60) + ""));
            }
            else if(counter <= 10){
                game.broadcast(CastleRush.getProperties().get("thereAreSecondsLeft")
                        .replace("%time", counter + ""));
            }
        }, 20, 60 * CastleRush.getConfigFile().config().getInt("timer"));
    }

    public static SuperRepeater getRepeater(){
        return repeater;
    }
}
