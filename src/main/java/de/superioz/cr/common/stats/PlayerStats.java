package de.superioz.cr.common.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Getter
@Setter
public class PlayerStats {

    private int wins;
    private int loses;
    private int elo;
    private UUID uuid;
    private String currentName;

    public PlayerStats(int wins, int loses, int elo, UUID uuid, String name){
        this.wins = wins;
        this.loses = loses;
        this.elo = elo;
        this.uuid = uuid;
        this.currentName = name;
    }

    public String toColoredString(){
        return "&e" + getElo() + " &7Elo&8; &e" + getWins() + " &7Win(s)&8; &e" + getLoses() + " &7Lose(s)";
    }

    public String toSimpleColoredString(){
        return "&e" + getElo() + " &7E&8; &e" + getWins() + " &7W&8; &e" + getLoses() + " &7L";
    }

}
