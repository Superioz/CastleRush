package de.superioz.cr.util;

import de.superioz.cr.common.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class WorldBackup implements Listener {

    private World world;
    private String worldName;
    private Game game;

    public WorldBackup(Game game){
        this.game = game;
        this.world = game.getWorld();
        this.worldName = world.getName();
    }

    public void unloadWorld(){
        for(Player player : world.getPlayers()){
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        Bukkit.getServer().unloadWorld(this.getWorld(), false);
    }

    public void loadWorld(){
        Bukkit.getServer().createWorld(new WorldCreator(worldName).copy(world));
        this.updateWorlds();
    }

    public void updateWorlds(){
        this.world = Bukkit.getWorld(worldName);
        this.game.updateWorld();
    }

    public void prepareBackup(){
        getWorld().setAutoSave(false);
    }

    public World getWorld(){
        if(world == null)
            world = Bukkit.getWorld(worldName);
        return world;
    }

}
