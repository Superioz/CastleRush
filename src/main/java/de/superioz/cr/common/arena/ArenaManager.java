package de.superioz.cr.common.arena;

import de.superioz.cr.common.tool.ArenaMultiTool;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.file.type.JsonFile;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaManager {

    protected static JsonFile backup;
    protected static ArenaCache cache;
    protected static ArenaMultiTool arenaMultiTool;

    public static void load(){
        cache = new ArenaCache();
        backup = new JsonFile("arenas", "data", CastleRush.getInstance().getDataFolder());
        arenaMultiTool = new ArenaMultiTool();

        if(backup.exists())
            cache.from(backup);
    }

    public static void backup(){
        if(size() > 0){
            backup.load(false, true);
        }

        if(backup.exists()){
            cache.write(backup);
        }
    }

    public static void add(Arena arena){
        cache.add(arena);
        backup();
    }

    public static void remove(int index){
        cache.remove(index);
        backup();
    }

    public static Arena get(String name){
        for(Arena ar : cache.arenaList){
            if((ar.getName() != null) && (name.equalsIgnoreCase(ar.getName()))){
                return ar;
            }
        }
        return null;
    }

    public static Arena get(int index){
        return cache.getFrom(index);
    }

    public static int size(){
        return cache.arenaList.size();
    }

    public static JsonFile getBackup(){
        return backup;
    }

    public static ArenaCache getCache(){
        return cache;
    }

    public static boolean checkArenaName(String name){
        return (!(name == null) && (!name.isEmpty())
                && (name.length() > 3)
                && (name.length() < 32));
    }

    // ==================================================================

    public static class EditorCache {

        protected static HashMap<Player, UnpreparedArena> editorLast = new HashMap<>();
        protected static HashMap<Player, RawUnpreparedArena> editorCache = new HashMap<>();

        public static void addPlayer(Player player, String name){
            if(!editorCache.containsKey(player)){
                editorLast.put(player, new UnpreparedArena(name));
                editorCache.put(player, new RawUnpreparedArena(name));
            }
        }

        public static boolean isFinished(Player player){
            if(!contains(player))
                return false;

            return editorLast.get(player).isFinished();
        }

        public static boolean contains(Player player){
            return editorCache.containsKey(player);
        }

        public static boolean contains(String arenaName){
            for(RawUnpreparedArena arena : editorCache.values()){
                if(arena.getName().equalsIgnoreCase(arenaName))
                    return true;
            }
            return false;
        }

        public static RawUnpreparedArena get(Player player){
            if(contains(player))
                return editorCache.get(player);
            return null;
        }

        public static UnpreparedArena getLast(Player player){
            if(contains(player))
                return editorLast.get(player);
            return null;
        }

    }

}
