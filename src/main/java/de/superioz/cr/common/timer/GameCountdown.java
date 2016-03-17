package de.superioz.cr.common.timer;

import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.event.GamePhaseEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GamePhase;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.library.bukkit.BukkitLibrary;
import de.superioz.library.bukkit.common.runnable.SuperRepeater;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameCountdown {

    protected SuperRepeater lobbyToBuild;
    protected SuperRepeater waitingToFinish;
    protected Game game;

    public static final int LAST_COUNTDOWN = 10;

    public GameCountdown(Game game){
        this.game = game;
    }

    /**
     * Run method
     */
    public void runLobbyTimer(){
        this.lobbyToBuild = new SuperRepeater(PluginSettings.LOBBY_TIMER);
        this.lobbyToBuild.run(onRepeat -> {
            if(!game.enoughPlayers()){
                lobbyToBuild.getRunnable().cancel();
                getGame().broadcast(LanguageManager.get("lobbyCountdownCancelled"));
                return;
            }

            int counter = lobbyToBuild.getCounter();

            if(counter % 10 == 0
                    || counter <= LAST_COUNTDOWN){
                getGame().broadcast(LanguageManager.get("lobbyCountdownItem").replace("%counter", counter+""));
            }
        }, onFinish -> {
            if(!game.enoughPlayers()){
                lobbyToBuild.getRunnable().cancel();
                getGame().broadcast(LanguageManager.get("lobbyCountdownCancelled"));
                return;
            }

            BukkitLibrary.callEvent(new GamePhaseEvent(game, GamePhase.BUILD));
        }, 20);
    }

    /**
     * Run method
     */
    public void runEndTimer(){
        this.waitingToFinish = new SuperRepeater(PluginSettings.END_TIMER);
        this.waitingToFinish.run(onRepeat -> {
            int counter = waitingToFinish.getCounter();

            if(counter == PluginSettings.END_TIMER || counter <= LAST_COUNTDOWN){
                getGame().broadcast(LanguageManager.get("endCountdownItem").replace("%counter", counter+""));
            }
        }, onFinish -> BukkitLibrary.callEvent(new GamePhaseEvent(game, GamePhase.FINISH)), 20);
    }

    /**
     * Cancels every task
     */
    public void cancel(){
        if(this.lobbyToBuild != null)
            this.lobbyToBuild.getRunnable().cancel();
        if(this.waitingToFinish != null)
            this.waitingToFinish.getRunnable().cancel();
    }

    // -- Intern methods

    public SuperRepeater getLobbyToBuild(){
        return lobbyToBuild;
    }

    public SuperRepeater getWaitingToFinish(){
        return waitingToFinish;
    }

    public Game getGame(){
        return game;
    }

}
