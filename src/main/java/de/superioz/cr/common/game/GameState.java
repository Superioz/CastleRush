package de.superioz.cr.common.game;

import de.superioz.library.java.util.SimpleStringUtils;
import net.md_5.bungee.api.ChatColor;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum GameState {

    LOBBY(ChatColor.GREEN, "lobby"),
    INGAME(ChatColor.DARK_RED, "ingame"),
    WAITING(ChatColor.GOLD, "waiting");

    String n;
    ChatColor color;

    GameState(ChatColor color, String name){
        this.color = color;
        this.n = name;
    }

    // -- Intern methods

    public String getSpecifier(){
        return color + SimpleStringUtils.upperFirstLetter(n);
    }

}
