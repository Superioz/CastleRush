package de.superioz.cr.command;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.cache.RawUnpreparedArena;
import de.superioz.cr.common.arena.cache.UnpreparedArena;
import de.superioz.cr.common.arena.object.Arena;
import de.superioz.cr.common.game.objects.GamePlot;
import de.superioz.cr.common.game.objects.GameWall;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.common.command.SubCommand;
import de.superioz.library.minecraft.server.common.command.context.CommandContext;
import de.superioz.library.minecraft.server.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class CacheCommand {

    @SubCommand(label = "cache", aliases = {"c"}, permission = "castlerush.cache"
            , usage = ""
            , desc = "Commands for handling editor's cache")
    public void cache(CommandContext context){
        CastleRush.getChatMessager().write("&c/cr cache [addplot:addwall:setkit:finish:tool:leave]",
                (Player)context.getSender());
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "info", aliases = {"i"}, permission = "castlerush.cache.info"
            , usage = ""
            , desc = "Adds selected wall to editor cache")
    public void info(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(
                    CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        CastleRush.getChatMessager().write(
                "&7Name: &b" + unpreparedArena.getName() + "&8; "
                        + "&b" + unpreparedArena.getGameWalls().size() + " &7wall(s)&8; "
                        + "&b" + unpreparedArena.getGamePlots().size() + " &7plot(s)&8; "
                        + "&b" + rawUnpreparedArena.getSpawnPoints().size() + " &7spawn(s)&8; "
                        + "&7Kit " + ((rawUnpreparedArena.getItemKit() != null) ? "&aexist" : "&cdoesn't exist")
                , player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "addplot", aliases = {"addp", "ap"}, permission = "castlerush.cache.addplot"
            , usage = ""
            , desc = "Adds selected plot to editor cache")
    public void addplot(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(
                    CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        if(!(rawUnpreparedArena.getRawGamePlots().size() >= 1)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("plotNeedsOneLocation"), player);
            return;
        }

        if(unpreparedArena.getGamePlots().size() >= 2){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("cannotAddMoreThan")
                    .replace("%n", 2 + ""), player);
            return;
        }

        unpreparedArena.addGamePlot(new GamePlot(rawUnpreparedArena.getRawGamePlots(), LocationUtil.fix(player
                .getLocation().getBlock().getLocation())));
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("addedANewGameplot")
                .replace("%size", unpreparedArena.getGamePlots().size() + ""), player);

        rawUnpreparedArena.setRawGamePlots(new ArrayList<>());
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "setkit", aliases = {"setk", "sk"}, permission = "castlerush.cache.setkit"
            , usage = ""
            , desc = "Sets the kit for unprepared arena")
    public void setkit(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        PlayerInventory inv = player.getInventory();
        ItemKit kit = new ItemKit(inv.getContents(), inv.getArmorContents());

        rawUnpreparedArena.setItemKit(kit);
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("setGamekitForCache"), player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "addwall", aliases = {"addw", "aw"}, permission = "castlerush.cache.addwall"
            , usage = ""
            , desc = "Adds selected wall to editor cache")
    public void addwall(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        if(rawUnpreparedArena.getRawGameWalls() == null
                || rawUnpreparedArena.getRawGameWalls().getType1() == null
                || rawUnpreparedArena.getRawGameWalls().getType2() == null){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("youMustMarkTwoLocations"), player);
            return;
        }

        if(unpreparedArena.getGamePlots().size() >= 27){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("cannotAddMoreThan")
                    .replace("%n", 27 + ""), player);
            return;
        }

        Location pos1 = rawUnpreparedArena.getRawGameWalls().getType1();
        Location pos2 = rawUnpreparedArena.getRawGameWalls().getType2();

        unpreparedArena.addGameWall(new GameWall(new SimplePair<>(pos1, pos2)));
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("addedANewWall").replace("%size",
                unpreparedArena.getGameWalls().size() + ""), player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "finish", aliases = {"fin", "f"}, permission = "castlerush.cache.finish"
            , usage = ""
            , desc = "Finished the editor cache and saves it")
    public void finish(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        if(rawUnpreparedArena.isFinished()){
            unpreparedArena.setSpawnPoints(rawUnpreparedArena.getSpawnPoints());
            unpreparedArena.setItemKit(rawUnpreparedArena.getItemKit());
        }else{
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("youArentFinished")
                    .replace("%reason", rawUnpreparedArena.getNotFinishedReason()), player);
            return;
        }

        if(!unpreparedArena.isFinished()){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("youArentFinished")
                    .replace("%reason", rawUnpreparedArena.getNotFinishedReason()), player);
            return;
        }

        Arena arena = new Arena(unpreparedArena.getName(), unpreparedArena.getSpawnPoints()
                , unpreparedArena.getGamePlots(), unpreparedArena.getGameWalls(), unpreparedArena.getItemKit());
        ArenaManager.add(arena);
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("arenaAddedToList")
                .replace("%arena", arena.getName()).replace("%size", ArenaManager.size() + ""), player);
        ArenaManager.EditorCache.remove(player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "tool", aliases = {"t"}, permission = "castlerush.cache.tool"
            , usage = ""
            , desc = "Gives you the editor cache multi-tool")
    public void tool(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK.getWrappedStack());
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("heresYourMultiTool"), player);
    }

    // ========================================================================================================

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "delplots", permission = "castlerush.cache.edit.delplots"
            , desc = "Edits the cache")
    public void delplots(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        unpreparedArena.getGamePlots().clear();
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("clearedPlotsInCache"), player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "delwalls", permission = "castlerush.cache.edit.delwalls"
            , desc = "Edits the cache")
    public void delwalls(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        unpreparedArena.getGameWalls().clear();
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("clearedWallsInCache"), player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "delspawns", permission = "castlerush.cache.edit.delspawns"
            , desc = "Edits the cache")
    public void delspawns(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null;
        assert unpreparedArena != null;

        rawUnpreparedArena.getSpawnPoints().clear();
        CastleRush.getChatMessager().write(CastleRush.getProperties().get("clearedSpawnsInCache"), player);
    }

    @SubCommand.Nested(parent = "cache")
    @SubCommand(label = "leave", permission = "castlerush.cache.edit.delwalls"
            , desc = "Edits the cache")
    public void leave(CommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }
        ArenaManager.EditorCache.remove(player);

        CastleRush.getChatMessager().write(CastleRush.getProperties().get("removedFromCache"), player);
    }

}
