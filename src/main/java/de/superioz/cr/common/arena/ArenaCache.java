package de.superioz.cr.common.arena;

import com.google.gson.reflect.TypeToken;
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
        file.write(this.list);
    }

    @Override
    public void from(JsonFile file){
        List<String> stringList = file.read(new TypeToken<ArrayList<String>>(){}.getType());

        if(this.list == null)
            this.list = new ArrayList<>();
        else
            this.arenaList = fromStringList(stringList);
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
            if(ar.equals(arena))
                return true;
        }
        return false;
    }

}
