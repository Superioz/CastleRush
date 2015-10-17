package de.superioz.cr.common.sign;

import de.superioz.cr.common.arena.Arena;
import org.bukkit.block.Sign;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class JoinSign {

    protected Sign sign;
    protected Arena arena;

    public JoinSign(Sign sign, Arena parent){
        this.arena = parent;
        this.sign = sign;
    }

    public Arena getArena(){
        return arena;
    }

    public Sign getSign(){
        return sign;
    }
}
