package de.superioz.cr.common.arena;

import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.library.bukkit.common.item.SimpleItem;
import de.superioz.library.bukkit.util.SerializeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class ItemKit {

    protected ItemStack[] armor;
    protected ItemStack[] content;

    public static final String SPLITERATOR = "%";

    public ItemKit(ItemStack[] content, ItemStack[] armor){
        this.content = content;
        this.armor = armor;
    }

    /**
     * Gets size of this kit
     *
     * @return The size
     */
    public int getSize(){
        int counter = 0;

        for(ItemStack item : armor){
            if(item != null && item.getType() != Material.AIR)
                counter++;
        }
        for(ItemStack item : content){
            if(item != null && item.getType() != Material.AIR)
                counter++;
        }

        return counter;
    }

    /**
     * Gets this kit as string
     *
     * @return The string
     */
    @Override
    public String toString(){
        return SerializeUtil.toString(content)
                + SPLITERATOR + SerializeUtil.toString(armor);
    }

    /**
     * Gets an itemkit from a string
     *
     * @param s The string
     *
     * @return The itemkit
     */
    public static ItemKit fromString(String s){
        String[] arr = s.split(SPLITERATOR);

        return new ItemKit(SerializeUtil.itemsFromString(arr[0]), SerializeUtil.itemsFromString(arr[1]));
    }

    /**
     * Get contents of this itemkit but unbreakable
     *
     * @param cont The content
     *
     * @return The content unbreakable
     */
    public static ItemStack[] getContents(ItemStack[] cont){
        ItemStack[] arr = new ItemStack[cont.length];

        for(int i = 0; i < cont.length; i++){
            ItemStack item = cont[i];

            if(item == null || item.getType() == Material.AIR)
                continue;

            arr[i] = new SimpleItem(item).setUnbreakable(true).setFlags(true, ItemFlag.HIDE_UNBREAKABLE)
                    .setLore(LanguageManager.get("itemIsUnbreakable")).getWrappedStack();
        }
        return arr;
    }

    /**
     * Set this kit for given player
     *
     * @param player The player
     */
    public void setFor(Player player){
        PlayerInventory inv = player.getInventory();
        inv.setContents(getContents(content));

        this.setArmor(player);
    }

    /**
     * Set items to this player if his inv doesnt contains the items
     *
     * @param player The player
     */
    public void setSoftFor(Player player){
        PlayerInventory inv = player.getInventory();

        for(ItemStack item : getContents(content)){
            if(item == null || item.getType() == Material.AIR){
                continue;
            }

            if(!inv.contains(item.getType()))
                inv.addItem(item);
        }

        this.setArmor(player);
    }

    /**
     * Set the kit armor for given player
     *
     * @param player The player
     */
    public void setArmor(Player player){
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = getContents(this.armor);

        if(!inv.contains(armor[3]))
            inv.setHelmet(armor[3]);
        if(!inv.contains(armor[2]))
            inv.setChestplate(armor[2]);
        if(!inv.contains(armor[1]))
            inv.setLeggings(armor[1]);
        if(!inv.contains(armor[0]))
            inv.setBoots(armor[0]);
    }

    /**
     * Remove the armor for given player
     *
     * @param player The player
     */
    public void removeArmor(Player player){
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = getContents(this.armor);

        for(ItemStack item : armor){
            if(inv.contains(item))
                inv.remove(item);
        }
    }

}
