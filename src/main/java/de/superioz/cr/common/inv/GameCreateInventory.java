package de.superioz.cr.common.inv;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.event.GameCreateEvent;
import de.superioz.cr.common.game.GameType;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.bukkit.BukkitLibrary;
import de.superioz.library.bukkit.common.inventory.InventorySize;
import de.superioz.library.bukkit.common.inventory.PageableInventory;
import de.superioz.library.bukkit.common.inventory.SuperInventory;
import de.superioz.library.bukkit.common.item.InteractableSimpleItem;
import de.superioz.library.bukkit.common.item.SimpleItem;
import de.superioz.library.bukkit.event.WrappedInventoryClickEvent;
import de.superioz.library.bukkit.util.LocationUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameCreateInventory {

    private static SuperInventory gameTypeChoose;
    private static SuperInventory gameArenaChoose;
    private static HashMap<Player, GameType> typeHashMap = new HashMap<>();

    /**
     * Opens this inventory to given player
     * @param player The player
     */
    public static void open(Player player){
        initGameTypeChooseInv();
        initGameArenaChooseInv();

        player.openInventory(gameTypeChoose.build());
    }

    /**
     * Inits the game type choose inventory
     */
    private static void initGameTypeChooseInv(){
        if(gameTypeChoose != null)
            return;
        SuperInventory superInventory = new SuperInventory("Choose type", InventorySize.THREE_ROWS);

        de.superioz.library.java.util.Consumer<WrappedInventoryClickEvent> consumer = event -> {
            ItemStack item = event.getItem();

            if(!item.hasItemMeta())
                return;
            String name = item.getItemMeta().getDisplayName();
            GameType type = GameType.from(name);

            // Get type
            if(type != null){
                typeHashMap.put(event.getPlayer(), type);

                event.cancelEvent();
                event.closeInventory();
                event.getPlayer().openInventory(gameArenaChoose.build());
            }
        };

        superInventory.set(new InteractableSimpleItem(12, PluginItems.GAMETYPE_PRIVATE, superInventory, consumer));
        superInventory.set(new InteractableSimpleItem(16, PluginItems.GAMETYPE_PUBLIC, superInventory, consumer));
        gameTypeChoose = superInventory;
    }

    /**
     * Inits the arena choose inventory
     */
    private static void initGameArenaChooseInv(){
        List<SimpleItem> arenaItems = new ArrayList<>();
        for(Arena ar : ArenaManager.getCache().arenaList){
            SimpleItem item = new SimpleItem(Material.STAINED_GLASS);
            item.setName("&d" + ar.getName());
            item.addLore("&7Spawn: &9@" + LocationUtil.toString(ar.getSpawnPoint()),
                    "&7Plots: &e" + ar.getGamePlots().size(),
                    "&7Walls: &e" + ar.getGameWalls().size(),
                    "&7Itemkit: &e" + ar.getItemKit().getSize() + " &7item(s)");
            arenaItems.add(item);
        }

        PageableInventory inventory = new PageableInventory("Choose an arena", InventorySize.FIVE_ROWS,
                arenaItems, PluginItems.MIDDLE_PAGE_CHOOSE_ARENA,
                PluginItems.NEXT_PAGE, PluginItems.LAST_PAGE);
        inventory.calculatePages(false, event -> {
            if(event.getItem() != null){
                ItemStack item1 = event.getItem();

                // Check item meta
                if(!item1.hasItemMeta()){
                    return;
                }

                // Get Arena
                String name1 = ChatColor.stripColor(item1.getItemMeta().getDisplayName());
                Arena arena = ArenaManager.get(name1);

                // Cancel event
                event.cancelEvent();
                event.closeInventory();

                // Check if game already runs
                if(GameManager.containsGameInQueue(arena)){
                    ChatManager.info().write(LanguageManager.get("thisGameAlreadyExists"), event.getPlayer());
                    return;
                }

                // Check arena
                if(arena == null){
                    ChatManager.info().write(LanguageManager.get("errorWhileCreatingTheGame"), event.getPlayer());
                    return;
                }

                // Gametype
                GameType type = typeHashMap.get(event.getPlayer());
                typeHashMap.remove(event.getPlayer());

                // CREATE GAME for ARENA
                BukkitLibrary.callEvent(new GameCreateEvent(arena, type, event.getPlayer()));
            }
        });
        gameArenaChoose = inventory.getPage(1);
    }

}
