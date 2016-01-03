package de.superioz.cr.common.game;

import lombok.Getter;
import org.bukkit.block.Sign;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Getter
public class GameSign {

    private Sign sign;
    private Type type;

    public GameSign(Type type, Sign sign){
        this.type = type;
        this.sign = sign;
    }

    // Class to define a game type
    public enum Type {

        CREATE_GAME,
        JOIN_GAME

    }

}
