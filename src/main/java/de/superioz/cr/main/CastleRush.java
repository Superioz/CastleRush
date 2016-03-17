package de.superioz.cr.main;

import de.superioz.cr.command.*;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.listener.ScoreboardListener;
import de.superioz.cr.common.listener.SignListener;
import de.superioz.cr.common.listener.StatsListener;
import de.superioz.cr.common.listener.game.BukkitGameListener;
import de.superioz.cr.common.listener.game.CustomGameListener;
import de.superioz.cr.common.listener.game.GamePlotListener;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.common.stats.DatabaseManager;
import de.superioz.cr.common.stats.SQLUtils;
import de.superioz.cr.util.PluginColor;
import de.superioz.library.bukkit.BukkitLibrary;
import de.superioz.library.bukkit.common.command.CommandHandler;
import de.superioz.library.bukkit.exception.CommandRegisterException;
import de.superioz.library.bukkit.logging.SuperLogger;
import de.superioz.library.bukkit.message.PlayerMessager;
import de.superioz.library.bukkit.util.YamlFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class CastleRush extends JavaPlugin {

    private static CastleRush instance;
    private static SuperLogger superLogger;
    private static PluginManager pluginManager;
    private static YamlFile configFile;
    private static DatabaseManager databaseManager;

    private static PlayerMessager chatMessager;
    private static PlayerMessager gameMessager;
    private static PlayerMessager statsMessager;

    public static final String BLOCK_SPACER = PluginColor.DARK + "┃";
    public static final String ARROW_SPACER = PluginColor.DARK + "»";

    @Override
    public void onEnable(){
        BukkitLibrary.initFor(this);
        instance = this;
        pluginManager = getServer().getPluginManager();

        // SuperLogger
        superLogger = new SuperLogger(this);
        superLogger.consoleLog("SuperLogger initialized!");

        // Properties
        LanguageManager.load();
        superLogger.consoleLog("Properties loaded!");

        // Config
        configFile = new YamlFile("config", "", getDataFolder());
        configFile.load(true, true);
        PluginSettings.load();

        // Messager
        this.initMessager();

        // Commands
        this.registerCommands();

        // Listener
        this.registerListener();

        // ArenaManager
        ArenaManager.load();
        superLogger.consoleLog("ArenaManager loaded!");

        // Load database
        this.loadDatabase();
    }

    @Override
    public void onDisable(){
        GameManager.stopArenas();
        ArenaManager.backup();

        if(databaseManager.check())
            databaseManager.close();
    }

    /**
     * Registers the messager
     */
    public void initMessager(){
        chatMessager = new PlayerMessager(LanguageManager.get("chatPrefix") + BLOCK_SPACER + " " + PluginColor.RESET);
        gameMessager = new PlayerMessager(LanguageManager.get("chatPrefix") + ARROW_SPACER + " " + PluginColor.RESET);
        statsMessager = new PlayerMessager(LanguageManager.get("chatPrefix")
                + LanguageManager.get("statsPrefix") + " " + ARROW_SPACER + " " + PluginColor.RESET);

        // Print to console
        superLogger.consoleLog("ChatMessager initialised!");
    }

    /**
     * Registers the listener
     */
    public void registerListener(){
        getPluginManager().registerEvents(new CustomGameListener(), this);
        getPluginManager().registerEvents(new GamePlotListener(), this);
        getPluginManager().registerEvents(new SignListener(), this);
        getPluginManager().registerEvents(new BukkitGameListener(), this);
        getPluginManager().registerEvents(new ScoreboardListener(), this);
        getPluginManager().registerEvents(new StatsListener(), this);

        // Print to console
        superLogger.consoleLog("Listener registered!");
    }

    /**
     * Registers the commands
     */
    public void registerCommands(){
        try{
            CommandHandler.registerCommand(MainCommand.class, OtherCommand.class,
                    ArenaCommand.class, GameCommand.class, CacheCommand.class, StatsCommand.class);

            // Print to console
            superLogger.consoleLog("Commands registered!");
        }catch(CommandRegisterException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads the database
     */
    public void loadDatabase(){
        superLogger.consoleLog("Load player stats ..");
        if(PluginSettings.PLAYERSTATS_ENABLED){
            databaseManager = new DatabaseManager();
            SQLUtils.createTable(databaseManager.getConnection(), databaseManager.getDatabase());

            if(databaseManager.check())
                superLogger.consoleLog("Player stats loaded. [Using '" + databaseManager.getType().getSpecifier() + "']");
        }
    }

    // -- Intern methods because it seems that lombok sucks at static methods (idk why)

    public static PlayerMessager getChatMessager(){
        return chatMessager;
    }

    public static PlayerMessager getGameMessager(){
        return gameMessager;
    }

    public static PlayerMessager getStatsMessager(){
        return statsMessager;
    }

    public static CastleRush getInstance(){
        return instance;
    }

    public static PluginManager getPluginManager(){
        return pluginManager;
    }

    public static SuperLogger getSuperLogger(){
        return superLogger;
    }

    public static YamlFile getConfigFile(){
        return configFile;
    }

    public static DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
}
