package com.gmail.nimadastmalchi.blockshuffle.commands;

import com.gmail.nimadastmalchi.blockshuffle.ActivePlayer;
import com.gmail.nimadastmalchi.blockshuffle.Main;
import com.gmail.nimadastmalchi.blockshuffle.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ListCommand implements CommandExecutor {
    private Main plugin;

    public ListCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("list").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // list, list TEAM_NAME
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check for an invalid command:
        if (args.length >= 2) {
            p.sendMessage(ChatColor.RED + "Invalid command syntax. Try again.");
            return true;
        }

        Main.lock.lock();
        try {
            String msg = "";
            if (args.length == 0) {
                boolean foundTeam = false;
                String teamPlayersMsg = ChatColor.GREEN + "Teams:\n" + ChatColor.WHITE;
                for (Map.Entry mapElement : Main.teams.entrySet()) {
                    foundTeam = true;
                    Team t = (Team) mapElement.getValue();
                    teamPlayersMsg += "    " + ChatColor.BLUE + t.getName() + ChatColor.WHITE + ": ";
                    for (ActivePlayer member : t.getMembers()) {
                        teamPlayersMsg += member.getName() + ", ";
                    }
                    teamPlayersMsg = teamPlayersMsg.substring(0, teamPlayersMsg.length() - 2);
                    teamPlayersMsg += "\n";
                }
                if (foundTeam) {
                    msg += teamPlayersMsg;
                }

                boolean foundSolo = false;
                String soloPlayersMsg;

                soloPlayersMsg = ChatColor.GREEN + "Solo players: " + ChatColor.WHITE;
                for (Map.Entry mapElement : Main.players.entrySet()) {
                    ActivePlayer ap = (ActivePlayer) mapElement.getValue();
                    if (!ap.isOnTeam() && ap.hasStarted()) {
                        foundSolo = true;
                        soloPlayersMsg += ap.getName() + ", ";
                    }
                }
                if (foundSolo) {
                    soloPlayersMsg = soloPlayersMsg.substring(0, soloPlayersMsg.length() - 2);
                    msg += soloPlayersMsg;
                }
            } else if (args.length == 1) {
                boolean foundTeam = false;
                if (Main.teams.containsKey(args[0])) {
                    Team t = Main.teams.get(args[0]);
                    for (ActivePlayer member : t.getMembers()) {
                        msg += member.getName() + ", ";
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Invalid team name. Try again.");
                    return true;
                }
                msg = msg.substring(0, msg.length() - 2);
            } else {
                p.sendMessage(ChatColor.RED + "Invalid command syntax. Try again.");
                return true;
            }

            if (msg.length() == 0) {
                p.sendMessage(ChatColor.RED + "No data.");
            } else {
                p.sendMessage(ChatColor.BLUE + msg);
            }
        } finally {
            Main.lock.unlock();
        }

        return true;
    }
}