package de.superioz.cr.common.countdowns;

import com.darkblade12.particleeffect.ParticleEffect;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.cache.RawUnpreparedArena;
import de.superioz.cr.common.arena.cache.UnpreparedArena;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class EditorCacheTimer {

    public static BukkitRunnable instance;

    public static void run(Player cacheOwner){
        instance = new BukkitRunnable() {

            RawUnpreparedArena rawArena = ArenaManager.EditorCache.get(cacheOwner);
            UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(cacheOwner);

            @Override
            public void run(){
                if(!ArenaManager.EditorCache.contains(cacheOwner)){
                    this.cancel();
                }

                for(Location loc : rawArena.getSpawnPoints()){
                    Location fixed = LocationUtils.fix(loc.getBlock().getLocation());

                }
            }
        };

        instance.runTaskTimer(CastleRush.getInstance(), 20L, 20L);
    }

    public static BukkitRunnable getRunnable(){
        return instance;
    }

    public void markLocation(Location loc, Player player, LocationType locationType){
        ParticleEffect particleEffect = ParticleEffect.BARRIER;

        switch(locationType){
            case SPAWN:
                particleEffect = ParticleEffect.HEART;
                break;
            case PLOT:
                particleEffect = ParticleEffect.CLOUD;
                break;
            case WALL:
                particleEffect = ParticleEffect.TOWN_AURA;
                break;
        }

        particleEffect.display(0, 0, 0, 0, 1, loc, player);
    }

    public enum LocationType {

        SPAWN,
        PLOT,
        WALL;

    }

}
