package de.superioz.cr.command;

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

import java.util.ArrayList;

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
        assert rawUnpreparedArena != null; assert unpreparedArena != null;

        if(!(rawUnpreparedArena.getRawGamePlots().size() >= 1)){
            CastleRush.getChatMessager().send("&cYou are not finished! Add more locations.", player);
            return;
        }

        unpreparedArena.addGamePlot(new GamePlot(rawUnpreparedArena.getRawGamePlots(), LocationUtils.fix(player
                .getLocation().getBlock().getLocation())));
        CastleRush.getChatMessager().send("&7Added a &bnew gameplot &7to the cache!", player);

        rawUnpreparedArena.setRawGamePlots(new ArrayList<>());
    }

    @SubCommand(name = "addwall", permission = "castlerush.addwall")
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
        CastleRush.getChatMessager().send("&7Added a &bnew gamewall &7to the cache", player);
    }

    @SubCommand(name = "finishedit", aliases = "finishcreate", permission = "castlerush.finishEditCreate")
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
            CastleRush.getChatMessager().send("&cYou are not finished!", player);
            return;
        }

        if(!unpreparedArena.isFinished()){
            CastleRush.getChatMessager().send("&cYou are not finished!", player);
            return;
        }

        Arena arena = new Arena(unpreparedArena.getName(), unpreparedArena.getSpawnPoints()
            ,unpreparedArena.getGamePlots(), unpreparedArena.getGameWalls(), unpreparedArena.getItemKit());
        ArenaManager.add(arena);
        CastleRush.getChatMessager().send("&7Arena &b"+unpreparedArena.getName()+" &7added to arena list!", player);
    }

}
