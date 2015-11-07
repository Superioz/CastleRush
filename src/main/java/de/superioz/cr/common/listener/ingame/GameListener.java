package de.superioz.cr.common.listener.ingame;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.events.GameLeaveEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.cr.common.game.division.GamePhase;
import de.superioz.cr.common.game.division.GameState;
import de.superioz.cr.main.CastleRush;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class GameListener implements Listener {

    public HashMap<Player , ItemStack[]> deathDrops = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();

        if(!GameManager.isIngame(player))
            return;

        ItemStack[] content = event.getEntity().getInventory().getContents();
        deathDrops.put(event.getEntity(), content);
        event.getEntity().getInventory().clear();

        event.getDrops().clear();
        event.setDroppedExp(0);
    }


    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player p1 = (Player) event.getEntity();

            if(event.getDamager() instanceof Player){
                Player p2 = (Player) event.getDamager();

                if(GameManager.isIngame(p1)
                        && GameManager.isIngame(p2)){
                    WrappedGamePlayer gp1 = GameManager.getWrappedGamePlayer(p1);
                    WrappedGamePlayer gp2 = GameManager.getWrappedGamePlayer(p2);

                    if(gp2.getTeamMates().contains(gp1))
                        event.setCancelled(true);
                }
            }
            else if(event.getDamager() instanceof Projectile){
                ProjectileSource source = ((Projectile)event.getDamager()).getShooter();

                if(source instanceof Player){
                    Player p2 = (Player) source;

                    if(GameManager.isIngame(p1)
                            && GameManager.isIngame(p2)){
                        WrappedGamePlayer gp1 = GameManager.getWrappedGamePlayer(p1);
                        WrappedGamePlayer gp2 = GameManager.getWrappedGamePlayer(p2);

                        if(gp2.getTeamMates().contains(gp1))
                            event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;

        event.setRespawnLocation(gp.getSpawnLocation());

        new BukkitRunnable(){
            @Override
            public void run(){
                if(deathDrops.containsKey(event.getPlayer())){
                    event.getPlayer().getInventory().clear();
                    for(ItemStack stack : deathDrops.get(event.getPlayer())){
                        if(stack != null)
                            event.getPlayer().getInventory().addItem(stack);
                    }

                    deathDrops.remove(event.getPlayer());
                }
                gp.getGame().getArena().getArena().getItemKit().setArmor(event.getPlayer());
            }
        }.runTaskLater(CastleRush.getInstance(), 1L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;
        Game game = gp.getGame();

        if(game.getArena().getGameState() != GameState.INGAME){
            CastleRush.getPluginManager().callEvent(new GameLeaveEvent(game, gp));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;
        Game game = gp.getGame();

        if(event.getItem() == null)
            return;

        if(player.getGameMode() == GameMode.SURVIVAL
            && event.getItem().getType() == Material.WATER_BUCKET
                || event.getItem().getType() == Material.LAVA_BUCKET){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(!GameManager.isIngame(player))
            return;
        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player); assert gp != null;
        Game game = gp.getGame();

        if(game.getArena().getGamePhase() != GamePhase.WAIT)
            return;

        Location to = event.getTo();
        Location spawn = gp.getSpawnLocation();

        if(to.distance(spawn) > 10){
            gp.teleport(spawn);
        }
    }

}
