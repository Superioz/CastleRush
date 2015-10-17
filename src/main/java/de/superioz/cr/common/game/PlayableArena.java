package de.superioz.cr.common.game;

import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.sign.JoinSign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class PlayableArena {

    protected Arena arena;
    protected GameManager.State gameState;
    protected List<Player> players;

    public PlayableArena(Arena arena, GameManager.State gameState, JoinSign sign){
        this.arena = arena;
        this.gameState = gameState;
        this.players = new ArrayList<>();
    }

    public List<Player> getPlayers(){
        return players;
    }

    public Arena getArena(){
        return arena;
    }

    public GameManager.State getGameState(){
        return gameState;
    }

}
