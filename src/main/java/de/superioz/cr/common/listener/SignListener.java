package de.superioz.cr.common.listener;

import de.superioz.cr.common.arena.Arena;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.PlayableArena;
import de.superioz.cr.main.CastleRush;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class SignListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onSign(SignChangeEvent event){
        Player player = event.getPlayer();

        if(!(event.getLine(0).contains("[CR]")
                && player.hasPermission("castlerush.createsign"))){
            return;
        }

        String l1 = event.getLine(1);
        if(l1 == null || l1.isEmpty()){
            return;
        }

        Arena arena = ArenaManager.get(l1);
        if(arena == null){
            event.getBlock().breakNaturally();
            CastleRush.getChatMessager().send("&cThat arena doesn't exist!", player);
            return;
        }

        int maxPlayers = arena.getGamePlots().size();
        int minPlayers = 0;
        String name = arena.getName();
        String header = ChatColor.AQUA + "CastleRush";

        event.setLine(0, header);
        event.setLine(1, name.substring(0, 16));
        event.setLine(2, minPlayers + "/" + maxPlayers);
        event.setLine(3, GameManager.State.LOBBY.getSpecifier());
        ((Sign) event.getBlock()).update(true);
    }

    @EventHandler
    public void onSignRightclick(PlayerInteractEvent event){

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();

        if(block == null || block.getType() == Material.AIR)
            return;

        if(block instanceof Sign){
            Sign sign = (Sign) block;
            Player player = event.getPlayer();
            Action action = event.getAction();

            if(!sign.getLine(0).equalsIgnoreCase(ChatColor.AQUA + "CastleRush")){
                return;
            }

            String arenaName = sign.getLine(1);
            Arena arena = ArenaManager.get(arenaName);

            if(GameManager.isIngame(player))
                return;

            if(!GameManager.containsGameInQueue(arena)){
                GameManager.addGameInQueue(new GameManager
                        .Game(new PlayableArena(arena, GameManager.State.LOBBY)));
            }
            GameManager.Game game = GameManager.getGame(arena);

            // Call event for further things
            CastleRush.getPluginManager().callEvent(new GameJoinEvent(game, player));
        }
    }

}
