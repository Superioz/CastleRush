package de.superioz.cr.common.settings;

import lombok.Setter;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
@Setter
public class GameSettings {

    public int buildTimer;
    public boolean rankedMatch;
    public boolean enterPlotDuringBuild;
    public boolean pvp;

    public GameSettings(){
        this.buildTimer = PluginSettings.GAME_TIMER;
        this.rankedMatch = PluginSettings.PLAYERSTATS_ELO_ENABLED;
        this.enterPlotDuringBuild = PluginSettings.ENTER_PLOT_DURING_BUILD;
        this.pvp = true;
    }

    public int getBuildTimer(){
        return buildTimer;
    }

    public boolean isRankedMatch(){
        return rankedMatch;
    }

    public boolean isPvp(){
        return pvp;
    }

    public boolean canEnterPlotDuringBuild(){
        return enterPlotDuringBuild;
    }

}
