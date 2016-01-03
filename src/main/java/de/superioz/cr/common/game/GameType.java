package de.superioz.cr.common.game;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum GameType {

    PRIVATE(DyeColor.RED, ChatColor.RED + "Private"),
    PUBLIC(DyeColor.BLUE, ChatColor.BLUE + "Public");

    DyeColor color;
    String s;

    GameType(DyeColor color, String s){
        this.color = color;
        this.s = s;
    }

    /**
     * Get gametype from string
     * @param s The string
     * @return The gametype
     */
    public static GameType from(String s){
        for(GameType type : values()){
            if(s.equalsIgnoreCase(type.getSpecifier()))
                return type;
        }
        return null;
    }

    // -- Intern methods

    public DyeColor getColor(){
        return color;
    }

    public String getSpecifier(){
        return s;
    }

}
