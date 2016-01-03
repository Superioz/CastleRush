package de.superioz.cr.common.game;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.PlayableArena;
import de.superioz.cr.common.game.team.Team;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.cr.common.timer.PlayerLeftTimer;
import de.superioz.cr.util.PluginColor;
import de.superioz.cr.util.PluginItems;
import de.superioz.library.java.util.SimpleStringUtils;
import de.superioz.library.java.util.classes.SimpleGroup;
import de.superioz.library.minecraft.server.common.inventory.InventorySize;
import de.superioz.library.minecraft.server.common.inventory.PageableInventory;
import de.superioz.library.minecraft.server.common.inventory.SuperInventory;
import de.superioz.library.minecraft.server.common.item.SimpleItem;
import de.superioz.library.minecraft.server.event.WrappedInventoryClickEvent;
import de.superioz.library.minecraft.server.util.BukkitUtil;
import de.superioz.library.minecraft.server.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class GameManager {

    private static List<Game> runningGames = new ArrayList<>();
    private static HashMap<UUID, SimpleGroup<WrappedGamePlayer, Long, PlayerLeftTimer>> leftPlayer
            = new HashMap<>();
    private static List<WrappedGamePlayer> leftForSurePlayer = new ArrayList<>();

    /**
     * Add given game to queue
     * @param game The game
     */
    public static void addGameInQueue(Game game){
        if(!runningGames.contains(game)){
            runningGames.add(game);
            game.registerBackup();
        }
    }

    /**
     * Remove given qame from queue
     * @param game The game
     * @param flag Flag if the backup should be unregistered
     */
    public static void removeGameFromQueue(Game game, boolean flag){
        if(runningGames.contains(game)){
            runningGames.remove(game);
            game.getGameCountdown().cancel();

            if(flag)
                game.unregisterBackup();
        }
    }

    /**
     * Check if given location is part of a plot
     * @param loc The location
     * @return The result
     */
    public static boolean isPlotPart(Location loc){
        for(Game g : getRunningGames()){
            for(GamePlot p : g.getArena().getArena().getGamePlots()){
                if(p.isPart(LocationUtil.fix(loc.getBlock().getLocation()))){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Fixes given location
     * @param fixable The location
     * @param world The name of the world
     * @return The location
     */
    public static Location getLocation(Location fixable, String world){
        fixable.setWorld(Bukkit.getWorld(world));
        return fixable;
    }

    /**
     * Checks if given arena is in queue
     * @param arena The arena
     * @return The result
     */
    public static boolean containsGameInQueue(Arena arena){
        return getGame(arena) != null;
    }

    /**
     * Get game of arena
     * @param arena The arena
     * @return The game
     */
    public static Game getGame(Arena arena){
        for(Game g : runningGames){
            if(g.getArena().getArena().getName().equalsIgnoreCase(arena.getName()))
                return g;
        }
        return null;
    }

    /**
     * Stop all arenas
     */
    public static void stopArenas(){
        runningGames.forEach(Game::stop);
    }

    /**
     * Get game of given player
     * @param player The player
     * @return The game
     */
    public static Game getGame(Player player){
        if(isIngame(player.getUniqueId())){
            for(Game g : runningGames){
                if(isIngame(player.getUniqueId(), g))
                    return g;
            }
        }
        return null;
    }

    /**
     * Checks if given uuid is ingame
     * @param uuid The uuid
     * @return The result
     */
    public static boolean isIngame(UUID uuid){
        for(Game g : runningGames){
            if(isIngame(uuid, g))
                return true;
        }
        return false;
    }

    /**
     * Checks if given uuid is ingame in given game
     * @param uuid The uuid
     * @param game The game
     * @return The result
     */
    public static boolean isIngame(UUID uuid, Game game){
        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            if(pl.getUuid().equals(uuid)){
                return true;
            }
        }
        return false;
    }

    /**
     * Get wrapped game player from player
     * @param player The player
     * @return The wrapped game player
     */
    public static WrappedGamePlayer getWrappedGamePlayer(Player player){
        WrappedGamePlayer gp = null;

        if(!isIngame(player.getUniqueId()))
            return gp;

        Game game = getGame(player);
        assert game != null;

        for(WrappedGamePlayer pl : game.getArena().getPlayers()){
            if(pl.getUuid().equals(player.getUniqueId())){
                gp = pl;
            }
        }
        return gp;
    }

    /**
     * Checks if given block has plot
     * @param block The block
     * @return The result
     */
    public static boolean hasPlot(Block block){
        for(Game g : runningGames){
            for(GamePlot pl : g.getArena().getArena().getGamePlots()){
                if(pl.isPart(block.getLocation()))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if given block has plot in given game
     * @param block The block
     * @param game The game
     * @return The result
     */
    public static boolean hasPlot(Block block, Game game){
        for(GamePlot pl : game.getArena().getArena().getGamePlots()){
            if(pl.isPart(block.getLocation()))
                return true;
        }
        return false;
    }

    /**
     * Gets the game of given block
     * @param block The block
     * @return The game
     */
    public static Game getGame(Block block){
        for(Game g : runningGames){
            for(GamePlot pl : g.getArena().getArena().getGamePlots()){
                if(pl.isPart(block.getLocation()))
                    return g;
            }
        }
        return null;
    }

    /**
     * Add given player to left-the-game-players
     * @param player The player
     */
    public static void addLeft(WrappedGamePlayer player){
        Player rootPlayer = player.getPlayer();

        if(getLeftPlayer().containsKey(rootPlayer.getUniqueId()))
            return;
        long timeStamp = System.currentTimeMillis();

        if(player.getGame().getArena().getGameState() != GameState.INGAME){
            timeStamp = -1;
        }

        PlayerLeftTimer leftTimer = new PlayerLeftTimer();
        leftPlayer.put(rootPlayer.getUniqueId(), new SimpleGroup<>(player, timeStamp, leftTimer));
        leftTimer.run(player);
    }

    /**
     * Remove given player from left-the-game-players
     * @param player The player
     */
    public static void removeLeft(WrappedGamePlayer player){
        if(!getLeftPlayer().containsKey(player.getUuid())){
            return;
        }

        PlayerLeftTimer countdown = getLeftPlayer().get(player.getUuid()).getObject3();

        if(countdown.getDelayer().getRunnable() != null){
            countdown.getDelayer().getRunnable().cancel();
        }

        getLeftPlayer().remove(player.getUuid());
    }

    /**
     * Gets the left-the-game-players from given player
     * @param player The player
     * @return The wrapped game player
     */
    public static WrappedGamePlayer getLeft(Player player){
        if(getLeftPlayer().containsKey(player.getUniqueId())){
            return getLeftPlayer().get(player.getUniqueId()).getObject1();
        }
        return null;
    }

    /**
     * Gets the left-for-sure-the-game-players from given player
     * @param gamePlayer The player
     * @return The wrapped game player
     */
    public static WrappedGamePlayer getForSure(Player gamePlayer){
        for(WrappedGamePlayer gp : getLeftForSurePlayer()){
            if(gp.getUuid().equals(gamePlayer.getUniqueId()))
                return gp;
        }
        return null;
    }

    /**
     * Checks if player is inside left-for-sure list
     * @param gp The player
     * @return The result
     */
    public static boolean containsForSure(Player gp){
        return getForSure(gp) != null;
    }

    /**
     * Creates a game with given values
     * @param gameMaster The game master
     * @param arena The arena
     * @param type The type
     * @return The result
     */
    public static boolean createGame(Player gameMaster, Arena arena, GameType type){
        String reason = arena.checkJoinable(gameMaster);
        String reason1 = checkInventory(gameMaster);

        if(!reason.isEmpty()
                || !reason1.isEmpty()){
            boolean flag = reason.isEmpty();

            ChatManager.info().write(LanguageManager.get("youCannotCreateGame")
                    .replace("%reason", flag ? reason1 : reason), gameMaster.getPlayer());
            return false;
        }

        // Check running games
        if(!containsGameInQueue(arena)){
            Game game = new Game(new PlayableArena(arena, GameState.LOBBY, GamePhase.LOBBY), type);
            GameManager.addGameInQueue(game);
        }
        return true;
    }

    /**
     * Check the inventory of given player
     * @param player The player
     * @return The result
     */
    public static String checkInventory(Player player){
        if(BukkitUtil.hasContent(player.getInventory())
                && !PluginSettings.CLEAR_INV){
            return "inventory not empty";
        }
        return "";
    }

    /**
     * Gets the game overview
     * @param title The title
     * @param consumer The consumer
     * @return The inventory
     */
    public static SuperInventory getGameOverview(String title, Consumer<WrappedInventoryClickEvent> consumer){
        List<Game> games = GameManager.getRunningGames();
        List<SimpleItem> items = new ArrayList<>();

        for(int i = 0; i < games.size(); i++){
            Game game = games.get(i);
            GameState state = game.getArena().getGameState();

            SimpleItem item = new SimpleItem(Material.STAINED_CLAY)
                    .setName(PluginColor.VIOLET + "Game #"+(i+1))
                    .setLore(PluginColor.LIGHT + "Name: " + PluginColor.DARK_AQUA + game.getArena().getArena().getName(),
                            PluginColor.LIGHT + "State: " + state.getSpecifier(),
                            PluginColor.LIGHT + "Type: " + SimpleStringUtils.upperFirstLetter(game.getType().getSpecifier()),
                            PluginColor.LIGHT + "Gamemaster: " + PluginColor.ICE + game.getGameMaster().getDisplayName(),
                            PluginColor.LIGHT + "Teams:");

            switch(state){
                case LOBBY:
                    item.setColor(DyeColor.GREEN);
                    break;
                case INGAME:
                    item.setColor(DyeColor.RED);
                    break;
                case WAITING:
                    item.setColor(DyeColor.YELLOW);
                    break;
            }

            // Pattern of overview items
            for(Team team : game.getTeamManager().getTeams()){
                item.addLore(PluginColor.DARK +"- " + team.getColoredName(game) + "&7: &e" + team.getTeamPlayer().size());
            }

            items.add(item);
        }

        // Get inventory
        PageableInventory inventory = new PageableInventory(title, InventorySize.FIVE_ROWS, items,
                PluginItems.MIDDLE_PAGE_GAME_VIEW, PluginItems.NEXT_PAGE, PluginItems.LAST_PAGE);
        inventory.calculatePages(false, consumer);

        return inventory.getPage(1);
    }

    // --- Intern methods

    public static List<Game> getRunningGames(){
        return runningGames;
    }

    public static HashMap<UUID, SimpleGroup<WrappedGamePlayer, Long, PlayerLeftTimer>> getLeftPlayer(){
        return leftPlayer;
    }

    public static List<WrappedGamePlayer> getLeftForSurePlayer(){
        return leftForSurePlayer;
    }

}
