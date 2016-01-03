package de.superioz.cr.common.settings;

import de.superioz.cr.common.stats.DatabaseType;
import de.superioz.cr.main.CastleRush;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class PluginSettings {

    // Should be final, but java sucks
    public static boolean CLEAR_INV;
    public static boolean TEAM_MODE;
    public static int GAME_TIMER;
    public static int LOBBY_TIMER;
    public static int END_TIMER;
    public static boolean GAME_WALLS_SET;
    public static Material GAME_WALLS_MATERIAL;
    public static int REJOIN_TIME;
    public static List<String> ALLOWED_BLOCKS;
    public static int LOBBY_MAX_DISTANCE;
    public static boolean PLAYERSTATS_ENABLED;
    public static int PLAYERSTATS_DEFAULT_ELO;
    public static int PLAYERSTATS_BASE_WINNER_ELO;
    public static int PLAYERSTATS_LOSER_ELO;
    public static boolean PLAYERSTATS_ELO_ENABLED;
    public static boolean PLAYERSTATS_ELO_NEGATIVE;
    public static DatabaseType DATABASE_TYPE;
    public static String DATABASE_HOSTNAME;
    public static String DATABASE_PORT;
    public static String DATABASE_DATABASE;
    public static String DATABASE_USER;
    public static String DATABASE_PASSWORD;
    public static boolean ENTER_PLOT_DURING_BUILD;

    // Other variables
    private static final String GAME_SECTION = "game.";
    private static final String PLAYERSTATS_SECTION = "playerStats.";
    private static final String STATS_ELO_SECTION = "playerStats.elo.";
    private static final String DATABASE_SECTION = "database.";

    /**
     * Loads values from the config
     */
    public static void load(){
        CLEAR_INV = getConfig().getBoolean("clearInv");
        TEAM_MODE = getConfig().getBoolean("teamMode");
        GAME_TIMER = getConfig().getInt(GAME_SECTION + "buildTimer");
        LOBBY_TIMER = getConfig().getInt(GAME_SECTION + "lobbyTimer");
        END_TIMER = getConfig().getInt(GAME_SECTION + "endTimer");
        GAME_WALLS_SET = getConfig().getBoolean(GAME_SECTION + "walls.set");
        GAME_WALLS_MATERIAL = Material.getMaterial(getConfig().getString(GAME_SECTION + "walls.material"));
        REJOIN_TIME = getConfig().getInt(GAME_SECTION + "rejoinTime");
        ALLOWED_BLOCKS = Arrays.asList(getConfig().getString(GAME_SECTION + "allowedBlocks").split(";"));
        LOBBY_MAX_DISTANCE = getConfig().getInt(GAME_SECTION + "lobbyMaxDistance");
        PLAYERSTATS_ENABLED = getConfig().getBoolean(PLAYERSTATS_SECTION + "enabled");
        PLAYERSTATS_DEFAULT_ELO = getConfig().getInt(STATS_ELO_SECTION + "default");
        PLAYERSTATS_BASE_WINNER_ELO = getConfig().getInt(STATS_ELO_SECTION + "baseWinner");
        PLAYERSTATS_LOSER_ELO = getConfig().getInt(STATS_ELO_SECTION + "loser");
        PLAYERSTATS_ELO_ENABLED = getConfig().getBoolean(STATS_ELO_SECTION + "enabled");
        PLAYERSTATS_ELO_NEGATIVE = getConfig().getBoolean(STATS_ELO_SECTION + "negative");
        DATABASE_TYPE = DatabaseType.from(getConfig().getString(DATABASE_SECTION + "type"));
        DATABASE_HOSTNAME = getConfig().getString(DATABASE_SECTION + "mysql.hostname");
        DATABASE_PORT = getConfig().getString(DATABASE_SECTION + "mysql.port");
        DATABASE_DATABASE = getConfig().getString(DATABASE_SECTION + "mysql.database");
        DATABASE_USER = getConfig().getString(DATABASE_SECTION + "mysql.user");
        DATABASE_PASSWORD = getConfig().getString(DATABASE_SECTION + "mysql.password");
        ENTER_PLOT_DURING_BUILD = getConfig().getBoolean(GAME_SECTION + "enterPlotDuringBuild");
    }

    /**
     * Get config
     * @return The file configuration
     */
    public static FileConfiguration getConfig(){
        return CastleRush.getConfigFile().config();
    }

    /**
     * Reloads the config
     */
    public static void reload(){
        CastleRush.getConfigFile().load(false, false);
        PluginSettings.load();
    }

}
