package de.superioz.cr.command;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.cache.EditorCache;
import de.superioz.cr.common.arena.ItemKit;
import de.superioz.cr.common.arena.raw.RawUnpreparedArena;
import de.superioz.cr.common.arena.raw.UnpreparedArena;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.GameWall;
import de.superioz.cr.common.game.team.TeamColor;
import de.superioz.cr.common.tool.IngameTeleportTool;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.PluginColor;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.bukkit.common.command.Command;
import de.superioz.library.bukkit.common.command.CommandWrapper;
import de.superioz.library.bukkit.common.command.context.CommandContext;
import de.superioz.library.bukkit.util.LocationUtil;
import de.superioz.library.bukkit.util.SerializeUtil;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.java.util.list.ListUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class CacheCommand {

    @Command(label = "cache", aliases = {"c"}, permission = "castlerush.cache"
            , desc = "Commands for handling editor's cache")
    public void cache(CommandContext context){
        List<String> subCommands = context.getCommand().getSubCommands().stream().map(CommandWrapper::getLabel)
                .collect(Collectors.toList());

        ChatManager.info().write("&c/cr cache [" + ListUtil.insert(subCommands, ":") +"]",
                (Player)context.getSender());
    }

    @Command.Nested(parent = "cache")
    @Command(label = "info", aliases = {"i"}, permission = "castlerush.cache.info"
            , desc = "Adds selected wall to editor cache")
    public void info(CommandContext context){
        Player player = (Player) context.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(
                    LanguageManager.get("notInEditorCache"), player);
            return;
        }

        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert unpreparedArena != null;

        // Get strings to send
        String spacer = PluginColor.DARK + "; ";
        String name = PluginColor.LIGHT + "Name: " + PluginColor.ICE + unpreparedArena.getName();
        String walls = PluginColor.ICE + unpreparedArena.getGameWalls().size() + " " + PluginColor.LIGHT + "wall(s)";
        String plots = PluginColor.ICE + unpreparedArena.getGamePlots().size() + " " + PluginColor.LIGHT + "plot(s)";
        String spawn = PluginColor.LIGHT + "Spawn " + ((unpreparedArena.getSpawnPoint() != null)
                ? PluginColor.LIME + "exist" : PluginColor.RED + "doesn't exist");
        String kit = PluginColor.LIGHT + "Kit " + ((unpreparedArena.getItemKit() != null)
                ? PluginColor.LIME + "exist" : PluginColor.RED + "doesn't exist");

        // Send at least
        ChatManager.info().write(
                name + spacer + walls + spacer + plots + spacer + spacer + spacer + kit, player);
    }

    @Command.Nested(parent = "cache")
    @Command(label = "addplot", aliases = {"addp", "ap"}, permission = "castlerush.cache.addplot"
            , desc = "Adds selected plot to editor cache")
    public void addplot(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(
                    LanguageManager.get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = EditorCache.get(player);
        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        if(!(rawUnpreparedArena.getRawGamePlots().size() >= 1)){
            ChatManager.info().write(LanguageManager.get("plotNeedsOneLocation"), player);
            return;
        }

        if(unpreparedArena.getGamePlots().size() >= TeamColor.values().length){
            ChatManager.info().write(LanguageManager.get("cannotAddMoreThan")
                    .replace("%n", 2 + ""), player);
            return;
        }

        unpreparedArena.addGamePlot(new GamePlot(rawUnpreparedArena.getRawGamePlots(), LocationUtil.fix(player
                .getLocation().getBlock().getLocation())));
        ChatManager.info().write(LanguageManager.get("addedANewGameplot")
                .replace("%size", unpreparedArena.getGamePlots().size() + ""), player);

        rawUnpreparedArena.setRawGamePlots(new ArrayList<>());
    }

    @Command.Nested(parent = "cache")
    @Command(label = "setkit", aliases = {"setk", "sk"}, permission = "castlerush.cache.setkit"
            , desc = "Sets the kit for unprepared arena")
    public void setkit(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }

        if(player.getInventory().getItem(IngameTeleportTool.SLOT) != null
                && player.getInventory().getItem(IngameTeleportTool.SLOT).getType() != Material.AIR){
            ChatManager.info().write(LanguageManager.get("slotMustBeFree")
                    .replace("%slot", IngameTeleportTool.SLOT + ""), player);
            return;
        }

        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert unpreparedArena != null;

        PlayerInventory inv = player.getInventory();
        ItemKit kit = new ItemKit(inv.getContents(), inv.getArmorContents());

        unpreparedArena.setItemKit(kit);
        ChatManager.info().write(LanguageManager.get("setGamekitForCache"), player);
    }

    @Command.Nested(parent = "cache")
    @Command(label = "addwall", aliases = {"addw", "aw"}, permission = "castlerush.cache.addwall"
            , desc = "Adds selected wall to editor cache")
    public void addwall(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = EditorCache.get(player);
        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        if(rawUnpreparedArena.getRawGameWalls() == null
                || rawUnpreparedArena.getRawGameWalls().getType1() == null
                || rawUnpreparedArena.getRawGameWalls().getType2() == null){
            ChatManager.info().write(LanguageManager.get("youMustMarkTwoLocations"), player);
            return;
        }

        if(unpreparedArena.getGamePlots().size() >= TeamColor.values().length){
            ChatManager.info().write(LanguageManager.get("cannotAddMoreThan")
                    .replace("%n", TeamColor.values().length + ""), player);
            return;
        }

        Location pos1 = rawUnpreparedArena.getRawGameWalls().getType1();
        Location pos2 = rawUnpreparedArena.getRawGameWalls().getType2();

        unpreparedArena.addGameWall(new GameWall(new SimplePair<>(pos1, pos2)));
        ChatManager.info().write(LanguageManager.get("addedANewWall").replace("%size",
                unpreparedArena.getGameWalls().size() + ""), player);

        rawUnpreparedArena.setRawGameWalls(new SimplePair<>(null, null));
    }

    @Command.Nested(parent = "cache")
    @Command(label = "finish", aliases = {"fin", "f"}, permission = "castlerush.cache.finish"
            , desc = "Finished the editor cache and saves it")
    public void finish(CommandContext context){
        Player player = (Player) context.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }

        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert unpreparedArena != null;

        if(unpreparedArena.isFinished()){
            unpreparedArena.setItemKit(unpreparedArena.getItemKit());
        }else{
            ChatManager.info().write(LanguageManager.get("youArentFinished")
                    .replace("%reason", unpreparedArena.getNotFinishedReason()), player);
            return;
        }

        if(!unpreparedArena.isFinished()){
            ChatManager.info().write(LanguageManager.get("youArentFinished")
                    .replace("%reason", unpreparedArena.getNotFinishedReason()), player);
            return;
        }

        ChatManager.info().write(LanguageManager.get("setArenaToList"), player);
        new BukkitRunnable() {
            @Override
            public void run(){
                Arena arena = new Arena(unpreparedArena.getName(),
                        SerializeUtil.toString(unpreparedArena.getSpawnPoint()),
                        unpreparedArena.getGamePlots(),
                        unpreparedArena.getGameWalls(),
                        unpreparedArena.getItemKit());
                ArenaManager.add(arena);
                ChatManager.info().write(LanguageManager.get("arenaAddedToList")
                        .replace("%arena", arena.getName()).replace("%size", ArenaManager.size() + ""), player);
                EditorCache.remove(player);
            }
        }.runTaskLater(CastleRush.getInstance(), 1L);
    }

    @Command.Nested(parent = "cache")
    @Command(label = "tool", aliases = {"t"}, permission = "castlerush.cache.tool"
            , desc = "Gives you the editor cache multi-tool")
    public void tool(CommandContext context){
        Player player = (Player) context.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }

        player.setItemInHand(PluginItems.MULTITOOL_STACK.getWrappedStack());
        ChatManager.info().write(LanguageManager.get("heresYourMultiTool"), player);
    }

    // ========================================================================================================

    @Command.Nested(parent = "cache")
    @Command(label = "delplots", permission = "castlerush.cache.edit.delplots"
            , desc = "Edits the cache")
    public void delplots(CommandContext context){
        Player player = (Player) context.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = EditorCache.get(player);
        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        unpreparedArena.getGamePlots().clear();
        ChatManager.info().write(LanguageManager.get("clearedPlotsInCache"), player);
    }

    @Command.Nested(parent = "cache")
    @Command(label = "delwalls", permission = "castlerush.cache.edit.delwalls"
            , desc = "Edits the cache")
    public void delwalls(CommandContext context){
        Player player = (Player) context.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = EditorCache.get(player);
        UnpreparedArena unpreparedArena = EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        unpreparedArena.getGameWalls().clear();
        ChatManager.info().write(LanguageManager.get("clearedWallsInCache"), player);
    }

    @Command.Nested(parent = "cache")
    @Command(label = "leave", permission = "castlerush.cache.leave"
            , desc = "Leaves the cache")
    public void leave(CommandContext context){
        Player player = (Player) context.getSender();

        if(!EditorCache.contains(player)){
            ChatManager.info().write(LanguageManager.get("notInEditorCache"), player);
            return;
        }
        EditorCache.removeForSure(player);

        ChatManager.info().write(LanguageManager.get("removedFromCache"), player);
    }

}
