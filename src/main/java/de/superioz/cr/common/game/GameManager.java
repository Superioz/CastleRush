package de.superioz.cr.common.game;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.object.Arena;
import de.superioz.cr.common.game.objects.GamePlot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameManager {

    private static List<Game> runningGames = new ArrayList<>();
    public static List<Material> allowedBlocks = Arrays.asList(
            Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.LEVER);

    public static void addGameInQueue(Game game){
        if(!runningGames.contains(game)){
            runningGames.add(game);
            game.registerBackup();
        }
    }

    public static void removeGameFromQueue(Game game){

        if(runningGames.contains(game)){
            runningGames.remove(game);
            game.unregisterBackup();
        }
    }

    public static boolean containsGameInQueue(Arena arena){
        return getGame(arena) != null;
    }

    public static Game getGame(Arena arena){
        for(Game g : runningGames){
            if(g.getArena().getArena().getName().equalsIgnoreCase(arena.getName()))
                return g;
        }
        return null;
    }

    public static void stopArenas(){
        runningGames.forEach(Game::stop);
    }

    public static Game getGame(Player player){
        if(isIngame(player)){
            for(Game g : runningGames){
                if(isIngame(player, g))
                    return g;
            }
        }
        return null;
    }

    public static boolean isIngame(Player player){
        for(Game g : runningGames){
            if(isIngame(player, g))
                return true;
        }
        return false;
    }

    public static boolean isIngame(Player player, Game game){
        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            Player p = pl.getPlayer();

            if(p.getUniqueId().equals(player.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    public static WrappedGamePlayer getWrappedGamePlayer(Player player){
        WrappedGamePlayer gp = null;

        if(!isIngame(player))
            return gp;

        Game game = getGame(player);
        assert game != null;

        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            Player p = pl.getPlayer();

            if(p.getUniqueId().equals(player.getUniqueId())){
                gp = pl;
            }
        }
        return gp;
    }

    public static boolean hasPlot(Block block){
        for(Game g : runningGames){
            for(GamePlot pl : g.getArena().getArena().getGamePlots()){
                if(pl.isPart(block.getLocation()))
                    return true;
            }
        }
        return false;
    }

    public static boolean hasPlot(Block block, Game game){
        for(GamePlot pl : game.getArena().getArena().getGamePlots()){
            if(pl.isPart(block.getLocation()))
                return true;
        }
        return false;
    }

    public static Game getGame(Block block){
        for(Game g : runningGames){
            for(GamePlot pl : g.getArena().getArena().getGamePlots()){
                if(pl.isPart(block.getLocation()))
                    return g;
            }
        }
        return null;
    }

}
