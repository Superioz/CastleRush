package de.superioz.cr.common.arena.object;

import de.superioz.library.minecraft.server.util.ChatUtil;
import org.bukkit.block.Sign;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class ArenaSign {

    private PlayableArena playableArena;
    protected Sign sign;
    protected PlayableArena arena;

    public ArenaSign(PlayableArena playableArena, Sign sign, PlayableArena arena){
        this.playableArena = playableArena;
        this.sign = sign;
        this.arena = arena;
    }

    public Sign getSign(){
        return sign;
    }

    public void setLine(int line, String message){
        getSign().setLine(line, ChatUtil.colored(message));
    }

    public void setLine(ArenaSignLine line, String message){
        getSign().setLine(line.getLine(), ChatUtil.colored(message));
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
                this.arena.getArena().getPattern(playableArena.getPlayers().size()));
        this.update();
    }

    public PlayableArena getArena(){
        return arena;
    }

    public PlayableArena getPlayableArena(){
        return playableArena;
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
