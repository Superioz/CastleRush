package de.superioz.cr.util;

import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.division.GameState;
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

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class WorldBackup implements Listener {

    private List<String> changedBlocks;
    private World world;
    private boolean flag = false;
    private Game game;

    public WorldBackup(Game game){
        this.game = game;
        this.world = game.getArena().getArena().getSpawnPoints().get(0).getWorld();
        this.changedBlocks = new ArrayList<>();
    }

    public void restoreBlocks(){
        int counter = 0;

        for(String s : changedBlocks){
            this.restoreBlock(s);
            counter++;
        }

        changedBlocks.clear();
        System.out.println(counter + " blocks restored!");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        BlockState bs = event.getBlockReplacedState();

        if(bs.getWorld().getName().equals(world.getName())
                && flag && !event.isCancelled()
                && game.getArena().getGameState() == GameState.INGAME){
            System.out.println("Block placed and saved! " + changedBlocks.size());
            changedBlocks.add(serializeBlock(bs));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().getWorld().getName().equals(world.getName())
                && flag && !event.isCancelled() && game.getArena().getGameState() == GameState.INGAME)
            System.out.println("Block broken and saved! " + changedBlocks.size());
            changedBlocks.add(serializeBlock(event.getBlock()));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event){
        if(event.getLocation().getWorld().getName().equals(world.getName())
                && flag && !event.isCancelled() && game.getArena().getGameState() == GameState.INGAME){
            List<Block> blocks = event.blockList();

            for(int i = 0; i < blocks.size(); i++){
                Block b = blocks.get(i);

                if(!GameManager.hasPlot(b, game)){
                    blocks.remove(b);
                    continue;
                }
                changedBlocks.add(serializeBlock(b));
                System.out.println("Block exploded and saved!");
            }
        }
    }

    public String serializeBlock(Block b){
        return b.getTypeId() + ":" + b.getData() + ":" + b.getWorld().getName()
                + ":" + b.getX() + ":" + +b.getY() + ":" + b.getZ();
    }

    public String serializeBlock(BlockState b){
        return b.getTypeId() + ":" + b.getBlock().getData() + ":" + b.getWorld().getName()
                + ":" + b.getX() + ":" + +b.getY() + ":" + b.getZ();
    }

    public void restoreBlock(String s){
        String[] blockdata = s.split(":");

        if(blockdata[0].contains("(")){
            String[] blockdata0 = blockdata[0].split("\\(");
            blockdata[0] = blockdata0[1].replace(")", "");
        }

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
