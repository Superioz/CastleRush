package de.superioz.cr.common.stats;

import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.database.sql.database.Database;
import de.superioz.library.java.database.sql.database.MySQL;
import de.superioz.library.java.database.sql.database.SQLite;

import java.io.File;
import java.sql.Connection;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class DatabaseManager {

    private Database database;
    private Connection connection;
    private DatabaseType type;

    public static final String DATABASE_NAME = "stats";

    public DatabaseManager(){
        this.type = PluginSettings.DATABASE_TYPE;

        if(type == DatabaseType.SQLITE){
            File folder = new File(CastleRush.getInstance().getDataFolder() + "/data");
            folder.mkdirs();
            this.database = new SQLite(CastleRush.getInstance(), "data/" + DATABASE_NAME);
        }
        else if(type == DatabaseType.MYSQL){
            this.database = new MySQL(CastleRush.getInstance(),
                    PluginSettings.DATABASE_HOSTNAME,
                    PluginSettings.DATABASE_PORT,
                    PluginSettings.DATABASE_DATABASE,
                    PluginSettings.DATABASE_USER,
                    PluginSettings.DATABASE_PASSWORD);
        }

        if(!open()){
            CastleRush.getSuperLogger().consoleLog("Couldn't connect to database!");
        }
    }

    /**
     * Opens the connection
     *
     * @return The result
     */
    public boolean open(){
        this.connection = database.openConnection();
        return this.connection != null;
    }

    /**
     * Closes the connection
     *
     * @return The result
     */
    public boolean close(){
        return this.database.closeConnection();
    }

    /**
     * Checks the connection
     *
     * @return The result
     */
    public boolean check(){
        return this.database.checkConnection();
    }

    // -- Intern methods

    public Database getDatabase(){
        return database;
    }

    public Connection getConnection(){
        return connection;
    }

    public DatabaseType getType(){
        return type;
    }
}
