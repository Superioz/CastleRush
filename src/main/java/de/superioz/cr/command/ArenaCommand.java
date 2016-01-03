package de.superioz.cr.command;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.cache.EditorCache;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.java.util.list.ListUtil;
import de.superioz.library.minecraft.server.common.command.CommandWrapper;
import de.superioz.library.minecraft.server.common.command.SubCommand;
import de.superioz.library.minecraft.server.common.command.context.CommandContext;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaCommand {

    @SubCommand(label = "arena", aliases = {"ar", "a"}, permission = "castlerush.arena"
            , usage = "", desc = "Commands for handling arenas")
    public void arenaCommand(CommandContext context){
        List<String> subCommands = context.getCommand().getSubCommands().stream().map(CommandWrapper::getLabel)
                .collect(Collectors.toList());

        ChatManager.info().write("&c/cr arena [" + ListUtil.insert(subCommands, ":") +"]",
                (Player)context.getSender());
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "create", aliases = {"crea", "c"}, permission = "castlerush.arena.create"
            , min = 1, usage = "[arenaName]", desc = "Creates an arena. Puts you into editor cache")
    public void create(CommandContext context){
        Player player = (Player) context.getSender();

        // Check if player already started to edit/create an arena
        if(EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("alreadyInEditorCache"), player);
            return;
        }

        String arenaName = ArenaManager.getName(context, 1);

        // Check if name of arena typed in is valid
        if(!ArenaManager.checkArenaName(arenaName)
                || EditorCache.contains(arenaName)){
            ChatManager.info().write(LanguageManager.get("nameNotValid")
                    + " ["+arenaName+"]", player);

            return;
        }

        EditorCache.addPlayer(player, arenaName);
        player.setItemInHand(PluginItems.MULTITOOL_STACK.getWrappedStack());
        ChatManager.info().write(
                LanguageManager.get("startCreatingArena").replace("%arena", arenaName), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "delete", aliases = {"del", "d"}, permission = "castlerush.arena.delete"
            , min = 1, usage = "[arenaName]", desc = "Deletes an arena")
    public void delete(CommandContext context){
        Player player = (Player) context.getSender();

        String arenaName = ArenaManager.getName(context, 1);

        Arena ar  = ArenaManager.get(arenaName);
        if(ar == null){
            ChatManager.info().write(LanguageManager.get("arenaDoesntExist"), player);
            return;
        }

        ArenaManager.getCache().remove(ar);
        ChatManager.info().write(
                LanguageManager.get("arenaRemoved").replace("%arena", arenaName), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "edit", aliases = {"e"}, permission = "castlerush.arena.edit"
            , min = 1, usage = "[arenaName]", desc = "Edits an arena. Puts you into editor cache")
    public void edit(CommandContext context){
        Player player = (Player) context.getSender();

        // Check if player already started to edit/create an arena
        if(EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("alreadyInEditorCache"), player);
            return;
        }

        String arenaName = ArenaManager.getName(context, 1);

        // Check if arena not exists
        Arena ar  = ArenaManager.get(arenaName);
        if(ar == null){
            ChatManager.info().write(LanguageManager.get("arenaDoesntExist"), player);
            return;
        }

        EditorCache.insert(player, ar);
        player.setItemInHand(PluginItems.MULTITOOL_STACK.getWrappedStack());
        ChatManager.info().write(
                LanguageManager.get("startEditingArena").replace("%arena", arenaName), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "list", aliases = {"l"}, permission = "castlerush.arena.list"
            , desc = "Lists all arenas")
    public void list(CommandContext context){
        Player player = (Player) context.getSender();

        List<Arena> arenas = ArenaManager.getCache().arenaList;
        String msg = "";

        for(Arena arena : arenas){
            msg += LanguageManager.get("arenaInList").replace("%arena", arena.getName());
        }

        ChatManager.info().write(
                LanguageManager.get("arenaList").replace("%arenas", msg)
                        .replace("%size", arenas.size() + ""), player);
    }

    @SubCommand.Nested(parent = "arena")
    @SubCommand(label = "teleport", aliases = {"tp"}, permission = "castlerush.arena.teleport"
            , desc = "Teleports you to an arena", min = 1, usage = "[arena]")
    public void teleport(CommandContext context){
        Player player = (Player) context.getSender();

        String s = ArenaManager.getName(context, 1);

        // Check if arena not exists
        Arena ar = ArenaManager.get(s);
        if(ar == null){
            ChatManager.info().write(LanguageManager.get("arenaDoesntExist"), player);
            return;
        }

        ArenaManager.reload();
        player.teleport(ar.getSpawnPoint());
        ChatManager.info().write(LanguageManager.get("teleportedToArena").replace("%arena", s), player);
    }

}
