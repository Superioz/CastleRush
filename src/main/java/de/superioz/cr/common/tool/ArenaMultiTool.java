package de.superioz.cr.common.tool;

import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.RawUnpreparedArena;
import de.superioz.cr.common.inventory.ArenaMultiToolInventory;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.items.ItemTool;
import de.superioz.library.minecraft.server.util.LocationUtils;
import de.superioz.library.minecraft.server.util.geometry.GeometryUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.List;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ArenaMultiTool extends ItemTool {

    public ArenaMultiToolHoe hoe = new ArenaMultiToolHoe();
    public ArenaMultiToolPickaxe pickaxe = new ArenaMultiToolPickaxe();
    public ArenaMultiToolShovel shovel = new ArenaMultiToolShovel();

    public ArenaMultiTool(){
        super(Utilities.ItemStacks.MULTITOOL_STACK, event -> {
            Action action = event.action();
            Player player = event.player();

            event.cancel();
            if(checkAction(action, player))
                return;

            CastleRush.getChatMessager().send("&6You can't do anything with this. Use Sneak+Rightclick", player);
        });
    }

    public static boolean checkAction(Action action, Player player){
        if((action == Action.RIGHT_CLICK_AIR
                || action == Action.RIGHT_CLICK_BLOCK)
                && player.isSneaking()){
            ArenaMultiToolInventory inv = new ArenaMultiToolInventory();
            inv.load();
            player.openInventory(inv.build());
            return true;
        }
        return false;
    }

    // ====================================================================================

    public class ArenaMultiToolShovel extends ItemTool {
        public ArenaMultiToolShovel(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL, event -> {
                Action action = event.action();
                Player player = event.player();
                Block block = event.clickedBlock();

                if(checkAction(action, player))
                    return;

                if(!ArenaManager.EditorCache.contains(player)){
                    return;
                }

                RawUnpreparedArena arena = ArenaManager.EditorCache.get(player);
                assert arena != null;

                event.cancel();
                switch(action){
                    case LEFT_CLICK_BLOCK:
                        if(player.isSneaking()){
                            Location l = LocationUtils.fix(block.getLocation());

                            if(!arena.addGamePlotLocation(new Location(l.getWorld(), l.getX(), 0, l.getZ()))){
                                CastleRush.getChatMessager().send("&cThis position was already added to plot!", player);
                                return;
                            }

                            CastleRush.getChatMessager().send("&7Added @&9"+LocationUtils.toString(l)+" &7to plot.",
                                    player);
                            break;
                        }

                        // Set Position 2
                        if(arena.getRawGamePlotMarker().getType1() == null){
                            // Location 1 is not set
                            CastleRush.getChatMessager().send("&cSet 1st Plot-location first!", player);
                            break;
                        }

                        Location pos1 = arena.getRawGamePlotMarker().getType1();
                        Location pos2 = LocationUtils.fix(block.getLocation());

                        List<Location> locs = GeometryUtils.cuboid(pos1, pos2);
                        for(Location lo : locs){
                            Location location = LocationUtils.fix(lo.getBlock().getLocation());
                            Location fixedLocation = new Location(
                                    location.getWorld(), location.getX(), 0, location.getZ());

                            if(!arena.addGamePlotLocation(fixedLocation)){
                                CastleRush.getChatMessager().send("&cOne of these positions was already added to " +
                                        "plot!", player);
                                return;
                            }
                        }

                        CastleRush.getChatMessager().send("&7Plot-position &e#2 set &7and added marked locations to " +
                                        "plot", player);

                        // remove from
                        arena.getRawGamePlotMarker().setType1(null);
                        arena.getRawGamePlotMarker().setType2(null);

                        CastleRush.getChatMessager().send("&7@&9"
                                + LocationUtils.toString(pos1) + "&7 - @&9"
                                + LocationUtils.toString(pos2), player);
                        break;
                    case RIGHT_CLICK_BLOCK:
                        //Set position 2
                        Location pos = LocationUtils.fix(block.getLocation());

                        arena.getRawGamePlotMarker().setType1(pos);
                        CastleRush.getChatMessager().send("&7Plot-position &e#1 set &7@&9"+LocationUtils.toString(pos),
                                player);
                        break;
                }
            });
        }
    }

    public class ArenaMultiToolPickaxe extends ItemTool {
        public ArenaMultiToolPickaxe(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE, event -> {
                Action action = event.action();
                Player player = event.player();
                Block block = event.clickedBlock();

                if(checkAction(action, player))
                    return;

                if(!ArenaManager.EditorCache.contains(player)){
                    return;
                }

                RawUnpreparedArena arena = ArenaManager.EditorCache.get(player);
                assert arena != null;

                event.cancel();
                switch(action){
                    case RIGHT_CLICK_BLOCK:
                        // Position 1
                        Location pos1 = LocationUtils.fix(block.getLocation());

                        arena.getRawGameWalls().setType1(pos1);
                        CastleRush.getChatMessager().send("&7Wall-position &e#1 set &7@&9"+LocationUtils.toString(pos1),
                                player);
                        break;
                    case LEFT_CLICK_BLOCK:
                        // Position 2
                        Location pos2 = LocationUtils.fix(block.getLocation());

                        arena.getRawGameWalls().setType2(pos2);
                        CastleRush.getChatMessager().send("&7Wall-position &e#2 set &7@&9"+LocationUtils.toString(pos2),
                                player);
                        break;
                }
            });
        }
    }

    public class ArenaMultiToolHoe extends ItemTool {
        public ArenaMultiToolHoe(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_HOE, event -> {
                Action action = event.action();
                Player player = event.player();
                Block block = event.clickedBlock();

                if(checkAction(action, player))
                    return;

                if(!ArenaManager.EditorCache.contains(player)){
                    return;
                }

                RawUnpreparedArena arena = ArenaManager.EditorCache.get(player);
                assert arena != null;

                Location pos = LocationUtils.fix(block.getLocation());
                event.cancel();
                switch(action){
                    case RIGHT_CLICK_BLOCK:
                        boolean b = arena.addSpawnpoint(pos);
                        String text = b ? "&7Spawnpoint &aadded &7@&9"+LocationUtils.toString(pos) : "&cThis " +
                                "spawnpoint already exists!";

                        CastleRush.getChatMessager().send(text, player);
                        break;
                    case LEFT_CLICK_BLOCK:
                        boolean b1 = arena.removeSpawnpoint(pos);
                        String text1 = b1 ? "&7Spawnpoint &cremoved &7@&9"+LocationUtils.toString(pos) : "&cThis " +
                                "spawnpoint doesn't exist!";

                        CastleRush.getChatMessager().send(text1, player);
                        break;
                }
            });
        }
    }

}
