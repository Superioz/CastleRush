package de.superioz.cr.command;

import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.command.annts.RawSubCommand;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaCommand {

    @SubCommand(name = "arena", aliases = {"ar", "a"}, permission = "castlerush.arena"
            , min = 1, usage = "[delete:create:edit:list]", desc = "Commands for handling arenas")
    public void arenaCommand(SubCommandContext context){
        context.forward(this.getClass(), "delete", "create", "edit", "list");
    }

    @RawSubCommand(name = "create", aliases = {"crea", "c"}, permission = "castlerush.arena.create"
            , min = 1, usage = "[arenaName]", desc = "Creates an arena. Puts you into editor cache")
    public void create(SubCommandContext context){
        Player player = (Player) context.getSender();

        // Check if player already started to edit/create an arena
        if(ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou are already in the EditorCache!", player);
            return;
        }

        String arenaName = "";
        for(int i = 0; i < context.argumentsLength(); i++){
            String add = "";
            if(i == context.argumentsLength())
                add = " ";

            arenaName += context.argument(i) + add;
        }

        // Check if name of arena typed in is valid
        if(!ArenaManager.checkArenaName(arenaName)
                || ArenaManager.EditorCache.contains(arenaName)){
            CastleRush.getChatMessager().send("&cThat name isn't valid!", player);
            return;
        }

        ArenaManager.EditorCache.addPlayer(player, arenaName);
        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        CastleRush.getChatMessager().send("&7You can now create the arena &b" + arenaName + "&7!", player);
    }

    @RawSubCommand(name = "delete", aliases = {"del", "d"}, permission = "castlerush.arena.delete"
            , min = 1, usage = "[arenaName]", desc = "Deletes an arena")
    public void delete(SubCommandContext context){
        Player player = (Player) context.getSender();

        String arenaName = "";
        for(int i = 0; i < context.argumentsLength(); i++){
            String add = "";
            if(i == context.argumentsLength())
                add = " ";

            arenaName += context.argument(i) + add;
        }

        Arena ar  = ArenaManager.get(arenaName);
        if(ar == null){
            CastleRush.getChatMessager().send("&cThis arena doesn't exist!", player);
            return;
        }

        ArenaManager.getCache().remove(ar);
        CastleRush.getChatMessager().send("&7Removed arena &c" + arenaName + "&7!", player);
    }

    @RawSubCommand(name = "edit", aliases = {"e"}, permission = "castlerush.arena.edit"
            , min = 1, usage = "[arenaName]", desc = "Edits an arena. Puts you into editor cache")
    public void edit(SubCommandContext context){

    }

    @RawSubCommand(name = "list", aliases = {"l"}, permission = "castlerush.arena.list"
            , desc = "Lists all arenas")
    public void list(SubCommandContext context){
        Player player = (Player) context.getSender();

        List<Arena> arenas = ArenaManager.getCache().arenaList;
        String msg = "";

        for(Arena arena : arenas){
            msg += "&b" + arena.getName() + "&8;";
        }

        CastleRush.getChatMessager().send("&7Arenas: " + msg + " &7[&b"+ArenaManager.size() + "&7]",
                player);
    }

}
