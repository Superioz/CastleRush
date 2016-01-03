package de.superioz.cr.common.cache;

import com.google.gson.reflect.TypeToken;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.Arena;
import de.superioz.library.java.cache.SimpleCache;
import de.superioz.library.java.file.type.JsonFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaCache extends SimpleCache<String> {

    public List<Arena> arenaList = new ArrayList<>();

    /**
     * Loads given arena again from given file
     *
     * @param file  The file
     * @param arena The arena
     */
    public void loadAgain(JsonFile file, Arena arena){
        List<Arena> l = load(file);
        int index = getIndex(arena);

        if(index == -1){
            return;
        }

        arenaList.set(index, l.get(getIndex(arena, l)));
        write(file);
    }

    /**
     * Loads the json file into a list of arenas
     *
     * @param file The file
     *
     * @return The arenalist
     */
    public List<Arena> load(JsonFile file){
        if(file == null)
            return new ArrayList<>();

        List<String> stringList = file.read(new TypeToken<ArrayList<String>>() {}.getType());

        if(this.list == null)
            return new ArrayList<>();
        else
            return fromStringList(stringList);
    }

    /**
     * Adds given arena to cache
     *
     * @param object The arena
     */
    public void add(Arena object){
        if(!this.contains(object))
            this.arenaList.add(object);
    }

    /**
     * Get arena from index
     *
     * @param index The index
     *
     * @return The arena
     */
    public Arena getFrom(int index){
        return Arena.fromString(getCached().get(index));
    }

    /**
     * Remove given arena from cache
     *
     * @param object The arena
     */
    public void remove(Arena object){
        if(this.contains(object))
            this.arenaList.remove(object);

        if(this.arenaList.size() == 0){
            ArenaManager.getBackup().write(this.arenaList);
        }
    }

    /**
     * Remove given index from cache
     *
     * @param index The index
     */
    public void remove(int index){
        this.list.remove(index);
    }

    /**
     * This arenalist to stringlist
     *
     * @return The stringlist
     */
    public List<String> toStringList(){
        return arenaList.stream().map(Arena::toString).collect(Collectors.toList());
    }

    /**
     * Gets arenalist from stringlist
     *
     * @param list The stringlist
     *
     * @return The arenalist
     */
    public List<Arena> fromStringList(List<String> list){
        return list.stream().map(Arena::fromString).collect(Collectors.toList());
    }

    /**
     * List of arenas from string list
     *
     * @return List of arenas
     */
    public List<Arena> list(){
        return fromStringList(this.list);
    }

    /**
     * Checks if cache contains arena
     *
     * @param arena The arena
     *
     * @return The result
     */
    public boolean contains(Arena arena){
        for(Arena ar : this.arenaList){
            if(ar.getName().equals(arena.getName()))
                return true;
        }
        return false;
    }

    /**
     * Get index of arena
     *
     * @param arena The arena
     *
     * @return The index
     */
    public int getIndex(Arena arena){
        return getIndex(arena, arenaList);
    }

    /**
     * Get index of given arena from list
     *
     * @param arena     The arena
     * @param arenaList The list
     *
     * @return The index
     */
    public int getIndex(Arena arena, List<Arena> arenaList){
        for(int i = 0; i < arenaList.size(); i++){
            Arena ar = arenaList.get(i);
            if(ar.getName().equals(arena.getName()))
                return i;
        }
        return -1;
    }

    // -- Intern methods

    @Override
    public void write(JsonFile file){
        this.list = this.toStringList();

        if(this.list.size() > 0){
            file.write(this.list);
        }
    }

    @Override
    public void from(JsonFile file){
        if(file == null)
            return;

        List<String> stringList = file.read(new TypeToken<ArrayList<String>>() {}.getType());

        if(this.list == null)
            this.list = new ArrayList<>();
        else
            this.arenaList = fromStringList(stringList);
    }

}
