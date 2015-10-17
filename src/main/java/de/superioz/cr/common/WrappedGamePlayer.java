package de.superioz.cr.common;

import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GamePlot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class WrappedGamePlayer {

    protected Player player;
    protected GameManager.Game game;

    public WrappedGamePlayer(GameManager.Game game, Player player){
        this.game = game;
        this.player = player;
    }

    public GameManager.Game getGame(){
        return game;
    }

    public GamePlot getPlot(){
        List<WrappedGamePlayer> players = getGame().getArena().getPlayers();
        int index = getGameIndex();

        Location spawnLocation = getGame().getArena().getArena().getSpawnPoints().get(index);

        double distance = -1;
        GamePlot plot = null;

        for(GamePlot gamePlot : getGame().getArena().getArena().getGamePlots()){
            Location l = gamePlot.getTeleportPoint();

            double d = l.distanceSquared(spawnLocation);
            if(distance > d){
                distance = d;
                plot = gamePlot;
            }
        }
        return plot;
    }

    public int getGameIndex(){
        List<WrappedGamePlayer> players = getGame().getArena().getPlayers();

        for(int i = 0; i < players.size(); i++){
            if(players.get(i).equals(this))
                return i;
        }
        return -1;
    }

    public Player getPlayer(){
        return player;
    }
}
