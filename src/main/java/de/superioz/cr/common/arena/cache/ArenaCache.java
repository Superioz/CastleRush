package de.superioz.cr.common.arena.cache;

import com.google.gson.reflect.TypeToken;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.object.Arena;
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

        List<String> stringList = file.read(new TypeToken<ArrayList<String>>(){}.getType());

        if(this.list == null)
            this.list = new ArrayList<>();
        else
            this.arenaList = fromStringList(stringList);
    }

    public void loadAgain(JsonFile file, Arena arena){
        List<Arena> l = load(file);
        int index = getIndex(arena);

        if(index == -1){
            return;
        }

        arenaList.set(index, l.get(getIndex(arena, l)));
        write(file);
    }

    public List<Arena> load(JsonFile file){
        if(file == null)
             return new ArrayList<>();

        List<String> stringList = file.read(new TypeToken<ArrayList<String>>(){}.getType());

        if(this.list == null)
            return new ArrayList<>();
        else
            return fromStringList(stringList);
    }

    public void add(Arena object){
        if(!this.contains(object))
            this.arenaList.add(object);
    }

    public Arena getFrom(int index){
        return Arena.fromString(getCached().get(index));
    }

    public void remove(Arena object){
        if(this.contains(object))
            this.arenaList.remove(object);

        if(this.arenaList.size() == 0){
            ArenaManager.getBackup().write(this.arenaList);
        }
    }

    public void remove(int index){
        this.list.remove(index);
    }

    public List<String> toStringList(){
        return arenaList.stream().map(Arena::toString).collect(Collectors.toList());
    }

    public List<Arena> fromStringList(List<String> list){
        return list.stream().map(Arena::fromString).collect(Collectors.toList());
    }

    public List<Arena> list(){
        return fromStringList(this.list);
    }

    public boolean contains(Arena arena){
        for(Arena ar : this.arenaList){
            if(ar.getName().equals(arena.getName()))
                return true;
        }
        return false;
    }

    public int getIndex(Arena arena){
        return getIndex(arena, arenaList);
    }

    public int getIndex(Arena arena, List<Arena> arenaList){
        for(int i = 0; i < arenaList.size(); i++){
            Arena ar = arenaList.get(i);
            if(ar.getName().equals(arena.getName()))
                return i;
        }
        return -1;
    }

}
