package de.superioz.cr.common.game.team;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Getter
public enum TeamColor {

    RED(DyeColor.RED, ChatColor.RED),
    BLUE(DyeColor.BLUE, ChatColor.BLUE),
    GREEN(DyeColor.GREEN, ChatColor.GREEN),
    YELLOW(DyeColor.YELLOW, ChatColor.YELLOW),
    ORANGE(DyeColor.ORANGE, ChatColor.GOLD),
    AQUA(DyeColor.CYAN, ChatColor.AQUA),
    MAGENTA(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE),
    PURPLE(DyeColor.PURPLE, ChatColor.DARK_PURPLE);

    private DyeColor dColor;
    private ChatColor cColor;

    TeamColor(DyeColor dyeColor, ChatColor chatColor){
        dColor = dyeColor;
        cColor = chatColor;
    }

    /**
     * Get teamcolor from index
     * @param index The index
     * @return The teamcolor
     */
    public static TeamColor from(int index){
        if(index < 0 || index > values().length-1)
            index = 0;

        return values()[index];
    }

    // -- Intern methods

    public DyeColor getDyeColor(){
        return dColor;
    }

    public ChatColor getChatColor(){
        return cColor;
    }

}
