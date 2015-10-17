package de.superioz.cr.common;


import de.superioz.library.minecraft.server.util.serialize.ItemStackSerializer;
import org.bukkit.inventory.ItemStack;

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
                + "&" + new ItemStackSerializer(armor).serialize();
    }

    public static ItemKit fromString(String s){
        String[] arr = s.split("&");

        return new ItemKit(new ItemStackSerializer(null).deserialize(arr[0]), new ItemStackSerializer(null)
                .deserialize(arr[1]));
    }

}
