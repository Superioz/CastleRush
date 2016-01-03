package de.superioz.cr.common.game;

import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.team.Team;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.util.TimeType;
import de.superioz.library.minecraft.server.common.view.SuperScoreboard;

import java.util.List;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameScoreboard {

    protected Game game;
    protected SuperScoreboard ingamePhaseScoreboard;

    public static final String SCOREBOARD_HEADER = LanguageManager.get("scoreboardPrefix") + " " + CastleRush.BLOCK_SPACER + " %time";
    public static final String TIME_PATTERN = "%hours:%minutes:%seconds";

    public GameScoreboard(Game game){
        this.game = game;
        this.ingamePhaseScoreboard = new SuperScoreboard("");
    }

    /**
     * Init scoreboard for given player
     *
     * @param player The player
     */
    public void initScoreboard(WrappedGamePlayer player){
        SuperScoreboard board = new SuperScoreboard(SCOREBOARD_HEADER.replace("%time", getTime()));
        Team team = player.getTeam();

        // Add main info's
        board.add("&7Players: &b" + getGame().getArena().getCurrentPlayerSize());
        board.add("&7Phase: &b" + getGame().getArena().getGamePhase().getSpecifier());
        board.add("&7Your Team: " + team.getColoredName(player.getGame()));
        board.add("&8&m---------------");

        // Teamnames
        for(WrappedGamePlayer gp : team.getTeamPlayer()){
            board.add("&8" + CastleRush.ARROW_SPACER + " " + team.getColor(gp.getGame()) + gp.getDisplayName());
        }
        board.build();

        player.setScoreboard(board);
        player.updateScoreboard();
    }

    /**
     * Gets the time for the game
     *
     * @return The time as string
     */
    public String getTime(){
        GamePhase phase = getGame().getArena().getGamePhase();
        String[] time = getGame().getTime();
        String min = time[TimeType.MINUTES.getIndex()];
        String hour = time[TimeType.HOURS.getIndex()];
        String color = "&a";

        if(hour.length() > 2){
            return "&7--:--:--";
        }

        if(phase == GamePhase.BUILD){
            int minutes = min.startsWith("0")
                    ? Integer.parseInt(String.valueOf(min.subSequence(1, min.length()))) : Integer.parseInt(min);

            if(minutes < 1)
                color = "&c";
            else if(minutes < (PluginSettings.GAME_TIMER / 4)){
                color = "&e";
            }
        }
        else if(phase == GamePhase.CAPTURE){
            color = "&c+";
        }

        return color + TIME_PATTERN
                .replace("%hours", hour)
                .replace("%minutes", min)
                .replace("%seconds", time[TimeType.SECONDS.getIndex()]);
    }

    /**
     * Resets the scoreboard (for the players too?)
     *
     * @param gamePlayerList The list of player
     * @param players        If players should reset true
     */
    public void reset(List<WrappedGamePlayer> gamePlayerList, boolean players){
        this.ingamePhaseScoreboard.reset(true);

        if(players)
            gamePlayerList.forEach(WrappedGamePlayer::resetScoreboard);
    }

    /**
     * Updates every scoreboard of every player
     *
     * @param gamePlayerList List of players
     */
    public void update(List<WrappedGamePlayer> gamePlayerList){
        gamePlayerList.forEach(WrappedGamePlayer::updateScoreboard);
    }

    /**
     * Inits the scoreboard for given players
     *
     * @param gamePlayerList The playerlist
     */
    public void updateScoreboard(List<WrappedGamePlayer> gamePlayerList){
        gamePlayerList.forEach(this::initScoreboard);
    }

    // -- Intern methods

    public Game getGame(){
        return game;
    }

}
