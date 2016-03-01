package de.superioz.cr.common.arena;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.*;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.util.GeometryUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class PlayableArena {

    protected Arena arena;
    protected GameState gameState;
    protected GamePhase gamePhase;
    protected List<WrappedGamePlayer> players;
    protected GameType type;

    private Material lastWallsMaterial;

    public PlayableArena(Arena arena, GameState gameState, GamePhase phase){
        this.arena = arena;
        this.gameState = gameState;
        this.gamePhase = phase;
        this.players = new ArrayList<>();
    }

    /**
     * Get current player size
     *
     * @return The size
     */
    public int getCurrentPlayerSize(){
        int counter = 0;

        for(WrappedGamePlayer gp : getPlayers()){
            if(!gp.hasLeft())
                counter++;
        }
        return counter;
    }

    /**
     * Load walls
     */
    private void loadWalls(){
        getArena().getGameWalls().forEach(GameWall::reloadBoundaries);
    }

    /**
     * Set walls with given material
     *
     * @param mat       The material
     * @param toReplace The to replace material
     */
    private void setWalls(Material mat, List<Material> toReplace){
        final Material[] finalMat = new Material[]{mat};
        this.loadWalls();

        new BukkitRunnable() {
            @Override
            public void run(){
                if(finalMat[0] == null)
                    finalMat[0] = Material.AIR;

                for(GameWall wall : getArena().getGameWalls()){
                    SimplePair<Location, Location> boundaries = wall.getBoundaries();

                    final Material finalMats = finalMat[0];
                    GeometryUtil.calcCuboid(boundaries.getType1(), boundaries.getType2()).stream().filter(l
                            -> toReplace.contains(l.getBlock().getType())).forEach(l
                            -> l.getBlock().setType(finalMats));
                }

                lastWallsMaterial = finalMat[0];
            }
        }.runTaskLater(CastleRush.getInstance(), 1L);
    }

    /**
     * Set walls (replace air)
     *
     * @param mat The material
     */
    public void setWalls(Material mat){
        this.setWalls(mat, Collections.singletonList(Material.AIR));
    }

    /**
     * Reset the walls
     */
    public void resetWalls(){
        this.setWalls(Material.AIR, Collections.singletonList(this.lastWallsMaterial));
    }

    /**
     * Get wrapped game player of given player
     *
     * @param p The player
     *
     * @return The wrapped game player
     */
    public WrappedGamePlayer getPlayer(Player p){
        WrappedGamePlayer g = null;

        for(WrappedGamePlayer gp : getPlayers()){
            if(gp.getPlayer() != null && gp.getPlayer().equals(p))
                g = gp;
        }
        return g;
    }

    /**
     * Get the game
     *
     * @return The game
     */
    public Game getGame(){
        return GameManager.getGame(getArena());
    }

    // -- Intern methods


    public Arena getArena(){
        return arena;
    }

    public void setArena(Arena arena){
        this.arena = arena;
    }

    public GamePhase getGamePhase(){
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase){
        this.gamePhase = gamePhase;
    }

    public GameState getGameState(){
        return gameState;
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }

    public GameType getType(){
        return type;
    }

    public void setType(GameType type){
        this.type = type;
    }

    public List<WrappedGamePlayer> getPlayers(){
        return players;
    }

    public void setPlayers(List<WrappedGamePlayer> players){
        this.players = players;
    }

    public Material getLastWallsMaterial(){
        return lastWallsMaterial;
    }

    public void setLastWallsMaterial(Material lastWallsMaterial){
        this.lastWallsMaterial = lastWallsMaterial;
    }
}
