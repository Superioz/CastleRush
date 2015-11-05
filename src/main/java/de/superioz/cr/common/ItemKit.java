package de.superioz.cr.common;


import de.superioz.library.minecraft.server.util.serialize.ItemStackSerializer;
import org.bukkit.entity.Player;
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

    public void setFor(Player player){
        PlayerInventory inv = player.getInventory();

        inv.setContents(content);
        inv.setHelmet(armor[3]);
        inv.setChestplate(armor[2]);
        inv.setLeggings(armor[1]);
        inv.setBoots(armor[0]);
    }

    public void resetArmor(Player player){
        PlayerInventory inv = player.getInventory();

        inv.setHelmet(armor[3]);
        inv.setChestplate(armor[2]);
        inv.setLeggings(armor[1]);
        inv.setBoots(armor[0]);
    }

}
