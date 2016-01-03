package de.superioz.cr.common.game.team;

import de.superioz.cr.common.WrappedGamePlayer;
import de.superioz.cr.common.game.Game;
import de.superioz.library.java.util.SimpleStringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class Team {

    public List<WrappedGamePlayer> teamPlayer;
    public String name;

    public Team(String name){
        this.name = name;
        this.teamPlayer = new ArrayList<>();
    }

    /**
     * Gets name with upper first letter
     *
     * @return The name
     */
    public String getName(){
        return SimpleStringUtils.upperFirstLetter(name.toLowerCase());
    }

    /**
     * Get upper first letter name colored
     *
     * @param game The game to get the coplor
     *
     * @return The colored name
     */
    public String getColoredName(Game game){
        return getColor(game) + getName();
    }

    /**
     * Get color of this team
     *
     * @param game The game where the team is
     *
     * @return The color as chatcolor
     */
    public ChatColor getColor(Game game){
        return game.getTeamManager().getColor(this).getChatColor();
    }

    /**
     * Get teammate by given name
     *
     * @param name The name
     *
     * @return The game player
     */
    public WrappedGamePlayer getMate(String name){
        for(WrappedGamePlayer gp : getTeamPlayer()){
            if(gp.getPlayer().getDisplayName().equalsIgnoreCase(name))
                return gp;
        }
        return null;
    }

    // -- Intern methods

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Team)) return false;

        Team team = (Team) o;

        return getName() != null ? getName().equals(team.getName()) : team.getName() == null;

    }

    @Override
    public int hashCode(){
        return getName() != null ? getName().hashCode() : 0;
    }

    public List<WrappedGamePlayer> getTeamPlayer(){
        return teamPlayer;
    }

    public void setTeamPlayer(List<WrappedGamePlayer> teamPlayer){
        this.teamPlayer = teamPlayer;
    }

}
