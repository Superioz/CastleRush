package de.superioz.cr.common;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.bukkit.message.PlayerMessager;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class ChatManager {

    private static PlayerMessager chatMessager;
    private static PlayerMessager gameMessager;
    private static PlayerMessager statsMessager;

    static {
        chatMessager = CastleRush.getChatMessager();
        gameMessager = CastleRush.getGameMessager();
        statsMessager = CastleRush.getStatsMessager();
    }

    // -- Intern methods

    public static PlayerMessager info(){
        return getChatMessager();
    }

    public static PlayerMessager game(){
        return getGameMessager();
    }

    public static PlayerMessager stats(){
        return statsMessager;
    }

    public static PlayerMessager getChatMessager(){
        return chatMessager;
    }

    public static PlayerMessager getGameMessager(){
        return gameMessager;
    }
}
