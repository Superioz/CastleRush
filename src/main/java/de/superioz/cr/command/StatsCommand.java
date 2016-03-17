package de.superioz.cr.command;

import de.superioz.cr.common.ChatManager;
import de.superioz.cr.common.Verifier;
import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.cr.common.stats.PlayerStats;
import de.superioz.cr.common.stats.StatsManager;
import de.superioz.cr.main.CastleRush;
import de.superioz.cr.common.settings.PluginSettings;
import de.superioz.library.bukkit.common.command.Command;
import de.superioz.library.bukkit.common.command.context.CommandContext;
import de.superioz.library.java.util.SimpleStringUtils;
import org.bukkit.entity.Player;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class StatsCommand {

    @Command(label = "stats", aliases = {"statistics", "showstats"}, desc = "Shows the stats of yourself or a friend",
            permission = "castlerush.showstats", usage = "[player]")
    public void stats(CommandContext commandContext){
        Player player = (Player) commandContext.getSender();

        // Check enabled
        if(!PluginSettings.PLAYERSTATS_ENABLED){
            ChatManager.info().write(LanguageManager.get("statsArentEnabled"), player);
            return;
        }

        // Check connection
        if(!CastleRush.getDatabaseManager().check()){
            ChatManager.info().write(LanguageManager.get("databaseNotConnected"), player);
            return;
        }

        // Check player
        String name = commandContext.getArgumentsLength() >= 1 ? commandContext.getArgument(1) : player.getDisplayName();

        // Check database
        if(!StatsManager.contains(name)){
            ChatManager.info().write(LanguageManager.get("playerIsntInDatabase").replace("%name", name), player);
            return;
        }

        PlayerStats stats = StatsManager.getStats(name);
        assert stats != null;

        // Message
        ChatManager.stats().write(LanguageManager.get("statsOfPlayer")
                .replace("%player", name)
                .replace("%stats", stats.toColoredString()), player);
    }

    @Command(label = "setstats", aliases = "setstatistics", desc = "Sets the stats of given player",
            permission = "castlerush.setstats", min = 1, usage = "[player] <elo> <wins> <loses>")
    public void setstats(CommandContext context){
        Player player = (Player) context.getSender();

        // Check enabled
        if(!PluginSettings.PLAYERSTATS_ENABLED){
            ChatManager.info().write(LanguageManager.get("statsArentEnabled"), player);
            return;
        }

        // Check connection
        if(!CastleRush.getDatabaseManager().check()){
            ChatManager.info().write(LanguageManager.get("databaseNotConnected"), player);
            return;
        }

        // Check player
        String name = context.getArgument(1);

        // Check database
        if(!StatsManager.contains(name)){
            ChatManager.info().write(LanguageManager.get("playerIsntInDatabase").replace("%name", name), player);
            return;
        }

        int elo = PluginSettings.PLAYERSTATS_DEFAULT_ELO;
        int wins = 0;
        int loses = 0;

        // Get arguments
        if(context.getArgumentsLength() >= 2
                && SimpleStringUtils.isInteger(context.getArgument(2))){
            elo = Verifier.verifyInteger(context.getArgument(2), PluginSettings.PLAYERSTATS_DEFAULT_ELO);
        }
        if(context.getArgumentsLength() >= 3
                && SimpleStringUtils.isInteger(context.getArgument(3))){
            wins = Verifier.verifyInteger(context.getArgument(3), 0);
        }
        if(context.getArgumentsLength() >= 4
                && SimpleStringUtils.isInteger(context.getArgument(4))){
            loses = Verifier.verifyInteger(context.getArgument(4), 0);
        }

        // Get old stats
        PlayerStats oldStats = StatsManager.getStats(name);
        assert oldStats != null;

        // Get new stats
        PlayerStats newStats = new PlayerStats(wins, loses, elo, oldStats.getUuid(), name);
        StatsManager.updateStats(newStats);

        // Get and send messages
        String[] messages = LanguageManager.get("statsUpdated").split("\n");

        ChatManager.stats().write(messages[0].replace("%player", name), player);
        ChatManager.stats().write(messages[1]
                .replace("%oldStats", oldStats.toSimpleColoredString())
                .replace("%newStats", newStats.toSimpleColoredString())
                .replace("%arrow", "➜"), player);
    }

    @Command(label = "ranklist", aliases = {"rank", "top"}, desc = "Shows the top players",
            permission = "castlerush.showstats", usage = "<numberOfPlayers>")
    public void ranklist(CommandContext context){
        Player player = (Player) context.getSender();

        // Check enabled
        if(!PluginSettings.PLAYERSTATS_ENABLED){
            ChatManager.info().write(LanguageManager.get("statsArentEnabled"), player);
            return;
        }

        // Check connection
        if(!CastleRush.getDatabaseManager().check()){
            ChatManager.info().write(LanguageManager.get("databaseNotConnected"), player);
            return;
        }

        // get value
        int val = 5;
        if(context.getArgumentsLength() >= 1){
            String valString = context.getArgument(1);
            val = Verifier.verifyInteger(valString, 5);
        }
        if(val > 50) val = 5;

        ChatManager.info().write(LanguageManager.get("topPlayersHeader").replace("%val", val+""), player);
        int counter = 1;
        for(PlayerStats stats : StatsManager.getTop(val)){
            String color = "&7";

            if(counter == 1) color = "&6";
            else if(counter == 2) color = "&f";

            ChatManager.info().write("&8-- " + color + "#" + counter + " " + stats.getCurrentName()
                    + " " + CastleRush.BLOCK_SPACER + " &e" + stats.getElo() + " &7Elo"
                    + " " + CastleRush.BLOCK_SPACER + " &e" + stats.getWins() + " &7Win(s)"
                    + " " + CastleRush.BLOCK_SPACER + " &e" + stats.getLoses() + " &7Lose(s)", player);
            counter++;
        }
    }

}
