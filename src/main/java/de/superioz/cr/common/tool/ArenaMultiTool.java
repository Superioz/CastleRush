package de.superioz.cr.common.tool;

import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.cache.RawUnpreparedArena;
import de.superioz.cr.common.inventory.ArenaMultiToolInventory;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.Utilities;
import de.superioz.library.minecraft.server.common.inventory.InventorySize;
import de.superioz.library.minecraft.server.common.inventory.SuperInventory;
import de.superioz.library.minecraft.server.common.item.SimpleItemTool;
import de.superioz.library.minecraft.server.exception.InventoryCreateException;
import de.superioz.library.minecraft.server.util.GeometryUtil;
import de.superioz.library.minecraft.server.util.LocationUtil;
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
public class ArenaMultiTool extends SimpleItemTool {

    public ArenaMultiToolHoe hoe = new ArenaMultiToolHoe();
    public ArenaMultiToolPickaxe pickaxe = new ArenaMultiToolPickaxe();
    public ArenaMultiToolShovel shovel = new ArenaMultiToolShovel();

    public ArenaMultiTool(){
        super(Utilities.ItemStacks.MULTITOOL_STACK, event -> {
            Action action = event.getAction();
            Player player = event.getPlayer();

            event.getEvent().setCancelled(true);
            if(checkAction(action, player))
                return;

            CastleRush.getChatMessager().write("&6You can't do anything with this. Use Sneak+Rightclick", player);
        });
    }

    public static boolean checkAction(Action action, Player player){
        if((action == Action.RIGHT_CLICK_AIR
                || action == Action.RIGHT_CLICK_BLOCK)
                && player.isSneaking()){
            SuperInventory inv = null;
            try{
                inv = new SuperInventory(CastleRush.getProperties().get("multitoolHeader"), InventorySize
                        .THREE_ROWS).from(ArenaMultiToolInventory.class);
                player.openInventory(inv.build());
                return true;
            }catch(InventoryCreateException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    // ====================================================================================

    public class ArenaMultiToolShovel extends SimpleItemTool {
        public ArenaMultiToolShovel(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_SHOVEL, event -> {
                Action action = event.getAction();
                Player player = event.getPlayer();
                Block block = event.getClickedBlock();

                if(checkAction(action, player))
                    return;

                if(!ArenaManager.EditorCache.contains(player)){
                    return;
                }

                RawUnpreparedArena arena = ArenaManager.EditorCache.get(player);
                assert arena != null;

                event.getEvent().setCancelled(true);
                switch(action){
                    case LEFT_CLICK_BLOCK:
                        if(player.isSneaking()){
                            Location l = LocationUtil.fix(block.getLocation());

                            if(!arena.addGamePlotLocation(new Location(l.getWorld(), l.getX(), 0, l.getZ()))){
                                CastleRush.getChatMessager().write("&cThis position was already added to plot!", player);
                                return;
                            }

                            CastleRush.getChatMessager().write("&7Added @&9"+LocationUtil.toString(l)+" &7to plot.",
                                    player);
                            break;
                        }

                        // Set Position 2
                        if(arena.getRawGamePlotMarker().getType1() == null){
                            // Location 1 is not set
                            CastleRush.getChatMessager().write("&cSet 1st Plot-location first!", player);
                            break;
                        }

                        Location pos1 = arena.getRawGamePlotMarker().getType1();
                        Location pos2 = LocationUtil.fix(block.getLocation());

                        List<Location> locs = GeometryUtil.calcCuboid(pos1, pos2);
                        for(Location lo : locs){
                            Location location = LocationUtil.fix(lo.getBlock().getLocation());
                            Location fixedLocation = new Location(
                                    location.getWorld(), location.getX(), 0, location.getZ());

                            if(!arena.addGamePlotLocation(fixedLocation)){
                                CastleRush.getChatMessager().write("&cOne of these positions was already added to " +
                                        "plot!", player);
                                return;
                            }
                        }

                        CastleRush.getChatMessager().write("&7Plot-position &e#2 set &7and added marked locations to " +
                                        "plot", player);

                        // remove from
                        arena.getRawGamePlotMarker().setType1(null);
                        arena.getRawGamePlotMarker().setType2(null);

                        CastleRush.getChatMessager().write("&7@&9"
                                + LocationUtil.toString(pos1) + "&7 - @&9"
                                + LocationUtil.toString(pos2), player);
                        break;
                    case RIGHT_CLICK_BLOCK:
                        //Set position 2
                        Location pos = LocationUtil.fix(block.getLocation());

                        arena.getRawGamePlotMarker().setType1(pos);
                        CastleRush.getChatMessager().write("&7Plot-position &e#1 set &7@&9"+LocationUtil.toString(pos),
                                player);
                        break;
                }
            });
        }
    }

    public class ArenaMultiToolPickaxe extends SimpleItemTool {
        public ArenaMultiToolPickaxe(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_PICKAXE, event -> {
                Action action = event.getAction();
                Player player = event.getPlayer();
                Block block = event.getClickedBlock();

                if(checkAction(action, player))
                    return;

                if(!ArenaManager.EditorCache.contains(player)){
                    return;
                }

                RawUnpreparedArena arena = ArenaManager.EditorCache.get(player);
                assert arena != null;

                event.getEvent().setCancelled(true);
                switch(action){
                    case RIGHT_CLICK_BLOCK:
                        // Position 1
                        Location pos1 = LocationUtil.fix(block.getLocation());

                        arena.getRawGameWalls().setType1(pos1);
                        CastleRush.getChatMessager().write("&7Wall-position &e#1 set &7@&9"+LocationUtil.toString(pos1),
                                player);
                        break;
                    case LEFT_CLICK_BLOCK:
                        // Position 2
                        Location pos2 = LocationUtil.fix(block.getLocation());

                        arena.getRawGameWalls().setType2(pos2);
                        CastleRush.getChatMessager().write("&7Wall-position &e#2 set &7@&9"+LocationUtil.toString(pos2),
                                player);
                        break;
                }
            });
        }
    }

    public class ArenaMultiToolHoe extends SimpleItemTool {
        public ArenaMultiToolHoe(){
            super(Utilities.ItemStacks.MULTITOOL_STACK_HOE, event -> {
                Action action = event.getAction();
                Player player = event.getPlayer();
                Block block = event.getClickedBlock();

                if(checkAction(action, player))
                    return;

                if(!ArenaManager.EditorCache.contains(player)){
                    return;
                }

                RawUnpreparedArena arena = ArenaManager.EditorCache.get(player);
                assert arena != null;

                event.getEvent().setCancelled(true);
                switch(action){
                    case RIGHT_CLICK_BLOCK:
                        Location pos = LocationUtil.fix(block.getLocation());
                        boolean b = arena.addSpawnpoint(pos);
                        String text = b ? "&7Spawnpoint &aadded &7@&9"+LocationUtil.toString(pos)
                                + " &7[&b" + arena.getSpawnPoints().size() + "&7]"
                                : "&cThis spawnpoint already exists!";

                        CastleRush.getChatMessager().write(text, player);
                        break;
                    case LEFT_CLICK_BLOCK:
                        Location pos1 = LocationUtil.fix(block.getLocation());
                        boolean b1 = arena.removeSpawnpoint(pos1);
                        String text1 = b1 ? "&7Spawnpoint &cremoved &7@&9"+LocationUtil.toString(pos1)
                                + " &7[&b" + arena.getSpawnPoints().size() + "&7]"
                                : "&cThis spawnpoint doesn't exist!";

                        CastleRush.getChatMessager().write(text1, player);
                        break;
                }
            });
        }
    }

}
