package de.superioz.cr.common.listener;

import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.arena.object.Arena;
import de.superioz.cr.common.arena.object.PlayableArena;
import de.superioz.cr.common.events.GameJoinEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.division.GamePhase;
import de.superioz.cr.common.game.division.GameState;
import de.superioz.cr.main.CastleRush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("arenaDoesntExist"), player);
            return;
        }

        if(!arena.checkJoinable(player).isEmpty()){
            event.getBlock().breakNaturally();
            CastleRush.getChatMessager().write(CastleRush.getProperties().get("cannotCreateSignReason")
                    .replace("%reason", arena.checkJoinable(player)), player);
            return;
        }

        String name = arena.getName();
        String header = ChatColor.AQUA + "CastleRush";

        if(name.length() > 16)
            name = name.substring(0, 16);

        event.setLine(0, header);
        event.setLine(1, name);
        event.setLine(2, arena.getPattern(0));
        event.setLine(3, GameState.LOBBY.getSpecifier());
        event.getBlock().getState().update(true);

        CastleRush.getChatMessager().write(CastleRush.getProperties().get("arenaSignMessage")
                .replace("%arena", name), player);
    }

    @EventHandler
    public void onSignRightclick(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        BlockState state = block.getState();

        if(state == null)
            return;

        if(state instanceof Sign){
            Sign sign = (Sign) state;
            Player player = event.getPlayer();

            if(!sign.getLine(0).equalsIgnoreCase(ChatColor.AQUA + "CastleRush")){
                return;
            }

            if(GameManager.isIngame(player)){
                return;
            }

            String arenaName = sign.getLine(1);
            Arena arena = ArenaManager.get(arenaName);

            if(arena == null)
                return;

            if(!GameManager.containsGameInQueue(arena)){
                GameManager.addGameInQueue(new Game(new PlayableArena(arena,
                        GameState.LOBBY, GamePhase.WAIT, sign)));
            }
            Game game = GameManager.getGame(arena);
            assert game != null;

            if(game.getArena().getGameState() != GameState.LOBBY){
                CastleRush.getChatMessager().write(CastleRush.getProperties().get("youCannotJoinThisArena"), player);
                return;
            }

            // Call event for further things
            CastleRush.getPluginManager().callEvent(new GameJoinEvent(game, player, player.getLocation()));
        }
    }

}
