package de.superioz.cr.common.stats;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.database.sql.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class SQLUtils {

    public static final String SQL_WINS_ID = "wins";
    public static final String SQL_LOSES_ID = "loses";
    public static final String SQL_ELO_ID = "elo";
    public static final String SQL_UUID_ID = "uuid";
    public static final String SQL_NAME_ID = "name";

    private static final String GET_STATS_QUERY = "SELECT * FROM %db WHERE " + withQuotes(SQL_UUID_ID) + "='%id';";
    private static final String GET_STATS_QUERY_NAME = "SELECT * FROM %db WHERE " + withQuotes(SQL_NAME_ID) + "='%id';";
    private static final String INSERT_STATS_EXECUTE = "INSERT INTO %db (%columns) VALUES (%values);";
    private static final String UPDATE_STATS_EXECUTE = "UPDATE %db SET %columns WHERE " + withQuotes(SQL_UUID_ID) + "='%id';";

    /**
     * Creates the table
     *
     * @return The result
     */
    public static boolean createTable(Connection connection, Database base){
        if(!base.checkConnection()){
            return false;
        }

        try{
            PreparedStatement statement = connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS " + withQuotes(DatabaseManager.DATABASE_NAME) + " (" +
                            SQL_UUID_ID + " VARCHAR(128) NOT NULL, " +
                            SQL_ELO_ID + " INT, " +
                            SQL_LOSES_ID + " INT, " +
                            SQL_WINS_ID + " INT, " +
                            SQL_NAME_ID + " VARCHAR(20)" + ");");
            base.executeSQL(statement);
            return true;
        }catch(SQLException e){
            CastleRush.getSuperLogger().consoleLog("Couldn't create table!");
        }
        return false;
    }

    /**
     * Get prepared statement for all stats
     *
     * @param uuid The uuid
     *
     * @return The statement
     */
    public static PreparedStatement getStatsQuery(UUID uuid){
        try{
            String query = GET_STATS_QUERY.replace("%db", DatabaseManager.DATABASE_NAME).replace("%id", uuid + "");
            return CastleRush.getDatabaseManager().getConnection()
                    .prepareStatement(query);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get prepared statement for all stats
     *
     * @param name The name
     *
     * @return The statement
     */
    public static PreparedStatement getStatsQuery(String name){
        try{
            String query = GET_STATS_QUERY_NAME.replace("%db", DatabaseManager.DATABASE_NAME).replace("%id", name + "");
            return CastleRush.getDatabaseManager().getConnection()
                    .prepareStatement(query);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Get prepared statement for insert a player into database
     *
     * @param uuid  The uuid
     * @param name  The name
     * @param elo   The elo
     * @param wins  The wins
     * @param loses The loses
     *
     * @return The statement
     */
    public static PreparedStatement getStatsInsert(UUID uuid, String name, int elo, int wins, int loses){
        try{
            return CastleRush.getDatabaseManager().getConnection()
                    .prepareStatement(INSERT_STATS_EXECUTE
                            .replace("%db", DatabaseManager.DATABASE_NAME)
                            .replace("%columns", withQuotes(SQL_UUID_ID) + ","
                                    + withQuotes(SQL_ELO_ID) + ","
                                    + withQuotes(SQL_WINS_ID) + ","
                                    + withQuotes(SQL_LOSES_ID) + ","
                                    + withQuotes(SQL_NAME_ID))
                            .replace("%values", "'" + uuid + "'" + "," + elo + "," + wins + "," + loses + ",'" + name + "'"));
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get prepared statement for updating players stats
     *
     * @param uuid  The uuid
     * @param name  The name
     * @param elo   The elo
     * @param wins  The wins
     * @param loses The loses
     *
     * @return The statement
     */
    public static PreparedStatement getStatsUpdate(UUID uuid, String name, int elo, int wins, int loses){
        try{
            String eloString = SQL_ELO_ID + "=" + elo;
            String winsString = SQL_WINS_ID + "=" + wins;
            String losesString = SQL_LOSES_ID + "=" + loses;
            String nameString = SQL_NAME_ID + "='" + name + "'";

            return CastleRush.getDatabaseManager().getConnection()
                    .prepareStatement(UPDATE_STATS_EXECUTE
                            .replace("%db", DatabaseManager.DATABASE_NAME)
                            .replace("%columns", eloString + "," + winsString + "," + losesString + "," + nameString)
                            .replace("%id", uuid + ""));
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets given string surrounded with quotes
     * @param s The string
     * @return The string with quotes
     */
    public static String withQuotes(String s){
        return "`" + s + "`";
    }

}
