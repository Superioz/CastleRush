package de.superioz.cr.common.cache;

import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.raw.RawUnpreparedArena;
import de.superioz.cr.common.arena.raw.UnpreparedArena;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class EditorCache {

    protected static HashMap<Player, UnpreparedArena> editorLast = new HashMap<>();
    protected static HashMap<Player, RawUnpreparedArena> editorCache = new HashMap<>();

    /**
     * Adds given player to cache
     *
     * @param player The player
     * @param name   The name of to-create arena
     */
    public static void addPlayer(Player player, String name){
        if(!editorCache.containsKey(player)){
            editorLast.put(player, new UnpreparedArena(name));
            editorCache.put(player, new RawUnpreparedArena(name));
        }
    }

    /**
     * Insert given player into cache with values of given arena
     *
     * @param player The player
     * @param arena  The arena
     */
    public static void insert(Player player, Arena arena){
        addPlayer(player, arena.getName());

        RawUnpreparedArena rawUnpreparedArena = get(player);
        UnpreparedArena unpreparedArena = getLast(player);

        if(rawUnpreparedArena == null
                || unpreparedArena == null){
            return;
        }

        unpreparedArena.setSpawn(arena.getSpawnPoint());
        unpreparedArena.setItemKit(arena.getItemKit());
        arena.getGameWalls().forEach(unpreparedArena::addGameWall);
        arena.getGamePlots().forEach(unpreparedArena::addGamePlot);
    }

    /**
     * Checks if given player is finished
     *
     * @param player The player
     *
     * @return The result
     */
    public static boolean isFinished(Player player){
        return contains(player) && editorLast.get(player).isFinished();
    }

    /**
     * Removes given player from cache
     *
     * @param player The player
     */
    public static void remove(Player player){
        if(contains(player)){
            if(!isFinished(player))
                return;

            editorLast.remove(player);
            editorCache.remove(player);
        }
    }

    /**
     * Removes given player from cache without checking if he's finished
     *
     * @param player The player
     */
    public static void removeForSure(Player player){
        if(contains(player)){
            editorLast.remove(player);
            editorCache.remove(player);
        }
    }

    /**
     * Checks if given player is inside cache
     *
     * @param player The player
     *
     * @return The result
     */
    public static boolean contains(Player player){
        return editorCache.containsKey(player);
    }

    /**
     * Checks if given name is inside cache
     *
     * @param arenaName The name
     *
     * @return The result
     */
    public static boolean contains(String arenaName){
        for(RawUnpreparedArena arena : editorCache.values()){
            if(arena.getName().equalsIgnoreCase(arenaName))
                return true;
        }
        return false;
    }

    /**
     * Gets raw arena from player
     *
     * @param player The player
     *
     * @return The arena
     */
    public static RawUnpreparedArena get(Player player){
        if(contains(player))
            return editorCache.get(player);
        return null;
    }

    /**
     * Gets unprepared arena from player
     *
     * @param player The player
     *
     * @return The arena
     */
    public static UnpreparedArena getLast(Player player){
        if(contains(player))
            return editorLast.get(player);
        return null;
    }

}
