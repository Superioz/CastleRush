package de.superioz.cr.util;

import de.superioz.library.java.util.classes.SimplePair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class InventoryBackup {

    private static Map<Player, List<SimplePair<ItemStack, Integer>>> contentMap = new HashMap<>();

    /**
     * Saves the inventory of given player into a map
     *
     * @param player The player
     */
    public static void save(Player player){
        PlayerInventory inventory = player.getInventory();

        List<SimplePair<ItemStack, Integer>> contents = new ArrayList<>();
        for(int i = 0; i < inventory.getSize(); i++){
            ItemStack item = inventory.getItem(i);

            if(item == null || item.getType() == Material.AIR){
                continue;
            }

            contents.add(new SimplePair<>(item, i));
        }

        contentMap.put(player, contents);
    }

    /**
     * Gives the player his stored inventory back
     *
     * @param player The player
     */
    public static void restore(Player player){
        if(!contentMap.containsKey(player))
            return;

        List<SimplePair<ItemStack, Integer>> contents = contentMap.get(player);

        for(SimplePair<ItemStack, Integer> pair : contents){
            player.getInventory().setItem(pair.getType2(), pair.getType1());
        }
        contentMap.remove(player);
    }

}
