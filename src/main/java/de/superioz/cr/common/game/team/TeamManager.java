package de.superioz.cr.common.game.team;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.event.GameTeamChangeEvent;
import de.superioz.cr.common.game.Game;
import de.superioz.cr.common.game.GameManager;
import de.superioz.library.bukkit.BukkitLibrary;
import de.superioz.library.bukkit.common.inventory.InventorySize;
import de.superioz.library.bukkit.common.inventory.SuperInventory;
import de.superioz.library.bukkit.common.item.InteractableSimpleItem;
import de.superioz.library.bukkit.common.item.SimpleItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class TeamManager {

    protected List<Team> teams;
    protected Game game;

    public TeamManager(Game game){
        teams = new ArrayList<>();
        this.game = game;

        for(int i = 0; i < game.getArena().getArena().getGamePlots().size(); i++){
            teams.add(new Team(TeamColor.from(i).name().toLowerCase()));
        }
    }

    /**
     * Get index of given team
     *
     * @param team The team
     *
     * @return The index
     */
    public int getIndex(Team team){
        for(int i = 0; i < teams.size(); i++){
            Team t = teams.get(i);

            if(t.equals(team))
                return i;
        }
        return -1;
    }

    /**
     * Add wrapped player
     *
     * @param player The player
     * @param index  The index
     *
     * @return Result as boolean
     */
    public boolean add(WrappedGamePlayer player, int index){
        Team team = getTeam(index);

        if(team.getTeamPlayer().contains(player)){
            return false;
        }

        team.getTeamPlayer().add(player);
        BukkitLibrary.callEvent(new GameTeamChangeEvent(player.getGame(), team));
        return true;
    }

    /**
     * Remove given player
     *
     * @param player The player
     */
    public void remove(WrappedGamePlayer player){
        Team team = player.getTeam();
        teams.stream().filter(t -> t.getTeamPlayer().contains(player)).forEach(t -> t.getTeamPlayer().remove(player));

        if(team != null)
            BukkitLibrary.callEvent(new GameTeamChangeEvent(player.getGame(), team));
    }

    /**
     * Set given player to index
     *
     * @param player The player
     * @param index  The index
     *
     * @return The result
     */
    public boolean set(WrappedGamePlayer player, int index){
        this.remove(player);
        return this.add(player, index);
    }

    /**
     * Get the team with given index
     *
     * @param index The index
     *
     * @return The team
     */
    public Team getTeam(int index){
        return teams.get(index);
    }

    /**
     * Get the item from given team
     *
     * @param team The team
     *
     * @return The item
     */
    public SimpleItem getItem(Team team){
        int index = getIndex(team);
        TeamColor teamColor = TeamColor.from(index);
        SimpleItem item = new SimpleItem(Material.WOOL);

        for(WrappedGamePlayer gp : team.getTeamPlayer()){
            item.addLore(LanguageManager.get("teamWoolListItem").replace("%color", teamColor.getChatColor() + "").replace
                    ("%playername", gp.getPlayer().getDisplayName()));
        }

        return item.setName(teamColor.getChatColor() + team.getName()).setColor
                (teamColor.getDyeColor()).addLore("", LanguageManager.get("teamChooseLoreChange"));
    }

    /**
     * Check if given player has a team
     *
     * @param player The player
     *
     * @return The result
     */
    public boolean contains(WrappedGamePlayer player){
        return get(player) != null;
    }

    /**
     * Get the team of given player
     *
     * @param player The player
     *
     * @return The team
     */
    public Team get(WrappedGamePlayer player){
        for(Team t : teams){
            for(WrappedGamePlayer gp : t.getTeamPlayer()){
                if(gp.equals(player))
                    return t;
            }
        }
        return null;
    }

    /**
     * Shuffle all players to teams
     *
     * @param players The playerlist
     */
    public void shuffle(List<WrappedGamePlayer> players){
        boolean flag = false;

        for(Team t : teams){
            if(t.getTeamPlayer().size() == 0){
                this.reset();
                flag = true;
            }
        }

        if(flag){
            for(WrappedGamePlayer gamePlayer : players){
                this.add(gamePlayer, getIndex(getTeamWithLowestSize()));
            }
        }
    }

    /**
     * Get the team with lowest size
     *
     * @return The team
     */
    public Team getTeamWithLowestSize(){
        Team lowest = getTeam(0);

        for(Team t : getTeams()){
            if(t.getTeamPlayer().size() < lowest.getTeamPlayer().size()){
                lowest = t;
            }
        }
        return lowest;
    }

    /**
     * Reset every team
     */
    public void reset(){
        for(Team t : teams){
            t.setTeamPlayer(new ArrayList<>());
        }
    }

    /**
     * Get the color from given team
     *
     * @param team The team
     *
     * @return The color
     */
    public TeamColor getColor(Team team){
        return TeamColor.from(getIndex(team));
    }

    /**
     * Get inventory of team choosing
     *
     * @return The inventory
     */
    public SuperInventory getInventory(){
        SuperInventory inventory =
                new SuperInventory(LanguageManager.get("teamChooseToolTitleRaw"), InventorySize.ONE_ROW);
        for(Team team : getTeams()){
            InteractableSimpleItem item =
                    new InteractableSimpleItem(getIndex(team) + 1, getItem(team), inventory, event -> {
                        Player player = event.getPlayer();

                        if(!GameManager.isIngame(player.getUniqueId()))
                            return;
                        WrappedGamePlayer gp = GameManager.getWrappedGamePlayer(player);

                        event.cancelEvent();
                        event.closeInventory();

                        int index = event.getSlot();
                        gp.getGame().getTeamManager().set(gp, index);
                        ChatManager.info().write(LanguageManager.get("youAreNowInTeam").replace("%team", getTeam(index)
                                .getColoredName(gp.getGame())), player);
                    });
            inventory.set(item);
        }
        return inventory;
    }

    // -- Intern methods

    public List<Team> getTeams(){
        return teams;
    }

    public Game getGame(){
        return game;
    }
}
