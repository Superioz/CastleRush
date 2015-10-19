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
import de.superioz.library.minecraft.server.command.annts.SubCommand;
import de.superioz.library.minecraft.server.command.cntxt.SubCommandContext;
import de.superioz.library.minecraft.server.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaCommand {

    @SubCommand(name = "edit", aliases = "create", permission = "castlerush.editAndCreate"
        , min = 1, usage = "[arenaName]", desc = "Start editing/creating an arena")
    public void editAndCreate(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        // Check if player already started to edit/create an arena
        if(ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou are already in the EditorCache!", player);
            return;
        }

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

        ArenaManager.EditorCache.addPlayer(player, arenaName);
        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        CastleRush.getChatMessager().send("&7You can now edit/create the arena &b" + arenaName + "&7!", player);
    }

    @SubCommand(name = "addplot", permission = "castlerush.addplot", desc = "Adds selected plot to cache")
    public void addPlot(SubCommandContext commandContext){
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

    @SubCommand(name = "setkit", permission = "castlerush.setkit", desc = "Sets your inventory as itemkit")
    public void setItemKit(SubCommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        RawUnpreparedArena rawUnpreparedArena = ArenaManager.EditorCache.get(player);
        UnpreparedArena unpreparedArena = ArenaManager.EditorCache.getLast(player);
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        PlayerInventory inv = player.getInventory();
        ItemKit kit = new ItemKit(inv.getContents(),
                new ItemStack[]{
                        inv.getHelmet(), inv.getChestplate(),
                        inv.getLeggings(), inv.getBoots()
                });

        rawUnpreparedArena.setItemKit(kit);
        CastleRush.getChatMessager().send("&7Set the &bgamekit &7for your cache!", player);
    }

    @SubCommand(name = "addwall", permission = "castlerush.addwall", desc = "Adds selected wall to cache")
    public void addWall(SubCommandContext commandContext){
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

    @SubCommand(name = "finishedit", aliases = "finishcreate", permission = "castlerush.finishEditCreate"
        , desc = "Finished the cache and creates the arena finally")
    public void finishEdit(SubCommandContext context){
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

    @SubCommand(name = "getmultitool", aliases = "multitool", permission = "castlerush.multitool"
        , desc = "Gives you the multitool")
    public void getTool(SubCommandContext context){
        Player player = (Player) context.getSender();

        if(!ArenaManager.EditorCache.contains(player)){
            CastleRush.getChatMessager().send("&cYou aren't in the EditorCache!", player);
            return;
        }

        player.setItemInHand(Utilities.ItemStacks.MULTITOOL_STACK);
        CastleRush.getChatMessager().send("&7Here is your wanted &eMultitool&7!", player);
    }

}
