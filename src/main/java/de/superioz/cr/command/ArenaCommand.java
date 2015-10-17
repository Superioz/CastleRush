package de.superioz.cr.command;

import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.RawUnpreparedArena;
import de.superioz.cr.common.arena.UnpreparedArena;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaCommand {

    @SubCommand(name = "edit", aliases = "create", permission = "castlerush.editAndCreate"
        , min = 1, usage = "[arenaName]")
    public void editAndCreate(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        String arenaName = "";
        for(int i = 0; i < commandContext.argumentsLength(); i++){
            String add = "";
            if(i == commandContext.argumentsLength())
                add = " ";

            arenaName += commandContext.argument(i) + add;
        }

        // Check if name of arena typed in is valid
        if(!ArenaManager.checkArenaName(arenaName)
                || ArenaManager.EditorCache.contains(arenaName)){
            CastleRush.getChatMessager().send("&cThat name isn't valid!", player);
            return;
        }

        // Check if player already started to edit/create an arena
        if(ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou are already in the EditorCache!", player);
            return;
        }

        ArenaManager.EditorCache.addPlayer(player, arenaName);
        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        CastleRush.getChatMessager().send("&7You can now edit/create the arena &b" + arenaName + "&7!", player);
    }

    @SubCommand(name = "addplot", permission = "castlerush.addplot")
    public void addPlot(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);


    }

}
