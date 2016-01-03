package de.superioz.cr.common.timer;

import de.superioz.cr.common.event.GamePhaseEvent;
import de.superioz.cr.common.event.GameScoreboardUpdateEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GamePhase;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.common.runnable.SuperRepeater;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class BuildCountdown {

    protected SuperRepeater repeater;
    protected Game game;

    public BuildCountdown(Game game){
        this.game = game;
    }

    /**
     * Run method
     */
    public void run(){
        repeater = new SuperRepeater(60 * game.getSettings().getBuildTimer());
        repeater.run(repeatRunnable -> {
            int counter = repeater.getCounter();

            if(game == null || !game.isRunning()
                    || game.getArena().getGamePhase() != GamePhase.BUILD){
                repeatRunnable.cancel();
            }

            if(counter % (60 * 5) == 0){
                game.broadcast(LanguageManager.get("thereAreMinutesLeft")
                        .replace("%time", (counter / 60) + ""));
            }
            else if(counter <= 10){
                game.broadcast(LanguageManager.get("thereAreSecondsLeft")
                        .replace("%time", counter + ""));
            }

            // Set scoreboard
            if(game.getArena().getGamePhase() == GamePhase.BUILD)
                SuperLibrary.callEvent(new GameScoreboardUpdateEvent(game, GameScoreboardUpdateEvent.Reason.UPDATE));
        }, endRunnable -> {
            // What happens at the end
            // Timer runs out - gamestate dont change
            // now the players plays another castle and they have to try to capture the wool
            if(game == null || !game.isRunning()){
                endRunnable.cancel();
            }

            if(game.getArena().getPlayers().size() < 2){
                game.setWinner(game.getArena().getPlayers().get(0));
                SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.END));
                return;
            }

            SuperLibrary.callEvent(new GamePhaseEvent(game, GamePhase.CAPTURE));
            game.broadcast(LanguageManager.get("startCaptureCastle"));

            // Set scoreboard
            SuperLibrary.callEvent(new GameScoreboardUpdateEvent(game, GameScoreboardUpdateEvent.Reason.UPDATE));
        }, 20);
    }

    public SuperRepeater getRepeater(){
        return repeater;
    }
}
