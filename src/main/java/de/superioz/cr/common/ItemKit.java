package de.superioz.cr.common;


import de.superioz.cr.main.CastleRush;
import de.superioz.library.minecraft.server.items.ItemBuilder;
import de.superioz.library.minecraft.server.util.serialize.ItemStackSerializer;
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
        return new ItemStackSerializer(content).serialize()
                + "%" + new ItemStackSerializer(armor).serialize();
    }

    public static ItemKit fromString(String s){
        String[] arr = s.split("%");

        return new ItemKit(new ItemStackSerializer(null).deserialize(arr[0]), new ItemStackSerializer(null)
                .deserialize(arr[1]));
    }

    public static ItemStack[] getContents(ItemStack[] cont){
        ItemStack[] arr = new ItemStack[cont.length];

        for(int i = 0; i < cont.length; i++){
            ItemStack item = cont[i];

            if(item == null || item.getType() == Material.AIR)
                continue;

            arr[i] = new ItemBuilder(item).unbreakable(true).itemFlag(ItemFlag.HIDE_UNBREAKABLE, true)
                    .lore(CastleRush.getProperties().get("itemIsUnbreakable")).build();
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
