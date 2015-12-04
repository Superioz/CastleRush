package de.superioz.cr.common;


import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.common.item.SimpleItem;
import de.superioz.library.minecraft.server.util.SerializeUtil;
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

    public ItemKit(ItemStack[] content, ItemStack[] armor){
        this.content = content;
        this.armor = armor;
    }

    @Override
    public String toString(){
        return SerializeUtil.toString(content)
                + "%" + SerializeUtil.toString(armor);
    }

    public static ItemKit fromString(String s){
        String[] arr = s.split("%");

        return new ItemKit(SerializeUtil.itemsFromString(arr[0]), SerializeUtil.itemsFromString(arr[1]));
    }

    public static ItemStack[] getContents(ItemStack[] cont){
        ItemStack[] arr = new ItemStack[cont.length];

        for(int i = 0; i < cont.length; i++){
            ItemStack item = cont[i];

            if(item == null || item.getType() == Material.AIR)
                continue;

            arr[i] = new SimpleItem(item).setUnbreakable(true).setFlags(true, ItemFlag.HIDE_UNBREAKABLE)
                    .setLore(CastleRush.getProperties().get("itemIsUnbreakable")).getWrappedStack();
        }
        return arr;
    }

    public void setFor(Player player){
        PlayerInventory inv = player.getInventory();
        inv.setContents(getContents(content));

        this.setArmor(player);
    }

    public void setArmor(Player player){
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = getContents(this.armor);

        inv.setHelmet(armor[3]);
        inv.setChestplate(armor[2]);
        inv.setLeggings(armor[1]);
        inv.setBoots(armor[0]);
    }

}
