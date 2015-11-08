package de.superioz.cr.common;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.objects.GamePlot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class WrappedGamePlayer {

    protected Player player;
    protected Game game;
    protected Location joinLocation;
    protected int index;

    public WrappedGamePlayer(Game game, Player player, Location joinLocation, int index){
        this.game = game;
        this.player = player;
        this.joinLocation = joinLocation;
        this.index = index;
    }

    public Game getGame(){
        return game;
    }

    public Location getJoinLocation(){
        return joinLocation;
    }

    public GamePlot getPlot(){
        Location spawnLocation = getSpawnLocation();

        double distance = -1;
        GamePlot plot = null;

        for(GamePlot gamePlot : getGame().getArena().getArena().getGamePlots()){
            Location l = gamePlot.getTeleportPoint();

            double d = spawnLocation.distanceSquared(l);

            if(distance == -1){
                distance = d;
                plot = gamePlot;
            }
            else if(d < distance){
                distance = d;
                plot = gamePlot;
            }
        }

        return plot;
    }

    public int getGameIndex(){
        return this.index;
    }

    public Location getSpawnLocation(){
        return getGame().getArena().getArena().getSpawnPoints()
                .get(getGameIndex()).clone().add(0, 1, 0);
    }

    public Player getPlayer(){
        return player;
    }

    public void clearInventory(){
        player.getInventory().clear();
        this.clearArmor();
    }

    public void clearArmor(){
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
    }


    public void clear(){
        player.setHealth(20D);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
    }

    public List<WrappedGamePlayer> getTeamMates(){
        return getGame().getArena().getPlayers().stream()
                .filter(gp -> gp.getGameIndex() == getGameIndex()
                        && gp != this).collect(Collectors.toList());
    }

    public List<String> getTeamMatesNames(){
        return getTeamMates().stream().map(gp -> gp.getPlayer().getDisplayName())
                .collect(Collectors.toList());
    }

    public World getWorld(){
        return getPlayer().getWorld();
    }

    public void teleport(Location location){
        getPlayer().teleport(location);
    }

}
