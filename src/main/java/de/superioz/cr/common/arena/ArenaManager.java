package de.superioz.cr.common.arena;

import de.superioz.cr.common.cache.ArenaCache;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.tool.GameMasterSettingsTool;
import de.superioz.cr.common.tool.IngameTeleportTool;
import de.superioz.cr.common.tool.TeamChooseTool;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.PluginTools;
import de.superioz.library.java.file.type.JsonFile;
import de.superioz.library.minecraft.server.common.command.context.CommandContext;
import org.bukkit.World;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaManager {

    protected static JsonFile backup;
    protected static ArenaCache cache;

    protected static TeamChooseTool teamTool;
    protected static IngameTeleportTool ingameTeleportTool;
    protected static GameMasterSettingsTool gameMasterTool;

    /**
     * Load this class (values etc)
     */
    public static void load(){
        cache = new ArenaCache();
        backup = new JsonFile("arenas", "data", CastleRush.getInstance().getDataFolder());
        PluginTools.load();

        // Get tools
        teamTool = new TeamChooseTool();
        ingameTeleportTool = new IngameTeleportTool();
        gameMasterTool = new GameMasterSettingsTool();

        if(backup.exists())
            cache.from(backup);
    }

    /**
     * Reload given arena
     *
     * @param arena The arena
     */
    public static void loadAgain(Arena arena){
        cache.loadAgain(backup, arena);
    }

    /**
     * Reloads the world of given arena until it is loaded
     *
     * @param arena The arena
     */
    public static void loadWorldProperly(Arena arena){
        World world = arena.getWorld();

        if(world == null){
            ArenaManager.loadAgain(arena);
            arena = ArenaManager.get(arena.getName());
            loadWorldProperly(arena);
        }
    }

    /**
     * Checks if a free arena is available
     *
     * @return The result as boolean
     */
    public static boolean hasFreeWorld(){
        int freeCounter = 0;
        for(Arena arena : getCache().arenaList){
            if(!existInWorld(arena.getWorld())){
                freeCounter++;
            }
        }
        return freeCounter != 0;
    }

    /**
     * Reloads the whole arena cache
     */
    public static void reload(){
        cache.arenaList.forEach(ArenaManager::loadWorldProperly);
    }

    /**
     * Backups the arenas
     */
    public static void backup(){
        if(size() > 0){
            backup.load(false, true);
        }

        if(backup.exists()){
            cache.write(backup);
        }
    }

    /**
     * Checks if a playablearena exists in given world (Except given arena)
     *
     * @param world  The world
     * @param except The exception
     *
     * @return The result as boolean
     */
    public static boolean existInWorld(World world, Arena except){
        for(Game game : GameManager.getRunningGames()){
            Arena arena = game.getArena().getArena();
            World world1 = arena.getWorld();

            if(world1.getName().equalsIgnoreCase(world.getName())){
                return except == null || !arena.getName().equalsIgnoreCase(except.getName());
            }
        }
        return false;
    }

    public static boolean existInWorld(World world){
        for(Game game : GameManager.getRunningGames()){
            Arena arena = game.getArena().getArena();
            World world1 = arena.getWorld();

            if(world1.getName().equalsIgnoreCase(world.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * Adds given arena to cache
     *
     * @param arena The arena
     */
    public static void add(Arena arena){
        if(cache.contains(arena)){
            cache.arenaList.set(cache.getIndex(arena), arena);
        }
        else{
            cache.add(arena);
        }

        backup();
        loadAgain(arena);
    }

    /**
     * Remove arena with given index from cache
     *
     * @param index The index as integer
     */
    public static void remove(int index){
        cache.remove(index);
        backup();
    }

    /**
     * Gets arena with given name
     *
     * @param name The name
     *
     * @return The arena
     */
    public static Arena get(String name){
        for(Arena ar : cache.arenaList){
            if((ar.getName() != null) && (name.equalsIgnoreCase(ar.getName()))){
                return ar;
            }
        }
        return null;
    }

    /**
     * Gets full name with given context and start-index
     *
     * @param context The context
     * @param arg     The start-index
     *
     * @return The full name as string
     */
    public static String getName(CommandContext context, int arg){
        StringBuilder builder = new StringBuilder("");

        for(int i = arg; i < context.getArgumentsLength() + 1; i++){
            String add = " ";
            if(i == (context.getArgumentsLength()))
                add = "";

            builder.append(context.getArgument(i)).append(add);
        }

        return builder.toString();
    }

    /**
     * Gets the arena by given index
     *
     * @param index The index
     *
     * @return The arena
     */
    public static Arena get(int index){
        return cache.getFrom(index);
    }

    /**
     * Size of all arenas
     *
     * @return The size
     */
    public static int size(){
        return cache.arenaList.size();
    }

    /**
     * Checks given arenaname
     *
     * @param name The name
     *
     * @return The result
     */
    public static boolean checkArenaName(String name){
        return (!(name == null) && (!name.isEmpty())
                && (name.length() > 3)
                && (name.length() < 32));
    }

    // -- Intern methods

    public static JsonFile getBackup(){
        return backup;
    }

    public static ArenaCache getCache(){
        return cache;
    }


}
