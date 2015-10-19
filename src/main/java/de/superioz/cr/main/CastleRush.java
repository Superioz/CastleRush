package de.superioz.cr.main;

import de.superioz.cr.command.ArenaCommand;
import de.superioz.cr.command.GameCommand;
import de.superioz.cr.command.MainCommand;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.listener.GameListener;
import de.superioz.cr.common.listener.GameProtectListener;
import de.superioz.cr.common.listener.SignListener;
import de.superioz.library.java.file.properties.SuperProperties;
import de.superioz.library.java.file.type.YamlFile;
import de.superioz.library.java.logging.SuperLogger;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.command.CommandHandler;
import de.superioz.library.minecraft.server.util.chat.ChatMessager;
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
    private static ChatMessager chatMessager;

    private static SuperProperties<String> stringProperties;
    private static YamlFile configFile;

    @Override
    public void onEnable(){
        SuperLibrary.initFor(this);
        instance = this;
        pluginManager = getServer().getPluginManager();

        // SuperLogger
        superLogger = new SuperLogger(this);
        superLogger.consoleLog("SuperLogger initialized!");

        // Properties
        stringProperties = new SuperProperties<>("strings", "", getDataFolder());
        stringProperties.load(true);
        superLogger.consoleLog("Properties loaded!");

        // Config
        configFile = new YamlFile("config", "", getDataFolder());
        configFile.load(true, true);

        // ChatMessager
        chatMessager = new ChatMessager(stringProperties.get("chatPrefix") + "&8┃ &r");
        superLogger.consoleLog("ChatMessager loaded!");

        // Commands
        CommandHandler.registerWith(MainCommand.class, ArenaCommand.class, GameCommand.class);
        superLogger.consoleLog("Commands registered!");

        // Listener
        getPluginManager().registerEvents(new GameListener(), this);
        getPluginManager().registerEvents(new GameProtectListener(), this);
        getPluginManager().registerEvents(new SignListener(), this);
        superLogger.consoleLog("Listener registered!");

        // ArenaManager
        ArenaManager.load();
        superLogger.consoleLog("ArenaManager loaded!");
    }

    @Override
    public void onDisable(){
        superLogger.consoleLog("Backup Arenas ..");
        ArenaManager.backup();
        superLogger.consoleLog("Arenas saved.");
    }

    public static ChatMessager getChatMessager(){
        return chatMessager;
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

    public static SuperProperties<String> getProperties(){
        return stringProperties;
    }

    public static YamlFile getConfigFile(){
        return configFile;
    }
}
