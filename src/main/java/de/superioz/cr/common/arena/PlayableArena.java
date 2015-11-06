package de.superioz.cr.common.arena;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameStateChangeEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.util.chat.ChatUtils;
import org.bukkit.block.Sign;

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
    protected List<WrappedGamePlayer> players;
    protected ArenaSign sign;

    public PlayableArena(Arena arena, GameManager.State gameState, Sign sign){
        this.arena = arena;
        this.gameState = gameState;
        this.players = new ArrayList<>();
        this.sign = new ArenaSign(sign, this);
    }

    public List<WrappedGamePlayer> getPlayers(){
        return players;
    }

    public Arena getArena(){
        return arena;
    }

    public GameManager.State getGameState(){
        return gameState;
    }

    public void setGameState(GameManager.State gameState){
        this.gameState = gameState;

        // Call event that the gamestate has changed
        CastleRush.getPluginManager()
                .callEvent(new GameStateChangeEvent(GameManager.getGame(getArena()), gameState));
    }

    public int getMaxPlayers(){
        return getArena().getSpawnPoints().size();
    }

    public ArenaSign getSign(){
        return sign;
    }


    public class ArenaSign {

        protected Sign sign;
        protected PlayableArena arena;

        public ArenaSign(Sign sign, PlayableArena arena){
            this.sign = sign;
            this.arena = arena;
        }

        public Sign getSign(){
            return sign;
        }

        public void setLine(int line, String message){
            getSign().setLine(line, ChatUtils.colored(message));
        }

        public void setLine(ArenaSignLine line, String message){
            getSign().setLine(line.getLine(), ChatUtils.colored(message));
        }

        public void update(){
            getSign().update();
        }

        public void updateGamestate(){
            setLine(ArenaSignLine.GAMESTATE.getLine(),
                    this.arena.gameState.getSpecifier());
            this.update();
        }

        public void updatePlayers(){
            setLine(ArenaSignLine.PLAYERS.getLine(),
                    this.arena.getArena().getPattern(getPlayers().size()));
            this.update();
        }

    }

    public enum ArenaSignLine {

        PLAYERS(2),
        GAMESTATE(3);

        int i;

        ArenaSignLine(int i){
           this.i = i;
        }

        public int getLine(){
            return i;
        }


    }

}
