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
        context.forward(this.getClass(), "addplot", "addwall", "setkit", "finish", "tool", "edit");
    }

    @RawSubCommand(name = "addplot", aliases = {"addp", "ap"}, permission = "castlerush.cache.addplot"
            , usage = ""
            , desc = "Adds selected plot to editor cache")
    public void addplot(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        if(!(rawUnpreparedArena.getRawGamePlots().size() >= 1)){
            CastleRush.getChatMessager().send("&cA plot needs one location!", player);
            return;
        }

        unpreparedArena.addGamePlot(new GamePlot(rawUnpreparedArena.getRawGamePlots(), LocationUtils.fix(player
                .getLocation().getBlock().getLocation())));
        CastleRush.getChatMessager().send("&7Added a &bnew gameplot &7to the cache! " +
                "&7[&b"+unpreparedArena.getGamePlots().size()+"&7/2]", player);

        rawUnpreparedArena.setRawGamePlots(new ArrayList<>());
    }

    @RawSubCommand(name = "setkit", aliases = {"setk", "sk"}, permission = "castlerush.cache.setkit"
            , usage = ""
            , desc = "Sets the kit for unprepared arena")
    public void setkit(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        PlayerInventory inv = player.getInventory();
        ItemKit kit = new ItemKit(inv.getContents(), inv.getArmorContents());

        rawUnpreparedArena.setItemKit(kit);
        CastleRush.getChatMessager().send("&7Set the &bgamekit &7for your cache!", player);
    }

    @RawSubCommand(name = "addwall", aliases = {"addw", "aw"}, permission = "castlerush.cache.addwall"
            , usage = ""
            , desc = "Adds selected wall to editor cache")
    public void addwall(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        if(rawUnpreparedArena.getRawGameWalls() == null
                || rawUnpreparedArena.getRawGameWalls().getType1() == null
                || rawUnpreparedArena.getRawGameWalls().getType2() == null){
            CastleRush.getChatMessager().send("&cYou must mark two locations!", player);
            return;
        }

        Location pos1 = rawUnpreparedArena.getRawGameWalls().getType1();
        Location pos2 = rawUnpreparedArena.getRawGameWalls().getType2();

        unpreparedArena.addGameWall(new GameWall(new SimplePair<>(pos1, pos2)));
        CastleRush.getChatMessager().send("&7Added a &bnew gamewall &7to the cache " +
                "&7[&b"+unpreparedArena.getGameWalls().size()+ "&7/1]", player);
    }

    @RawSubCommand(name = "finish", aliases = {"fin", "f"}, permission = "castlerush.cache.finish"
            , usage = ""
            , desc = "Finished the editor cache and saves it")
    public void finish(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
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
            CastleRush.getChatMessager().send("&cYou aren't finished! &7["
                    + rawUnpreparedArena.getNotFinishedReason().toUpperCase()+"&7]", player);
            return;
        }

        if(!unpreparedArena.isFinished()){
            CastleRush.getChatMessager().send("&cYou aren't finished! &7["
                    + unpreparedArena.getNotFinishedReason().toUpperCase()+"&7]", player);
            return;
        }

        Arena arena = new Arena(unpreparedArena.getName(), unpreparedArena.getSpawnPoints()
                ,unpreparedArena.getGamePlots(), unpreparedArena.getGameWalls(), unpreparedArena.getItemKit());
        ArenaManager.add(arena);
        CastleRush.getChatMessager().send("&7Arena &b"+unpreparedArena.getName()+" &7added to arena list! " +
                "[&b"+ArenaManager.size()+"&7]", player);
    }

    @RawSubCommand(name = "tool", aliases = {"t"}, permission = "castlerush.cache.tool"
            , usage = ""
            , desc = "Gives you the editor cache multi-tool")
    public void tool(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        CastleRush.getChatMessager().send("&7Here is your wanted &eMultitool&7!", player);
    }

}
