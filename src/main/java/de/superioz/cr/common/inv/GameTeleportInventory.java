package de.superioz.cr.common.inv;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GamePlot;
import de.superioz.cr.common.game.team.Team;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.java.util.SimpleStringUtils;
import de.superioz.library.minecraft.server.common.inventory.InventorySize;
import de.superioz.library.minecraft.server.common.inventory.PageableInventory;
import de.superioz.library.minecraft.server.common.inventory.SuperInventory;
import de.superioz.library.minecraft.server.common.item.InteractableSimpleItem;
import de.superioz.library.minecraft.server.common.item.SimpleItem;
import de.superioz.library.minecraft.server.event.WrappedInventoryClickEvent;
import de.superioz.library.minecraft.server.util.ItemUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameTeleportInventory {

    private static SuperInventory overviewInventory;
    private static SuperInventory checkpointsInventory;
    private static SuperInventory plotsInventory;

    /**
     * Opens the inventory for given player
     *
     * @param gamePlayer The player
     */
    public static void open(WrappedGamePlayer gamePlayer){
        initPlotsInventory(gamePlayer.getGame());

        openOverview(gamePlayer);
    }

    /**
     * Inits the plots inventory
     *
     * @param game The game
     */
    private static void initPlotsInventory(Game game){
        if(plotsInventory != null)
            return;
        SuperInventory superInventory = new SuperInventory("Choose a plot", InventorySize.ONE_ROW);

        List<SimpleItem> items = new ArrayList<>();
        int counter = 1;
        for(GamePlot plot : game.getArena().getArena().getGamePlots()){
            Team team = game.getTeamManager().getTeam(game.getArena().getArena().getIndex(plot));
            SimpleItem item = new SimpleItem(Material.BRICK)
                    .setName(CastleRush.ARROW_SPACER + " &dPlot #" + counter)
                    .addLore(LanguageManager.get("clickToTeleportToPlot").replace("%team", team.getColoredName(game)));
            items.add(item);
            counter++;
        }

        // get consumer
        Consumer<WrappedInventoryClickEvent> eventConsumer = event -> {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if(!GameManager.isIngame(player.getUniqueId())){
                return;
            }
            WrappedGamePlayer gamePlayer = GameManager.getWrappedGamePlayer(player);

            if(!item.hasItemMeta()){
                return;
            }

            String index = item.getItemMeta().getDisplayName().split("#")[1];
            if(!SimpleStringUtils.isInteger(index)){
                return;
            }
            int indexInteger = Integer.parseInt(index);

            GamePlot plot = game.getArena().getArena().getGamePlots().get(indexInteger - 1);
            gamePlayer.teleport(plot.getTeleportPoint());
        };

        for(int i = 0; i < items.size(); i++){
            superInventory.set(new InteractableSimpleItem(i + 1, items.get(i), superInventory, eventConsumer));
        }
        plotsInventory = superInventory;
    }

    /**
     * Inits the overview inventory
     */
    private static void openOverview(WrappedGamePlayer gamePlayer){
        if(overviewInventory != null){
            gamePlayer.getPlayer().openInventory(overviewInventory.build());
            return;
        }
        SuperInventory superInventory = new SuperInventory("Choose target", InventorySize.THREE_ROWS);

        Consumer<WrappedInventoryClickEvent> consumer = event -> {
            ItemStack item = event.getItem();
            WrappedGamePlayer wrappedGamePlayer = GameManager.getWrappedGamePlayer(event.getPlayer());
            event.cancelEvent();
            event.closeInventory();

            if(ItemUtil.compare(item, PluginItems.TELEPORT_TOOL_PLOTS.getWrappedStack())){
                wrappedGamePlayer.getPlayer().openInventory(plotsInventory.build());
            }
            else if(ItemUtil.compare(item, PluginItems.TELEPORT_TOOL_CHECKPOINTS.getWrappedStack())){
                ChatManager.info().write("&cComing soon! :)", wrappedGamePlayer.getPlayer());
            }
            else if(ItemUtil.compare(item, PluginItems.TELEPORT_TOOL_MATE.getWrappedStack())){
                openTeamMatesInventory(wrappedGamePlayer);
            }
        };

        // Get items
        SimpleItem plots = PluginItems.TELEPORT_TOOL_PLOTS;
        SimpleItem mates = PluginItems.TELEPORT_TOOL_MATE;
        SimpleItem checkpoints = PluginItems.TELEPORT_TOOL_CHECKPOINTS;

        superInventory.set(new InteractableSimpleItem(12, plots, superInventory, consumer));
        superInventory.set(new InteractableSimpleItem(14, mates, superInventory, consumer));
        superInventory.set(new InteractableSimpleItem(16, checkpoints, superInventory, consumer));
        overviewInventory = superInventory;
        openOverview(gamePlayer);
    }

    /**
     * Open inventory to choose a teammate to teleport for given player
     *
     * @param player The player
     */
    private static void openTeamMatesInventory(WrappedGamePlayer player){
        List<SimpleItem> mateItems = new ArrayList<>();
        List<WrappedGamePlayer> teamMates = player.getTeamMates().stream().collect(Collectors.toList());
        teamMates.remove(player);

        // Check size
        if(teamMates.size() == 0){
            ChatManager.info().write(LanguageManager.get("youHaventTeammates"), player.getPlayer());
            return;
        }

        // Init teams
        for(WrappedGamePlayer gp : teamMates){
            // Set skull owner
            ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(gp.getPlayer().getDisplayName());
            SimpleItem item = new SimpleItem(stack);

            // Name
            String name = CastleRush.ARROW_SPACER + " " + gp.getTeam().getColor(gp.getGame()) + gp.getDisplayName();
            item.setName(name);

            // Lore
            item.addLore(LanguageManager.get("clickToTeleportTo").replace("%player", gp.getDisplayName()));

            // Add to list
            mateItems.add(item);
        }

        // Get inventory
        PageableInventory inventory = new PageableInventory("Choose mate", InventorySize.FOUR_ROWS, mateItems,
                PluginItems.MIDDLE_PAGE_GAME_MATES, PluginItems.NEXT_PAGE, PluginItems.LAST_PAGE);
        inventory.calculatePages(false, event -> {
            event.cancelEvent();
            event.closeInventory();

            ItemStack item = event.getItem();
            WrappedGamePlayer pl = GameManager.getWrappedGamePlayer(event.getPlayer());

            if(item == null
                    || !item.hasItemMeta())
                return;

            // Get game player
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[1];
            WrappedGamePlayer gamePlayer = pl.getTeam().getMate(name);

            // Check on which plot
            if(pl.getPlotStandingOn() == null
                    || gamePlayer.getPlotStandingOn() == null
                    || !pl.getPlotStandingOn().equals(gamePlayer.getPlotStandingOn())){
                ChatManager.info().write(LanguageManager.get("arentOnSamePlot")
                        .replace("%pl", gamePlayer.getDisplayName()), pl.getPlayer());
                return;
            }

            // Teleport
            pl.teleport(gamePlayer.getPlayer().getLocation());
        });

        // Open inventory
        inventory.open(player.getPlayer());
    }

}
