package de.superioz.cr.util;

import de.superioz.cr.common.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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

    public boolean unloadWorld(){
        for(Player player : world.getPlayers()){
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        return Bukkit.getServer().unloadWorld(this.getWorld(), false);
    }

    public void loadWorld(){
        Bukkit.getServer().createWorld(new WorldCreator(worldName).copy(world));
        this.updateWorlds();
    }

    public void updateWorlds(){
        this.world = Bukkit.getWorld(worldName);
        this.game.updateWorld();
    }

    public World getWorld(){
        if(world == null)
            world = Bukkit.getWorld(worldName);
        return world;
    }

    public void rollback(){
        World world = getWorld();
        File folder = world.getWorldFolder();
        String worldName = world.getName();

        if(!unloadWorld()){
            return;
        }

        this.deleteDirectory(folder);
        final File templateFolder = new File(Bukkit.getServer().getWorldContainer(), worldName + "_template");
        final File worldFolder = new File(Bukkit.getServer().getWorldContainer(), worldName);

        if(!templateFolder.exists()){
            return;
        }

        this.copyDir(templateFolder, worldFolder);
        this.loadWorld();
    }

    private void copyDir(File source, File target){
        try{
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName())){
                if(source.isDirectory()){
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for(String file : files){
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyDir(srcFile, destFile);
                    }
                }else{
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private boolean deleteDirectory(File path){
        if(path.exists()){
            File files[] = path.listFiles();
            assert files != null;
            for(File file : files){
                if(file.isDirectory()){
                    deleteDirectory(file);
                }else{
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

}
