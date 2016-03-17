package de.superioz.cr.util;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.cache.EditorCache;
import de.superioz.cr.common.arena.raw.RawUnpreparedArena;
import de.superioz.cr.common.arena.raw.UnpreparedArena;
import de.superioz.cr.common.tool.ArenaMultiTool;
import de.superioz.cr.common.tool.ArenaMultiToolInventory;
import de.superioz.library.bukkit.common.inventory.InventorySize;
import de.superioz.library.bukkit.common.inventory.SuperInventory;
import de.superioz.library.bukkit.common.item.SimpleItemTool;
import de.superioz.library.bukkit.exception.InventoryCreateException;
import de.superioz.library.bukkit.util.GeometryUtil;
import de.superioz.library.bukkit.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Set;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class PluginTools {

    public static final SimpleItemTool MULTI_TOOL = new ArenaMultiTool(PluginItems.MULTITOOL_STACK, event -> {
        Action action = event.getAction();
        Player player = event.getPlayer();

        event.getEvent().setCancelled(true);
        if(checkAction(action, player))
            return;

        ChatManager.info().write(LanguageManager.get("cannotDoAnythingWith"), player);
    });

    public static final SimpleItemTool MULTI_TOOL_PICKAXE = new ArenaMultiTool(
            PluginItems.MULTITOOL_STACK_PICKAXE, event -> {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(checkAction(action, player))
            return;

        if(!EditorCache.contains(player)){
            return;
        }

        RawUnpreparedArena arena = EditorCache.get(player);
        assert arena != null;

        event.getEvent().setCancelled(true);
        if(block == null
                || !block.getType().isSolid()){
            return;
        }

        // Check location
        if(!arena.checkLocation(player, block.getLocation())){
            ChatManager.info().write(LanguageManager.get("locationNotInSameWorld"), player);
            return;
        }

        // Check action
        switch(action){
            case RIGHT_CLICK_BLOCK:
                // Position 1
                Location pos1 = LocationUtil.fix(block.getLocation());

                arena.getRawGameWalls().setType1(pos1);
                ChatManager.info().write(LanguageManager.get("wallPositionOneSetTo").replace("%pos", LocationUtil.toString(pos1)), player);
                break;
            case LEFT_CLICK_BLOCK:
                // Position 2
                Location pos2 = LocationUtil.fix(block.getLocation());

                arena.getRawGameWalls().setType2(pos2);
                ChatManager.info().write(LanguageManager.get("wallPositionTwoSetTo").replace("%pos", LocationUtil.toString(pos2)), player);
                break;
        }
    });

    public static final SimpleItemTool MULTI_TOOL_SHOVEL = new ArenaMultiTool(
            PluginItems.MULTITOOL_STACK_SHOVEL, event -> {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(checkAction(action, player))
            return;

        if(!EditorCache.contains(player)){
            return;
        }

        RawUnpreparedArena arena = EditorCache.get(player);
        assert arena != null;

        event.getEvent().setCancelled(true);
        if(block == null
                || !block.getType().isSolid()){
            return;
        }

        // Check location
        if(!arena.checkLocation(player, block.getLocation())){
            ChatManager.info().write(LanguageManager.get("locationNotInSameWorld"), player);
            return;
        }

        // Check action
        switch(action){
            case RIGHT_CLICK_BLOCK:
                if(player.isSneaking()){
                    Location l = LocationUtil.fix(block.getLocation());

                    if(arena.plotPosContains(l)){
                        ChatManager.info().write(LanguageManager.get("positionWasAlreadyAdded"), player);
                        return;
                    }

                    arena.addGamePlotLocation(l);
                    ChatManager.info().write(LanguageManager.get("addedPosToPlot").replace("%pos", LocationUtil.toString(l)),
                            player);
                }
                else{
                    Location middlePosition = LocationUtil.fix(block.getLocation());

                    if(arena.plotPosContains(middlePosition)){
                        ChatManager.info().write(LanguageManager.get("positionWasAlreadyAdded"), player);
                        return;
                    }

                    arena.setRawGamePlotMarker(middlePosition);
                    ChatManager.info().write(LanguageManager.get("setAsPlotCenter")
                            .replace("%pos", LocationUtil.toString(middlePosition)), player);
                }
                break;
            case LEFT_CLICK_BLOCK:
                Material type = block.getType();

                if(arena.getRawGamePlotMarker() == null){
                    ChatManager.info().write(LanguageManager.get("youMustSetCenterFirst"), player);
                    return;
                }
                Location center = arena.getRawGamePlotMarker();
                Block centerBlock = center.getBlock();

                Set<Block> blocks = GeometryUtil.fill4(centerBlock.getWorld(),
                        centerBlock.getX(), centerBlock.getY(), centerBlock
                                .getZ(), type, true);
                blocks.forEach(block1 -> arena.addGamePlotLocation(block1.getLocation()));
                ChatManager.info().write(LanguageManager.get("addedMorePositionsToPlot")
                                .replace("%val", blocks.size() + "").replace("%type", type.name()),
                        player);

                arena.setRawGamePlotMarker(null);
                break;
        }
    });

    public static final SimpleItemTool MULTI_TOOL_HOE = new ArenaMultiTool(
            PluginItems.MULTITOOL_STACK_HOE, event -> {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(checkAction(action, player))
            return;

        if(!EditorCache.contains(player)){
            return;
        }

        UnpreparedArena arena = EditorCache.getLast(player);
        assert arena != null;

        event.getEvent().setCancelled(true);
        if(block == null
                || !block.getType().isSolid()){
            return;
        }

        switch(action){
            case RIGHT_CLICK_BLOCK:
                Location pos = LocationUtil.fix(block.getLocation());
                String text = LanguageManager.get("spawnPointSetTo").replace("%pos", LocationUtil.toString(pos));

                arena.setSpawn(pos);
                ChatManager.info().write(text, player);
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                String text1 = LanguageManager.get("spawnPointRemoved");

                arena.setSpawn(null);
                ChatManager.info().write(text1, player);
                break;
        }
    });

    /**
     * Checks the action for player
     *
     * @param action The action
     * @param player The player
     *
     * @return The result as boolean
     */
    public static boolean checkAction(Action action, Player player){
        if((action == Action.RIGHT_CLICK_AIR
                || action == Action.RIGHT_CLICK_BLOCK)
                && player.isSneaking()){
            SuperInventory inv = null;
            try{
                inv = new SuperInventory(LanguageManager.get("multitoolHeader"), InventorySize
                        .THREE_ROWS).from(ArenaMultiToolInventory.class);
                player.openInventory(inv.build());
                return true;
            }catch(InventoryCreateException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Method to load every item inside this class
     */
    public static void load(){}

}
