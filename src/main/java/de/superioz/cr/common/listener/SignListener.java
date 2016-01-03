package de.superioz.cr.common.listener;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.arena.ArenaManager;
import de.superioz.cr.common.event.GameJoinEvent;
import de.superioz.cr.common.event.GameSignInteractEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.GameSign;
import de.superioz.cr.common.inv.GameCreateInventory;
import de.superioz.library.java.util.SimpleStringUtils;
import de.superioz.library.main.SuperLibrary;
import de.superioz.library.minecraft.server.common.inventory.SuperInventory;
import de.superioz.library.minecraft.server.util.ChatUtil;
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
import org.bukkit.inventory.ItemStack;

/**
 * This class was created as a part of CastleRush (Spigot)
 *
 * @author Superioz
 */
public class SignListener implements Listener {

    private static final String SIGN_HEADER = ChatColor.BLUE + "CastleRush";
    private static final String SIGN_CREATE = ChatColor.LIGHT_PURPLE + "[CREATE LOBBY]";
    private static final String SIGN_JOIN = ChatColor.DARK_GREEN + "[JOIN LOBBY]";
    private static final String SIGN_SPACER = "&8&m----------";
    private static final String SIGN_ENDLINE = "&7Rightclick";

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


        if(l1.equalsIgnoreCase("create")){
            event.setLine(0, SIGN_HEADER);
            event.setLine(1, SIGN_CREATE);
            event.setLine(2, ChatUtil.colored(SIGN_SPACER));
            event.setLine(3, ChatUtil.colored(SIGN_ENDLINE));
        }
        else if(l1.equalsIgnoreCase("join")){
            event.setLine(0, SIGN_HEADER);
            event.setLine(1, SIGN_JOIN);
            event.setLine(2, ChatUtil.colored(SIGN_SPACER));
            event.setLine(3, ChatUtil.colored(SIGN_ENDLINE));
        }
        else{
            event.getBlock().breakNaturally();
            ChatManager.info().write("&cWrong sign command.", player);
            return;
        }

        ChatManager.info().write("&7Sign created.", player);
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

            if(!sign.getLine(0).equalsIgnoreCase(SIGN_HEADER)){
                return;
            }

            String type = sign.getLine(1);

            if(type.equals(SIGN_CREATE)){
                // CREATE A LOBBY
                if(!ArenaManager.hasFreeWorld()){
                    ChatManager.info().write("&cThere's no free world left!", player);
                    return;
                }
                SuperLibrary.callEvent(new GameSignInteractEvent(player, GameSign.Type.CREATE_GAME));
            }
            else if(type.equals(SIGN_JOIN)){
                // JOIN A LOBBY
                if(GameManager.getRunningGames().size() == 0){
                    ChatManager.info().write("&cThere's no game running at the moment!", player);
                    return;
                }

                SuperLibrary.callEvent(new GameSignInteractEvent(player, GameSign.Type.JOIN_GAME));
            }
        }
    }

    @EventHandler
    public void onGameSign(GameSignInteractEvent event){
        Player player = event.getPlayer();
        GameSign.Type type = event.getType();

        if(type == GameSign.Type.CREATE_GAME){
            GameCreateInventory.open(player);
        }
        else{
            SuperInventory inventory = GameManager.getGameOverview("Choose game", clickEvent -> {
                ItemStack item = clickEvent.getItem();
                clickEvent.cancelEvent();

                if(!item.hasItemMeta()){
                    return;
                }

                String id = item.getItemMeta().getDisplayName().split("#")[1];
                if(!SimpleStringUtils.isInteger(id)){
                    return;
                }
                int idInteger = Integer.parseInt(id);
                Game game = GameManager.getRunningGames().get(idInteger-1);

                // Call event for further things
                SuperLibrary.callEvent(new GameJoinEvent(game.getArena().getArena(),
                        clickEvent.getPlayer(), clickEvent.getPlayer().getLocation()));
                clickEvent.closeInventory();
            });
            player.openInventory(inventory.build());
        }
    }

}
