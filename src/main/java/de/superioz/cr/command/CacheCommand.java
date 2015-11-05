package de.superioz.cr.command;

import de.superioz.cr.common.ItemKit;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.RawUnpreparedArena;
import de.superioz.cr.common.arena.UnpreparedArena;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.GameWall;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.java.util.classes.SimplePair;
import de.superioz.library.minecraft.server.command.annts.RawSubCommand;
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import de.superioz.library.minecraft.server.util.LocationUtils;
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

    @SubCommand(name = "cache", aliases = {"c"}, permission = "castlerush.cache"
            , min = 1, usage = "[addplot:addwall:setkit:finish:tool:edit]"
            , desc = "Commands for handling editor's cache")
    public void cache(SubCommandContext context){
        context.forward(this.getClass(), "addplot", "addwall", "setkit", "finish", "tool");
    }

    @RawSubCommand(name = "addplot", aliases = {"addp", "ap"}, permission = "castlerush.cache.addplot"
            , usage = ""
            , desc = "Adds selected plot to editor cache")
    public void addplot(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send(
                    CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        if(!(rawUnpreparedArena.getRawGamePlots().size() >= 1)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("plotNeedsOneLocation"), player);
            return;
        }

        if(unpreparedArena.getGamePlots().size() == 2){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("cannotAddMoreThan")
                            .replace("%n", 2+""), player);
            return;
        }

        unpreparedArena.addGamePlot(new GamePlot(rawUnpreparedArena.getRawGamePlots(), LocationUtils.fix(player
                .getLocation().getBlock().getLocation())));
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("addedANewGameplot")
                .replace("%size", unpreparedArena.getGamePlots().size()+""), player);

        rawUnpreparedArena.setRawGamePlots(new ArrayList<>());
    }

    @RawSubCommand(name = "setkit", aliases = {"setk", "sk"}, permission = "castlerush.cache.setkit"
            , usage = ""
            , desc = "Sets the kit for unprepared arena")
    public void setkit(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        PlayerInventory inv = player.getInventory();
        ItemKit kit = new ItemKit(inv.getContents(), inv.getArmorContents());

        rawUnpreparedArena.setItemKit(kit);
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("setGamekitForCache"), player);
    }

    @RawSubCommand(name = "addwall", aliases = {"addw", "aw"}, permission = "castlerush.cache.addwall"
            , usage = ""
            , desc = "Adds selected wall to editor cache")
    public void addwall(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        if(rawUnpreparedArena.getRawGameWalls() == null
                || rawUnpreparedArena.getRawGameWalls().getType1() == null
                || rawUnpreparedArena.getRawGameWalls().getType2() == null){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youMustMarkTwoLocations"), player);
            return;
        }

        Location pos1 = rawUnpreparedArena.getRawGameWalls().getType1();
        Location pos2 = rawUnpreparedArena.getRawGameWalls().getType2();

        unpreparedArena.addGameWall(new GameWall(new SimplePair<>(pos1, pos2)));
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("addedANewWall").replace("%size",
                        unpreparedArena.getGameWalls().size()+""), player);
    }

    @RawSubCommand(name = "finish", aliases = {"fin", "f"}, permission = "castlerush.cache.finish"
            , usage = ""
            , desc = "Finished the editor cache and saves it")
    public void finish(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        if(rawUnpreparedArena.isFinished()){
            unpreparedArena.setSpawnPoints(rawUnpreparedArena.getSpawnPoints());
            unpreparedArena.setItemKit(rawUnpreparedArena.getItemKit());
        }
        else{
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentFinished")
                    .replace("%reason", rawUnpreparedArena.getNotFinishedReason()), player);
            return;
        }

        if(!unpreparedArena.isFinished()){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("youArentFinished")
                    .replace("%reason", rawUnpreparedArena.getNotFinishedReason()), player);
            return;
        }

        Arena arena = new Arena(unpreparedArena.getName(), unpreparedArena.getSpawnPoints()
                ,unpreparedArena.getGamePlots(), unpreparedArena.getGameWalls(), unpreparedArena.getItemKit());
        ArenaManager.add(arena);
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("arenaAddedToList")
                .replace("%arena", arena.getName()).replace("%size", ArenaManager.size()+""), player);
        ArenaManager.EditorCache.remove(player);
    }

    @RawSubCommand(name = "tool", aliases = {"t"}, permission = "castlerush.cache.tool"
            , usage = ""
            , desc = "Gives you the editor cache multi-tool")
    public void tool(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send(CastleRush.getProperties().get("notInEditorCache"), player);
            return;
        }

        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        CastleRush.getChatMessager().send(CastleRush.getProperties().get("heresYourMultiTool"), player);
    }

}
