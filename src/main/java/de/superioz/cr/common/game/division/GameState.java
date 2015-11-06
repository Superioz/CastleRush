package de.superioz.cr.common.game.division;

import net.md_5.bungee.api.ChatColor;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum GameState {

    LOBBY(ChatColor.GREEN + "lobby"),
    INGAME(ChatColor.DARK_RED + "ingame"),
    WAITING(ChatColor.GOLD + "waiting");


    String n;

    GameState(String name){
        this.n = name;
    }

    public String getSpecifier(){
        return n.toUpperCase();
    }

}
