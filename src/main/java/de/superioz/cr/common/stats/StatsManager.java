package de.superioz.cr.common.stats;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameState;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.common.settings.PluginSettings;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class StatsManager {

    /**
     * Gets the top 'val' players
     *
     * @param val How many players from 1
     *
     * @return The list
     */
    public static List<PlayerStats> getTop(int val){
        String query = "SELECT * FROM " + DatabaseManager.DATABASE_NAME + " ORDER BY " + SQLUtils.SQL_ELO_ID + " DESC LIMIT " + val;
        List<PlayerStats> list = new ArrayList<>();

        try{
            ResultSet resultSet = CastleRush.getDatabaseManager().getConnection().prepareStatement(query).executeQuery();

            while(resultSet.next()){
                int wins = resultSet.getInt(SQLUtils.SQL_WINS_ID);
                int loses = resultSet.getInt(SQLUtils.SQL_LOSES_ID);
                int elo = resultSet.getInt(SQLUtils.SQL_ELO_ID);
                String name = resultSet.getString(SQLUtils.SQL_NAME_ID);
                UUID uuid = UUID.fromString(resultSet.getString(SQLUtils.SQL_UUID_ID));

                PlayerStats stats = new PlayerStats(wins, loses, elo, uuid, name);
                list.add(stats);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get stats of given uuid
     *
     * @param uuid The uuid
     *
     * @return The stats
     */
    public static PlayerStats getStats(UUID uuid){
        if(!check() || !contains(uuid)){
            return null;
        }

        try{
            PreparedStatement statement = SQLUtils.getStatsQuery(uuid);
            assert statement != null;
            ResultSet resultSet = statement.executeQuery();
            PlayerStats stats = null;

            while(resultSet.next()){
                int wins = resultSet.getInt(SQLUtils.SQL_WINS_ID);
                int loses = resultSet.getInt(SQLUtils.SQL_LOSES_ID);
                int elo = resultSet.getInt(SQLUtils.SQL_ELO_ID);
                String name = resultSet.getString(SQLUtils.SQL_NAME_ID);

                stats = new PlayerStats(wins, loses, elo, uuid, name);
            }
            return stats;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get stats of given uuid
     *
     * @param name The name
     *
     * @return The stats
     */
    public static PlayerStats getStats(String name){
        if(!check() || !contains(name)){
            return null;
        }

        try{
            PreparedStatement statement = SQLUtils.getStatsQuery(name);
            assert statement != null;
            ResultSet resultSet = statement.executeQuery();
            PlayerStats stats = null;

            while(resultSet.next()){
                int wins = resultSet.getInt(SQLUtils.SQL_WINS_ID);
                int loses = resultSet.getInt(SQLUtils.SQL_LOSES_ID);
                int elo = resultSet.getInt(SQLUtils.SQL_ELO_ID);
                UUID uuid = UUID.fromString(resultSet.getString(SQLUtils.SQL_UUID_ID));

                stats = new PlayerStats(wins, loses, elo, uuid, name);
            }
            return stats;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add given uuid to database
     *
     * @param player The player
     */
    public static void add(Player player){
        DatabaseManager manager = CastleRush.getDatabaseManager();
        UUID uuid = player.getUniqueId();
        String name = player.getDisplayName();

        if(!check() || contains(uuid)){
            return;
        }

        try{
            manager.getDatabase().executeSQL(SQLUtils.getStatsInsert(uuid, name, PluginSettings.PLAYERSTATS_DEFAULT_ELO, 0, 0));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Updates the stats from given stats#uuid
     *
     * @param stats The stats
     */
    public static void updateStats(PlayerStats stats){
        DatabaseManager manager = CastleRush.getDatabaseManager();

        if(!check() || !contains(stats.getUuid())){
            return;
        }

        try{
            manager.getDatabase().updateSQL(SQLUtils.getStatsUpdate(stats.getUuid(), stats.getCurrentName(),
                    stats.getElo(), stats.getWins(), stats.getLoses()));
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Checks if uuid is in database
     *
     * @param uuid The uuid
     *
     * @return The result
     */
    public static boolean contains(UUID uuid){
        if(!check()){
            return false;
        }

        try{
            PreparedStatement statement = SQLUtils.getStatsQuery(uuid);
            assert statement != null;
            ResultSet result = statement.executeQuery();

            return result.next();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if uuid is in database
     *
     * @param name The name
     *
     * @return The result
     */
    public static boolean contains(String name){
        if(!check()){
            return false;
        }

        try{
            PreparedStatement statement = SQLUtils.getStatsQuery(name);
            assert statement != null;
            ResultSet result = statement.executeQuery();

            return result.next();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update statistics of finished game
     *
     * @param game The game
     */
    public static void updateStatistics(Game game){
        if(game.getArena().getGameState() != GameState.WAITING
                || !PluginSettings.PLAYERSTATS_ENABLED){
            return;
        }

        // Check playersize
        if(!(game.getArena().getCurrentPlayerSize() >= 2)){
            game.broadcast(LanguageManager.get("tooFewPlayers"));
            return;
        }

        // Check connection
        if(!CastleRush.getDatabaseManager().check()){
            game.broadcast(LanguageManager.get("databaseNotConnected"));
            return;
        }

        // Get players (winners and losers)
        List<WrappedGamePlayer> loser = game.getArena().getPlayers().stream().collect(Collectors.toList());
        List<WrappedGamePlayer> winnerTeam = game.getWinner().getTeamMates();
        loser.removeAll(winnerTeam);

        // Get time used and base elo
        int minutes = (int) (((System.currentTimeMillis() - game.getTimeStamp()) / 1000) / 60);
        int baseWinnerElo = PluginSettings.PLAYERSTATS_BASE_WINNER_ELO;
        int loserElo = PluginSettings.PLAYERSTATS_LOSER_ELO;

        // Update stats of losers
        for(WrappedGamePlayer gp : loser){
            // Stats
            PlayerStats oldStats = gp.getStats();

            // Get new stats
            int newLoses = oldStats.getLoses() + 1;
            int newElo = oldStats.getElo();

            // Check if elo is allowed
            if(game.getSettings().isRankedMatch()){
                newElo -= loserElo;

                if(newElo < 0 && !PluginSettings.PLAYERSTATS_ELO_NEGATIVE){
                    newElo = 0;
                }

                ChatManager.stats().write(LanguageManager.get("eloUpdated")
                        .replace("%operation", "&c-&7" + loserElo), gp.getPlayer());
            }
            // Send message
            ChatManager.stats().write(LanguageManager.get("losesUpdated"), gp.getPlayer());

            // Update
            gp.updateStats(new PlayerStats(oldStats.getWins(), newLoses, newElo, gp.getUuid(), gp.getDisplayName()));
        }

        // Update stats of winners
        for(WrappedGamePlayer gp : winnerTeam){
            // Stats
            PlayerStats oldStats = gp.getStats();

            // Get new stats
            int newWins = oldStats.getWins() + 1;
            int eloAddon = PluginSettings.GAME_TIMER - minutes;
            if(eloAddon < 0) eloAddon = 0;
            int newEloAddon = 0;

            if(game.getSettings().isRankedMatch()){
                newEloAddon += baseWinnerElo + (eloAddon / 2);
                ChatManager.stats().write(LanguageManager.get("eloUpdated")
                        .replace("%operation", "&a+&7" + newEloAddon), gp.getPlayer());
            }
            // Send message
            ChatManager.stats().write(LanguageManager.get("winsUpdated"), gp.getPlayer());

            // Update
            gp.updateStats(new PlayerStats(newWins, oldStats.getLoses(), oldStats.getElo() + newEloAddon, gp.getUuid(), gp.getDisplayName()));
        }
    }

    // ------ Intern methods

    // Check if stats are enabled and database is connected
    private static boolean check(){
        return PluginSettings.PLAYERSTATS_ENABLED && CastleRush.getDatabaseManager().check();
    }

}
