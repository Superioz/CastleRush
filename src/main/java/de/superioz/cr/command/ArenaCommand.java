package de.superioz.cr.command;

import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.object.Arena;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.common.command.SubCommand;
import de.superioz.library.minecraft.server.common.command.context.CommandContext;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaCommand {

    @SubCommand(label = "arena", aliases = {"ar", "a"}, permission = "castlerush.arena"
            , usage = "", desc = "Commands for handling arenas")
    public void arenaCommand(CommandContext context){
        CastleRush.getChatMessager().write("&c/cr arena [delete:create:edit:list]",
                (Player)context.getSender());
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "create", aliases = {"crea", "c"}, permission = "castlerush.arena.create"
            , min = 1, usage = "[arenaName]", desc = "Creates an arena. Puts you into editor cache")
    public void create(CommandContext context){
        Player player = (Player) context.getSender();

        // Check if player already started to edit/create an arena
        if(ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("alreadyInEditorCache"), player);
            return;
        }

        String arenaName = ArenaManager.getName(context, 1);

        // Check if name of arena typed in is valid
        if(!ArenaManager.checkArenaName(arenaName)
                || ArenaManager.EditorCache.contains(arenaName)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("nameNotValid")
                    + " ["+arenaName+"]", player);

            return;
        }

        ArenaManager.EditorCache.addPlayer(player, arenaName);
        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK.getWrappedStack());
        CastleRush.getChatMessager().write(
                CastleRush.getProperties().get("startCreatingArena").replace("%arena", arenaName), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "delete", aliases = {"del", "d"}, permission = "castlerush.arena.delete"
            , min = 1, usage = "[arenaName]", desc = "Deletes an arena")
    public void delete(CommandContext context){
        Player player = (Player) context.getSender();

        String arenaName = ArenaManager.getName(context, 1);

        Arena ar  = ArenaManager.get(arenaName);
        if(ar == null){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("arenaDoesntExist"), player);
            return;
        }

        ArenaManager.getCache().remove(ar);
        CastleRush.getChatMessager().write(
                CastleRush.getProperties().get("arenaRemoved").replace("%arena", arenaName), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "edit", aliases = {"e"}, permission = "castlerush.arena.edit"
            , min = 1, usage = "[arenaName]", desc = "Edits an arena. Puts you into editor cache")
    public void edit(CommandContext context){
        Player player = (Player) context.getSender();

        // Check if player already started to edit/create an arena
        if(ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("alreadyInEditorCache"), player);
            return;
        }

        String arenaName = ArenaManager.getName(context, 1);

        // Check if arena already exists
        Arena ar  = ArenaManager.get(arenaName);
        if(ar == null){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("arenaDoesntExist"), player);
            return;
        }

        ArenaManager.EditorCache.insert(player, ar);
        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK.getWrappedStack());
        CastleRush.getChatMessager().write(
                CastleRush.getProperties().get("startEditingArena").replace("%arena", arenaName), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "list", aliases = {"l"}, permission = "castlerush.arena.list"
            , desc = "Lists all arenas")
    public void list(CommandContext context){
        Player player = (Player) context.getSender();

        List<Arena> arenas = ArenaManager.getCache().arenaList;
        String msg = "";

        for(Arena arena : arenas){
            msg += CastleRush.getProperties().get("arenaInList").replace("%arena", arena.getName());
        }

        CastleRush.getChatMessager().write(
                CastleRush.getProperties().get("arenaList").replace("%arenas", msg)
                        .replace("%size", arenas.size() + ""), player);
    }

}
