package de.superioz.cr.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class WorldBackup implements Listener {

    private List<String> changedBlocks;
    private World world;
    private boolean flag;

    public WorldBackup(World world){
        this.world = world;
        this.changedBlocks = new ArrayList<>();
    }

    public void restoreBlocks(){
        changedBlocks.forEach(this::restoreBlock);
        changedBlocks.clear();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        BlockState bs = event.getBlockReplacedState();

        if(bs.getWorld().getName().equals(world.getName())
                && flag && !event.isCancelled()){
            System.out.println("Block placed and saved");
            changedBlocks.add(serializeBlock(bs));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().getWorld().getName().equals(world.getName())
                && flag && !event.isCancelled())
            changedBlocks.add(serializeBlock(event.getBlock()));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event){
        if(event.getLocation().getWorld().getName().equals(world.getName())
                && flag && !event.isCancelled())
            changedBlocks.addAll(event.blockList().stream()
                    .map(this::serializeBlock).collect(Collectors.toList()));
    }

    public String serializeBlock(Block b){
        return b.getTypeId() + ":" + b.getData() + ":" + b.getWorld().getName()
                + ":" + b.getX() + ":" + +b.getY() + ":" + b.getZ();
    }

    public String serializeBlock(BlockState b){
        return b.getTypeId() + ":" + b.getData() + ":" + b.getWorld().getName()
                + ":" + b.getX() + ":" + +b.getY() + ":" + b.getZ();
    }

    public void restoreBlock(String s){
        String[] blockdata = s.split(":");

        if(blockdata[0].contains("AIR"))
            blockdata[0] = "0";

        int typeId = Integer.parseInt(blockdata[0]);
        byte data = Byte.parseByte(blockdata[1]);
        World world = Bukkit.getWorld(blockdata[2]);
        int x = Integer.parseInt(blockdata[3]);
        int y = Integer.parseInt(blockdata[4]);
        int z = Integer.parseInt(blockdata[5]);

        world.getBlockAt(x, y, z).setTypeId(typeId);
        world.getBlockAt(x, y, z).setData(data);
    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

}
