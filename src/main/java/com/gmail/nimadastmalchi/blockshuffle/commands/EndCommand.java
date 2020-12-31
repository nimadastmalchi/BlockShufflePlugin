package com.gmail.nimadastmalchi.blockshuffle.commands;

import com.gmail.nimadastmalchi.blockshuffle.ActivePlayer;
import com.gmail.nimadastmalchi.blockshuffle.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndCommand implements CommandExecutor {
    private Main plugin;

    public EndCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("end").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check for an invalid command:
        if (args.length != 0) {
            p.sendMessage(ChatColor.RED + "Invalid command syntax. Try again.");
            return true;
        }

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());

            // Check if player has not started yet:
            if (!ap.hasStarted()) {
                p.sendMessage(ChatColor.RED + "Block Shuffle has not started yet. Use '/start' to begin.");
                return true;
            }

            // If player has already started:
            p.sendMessage(ChatColor.BLUE + "Thanks for playing Block Shuffle!");
            // Check if the player is already on a team (if so, remove from team):
            if (ap.isOnTeam()) {
                Main.teams.get(ap.getTeamName()).removeMember(ap);

                // If Team is empty when the player left, remove the team from teams map
                if (Main.teams.get(ap.getTeamName()).getSize() == 0) {
                    Main.teams.remove(ap.getTeamName());
                }

                // Possibly pointless
                ap.setOnTeam(false);
                ap.setTeamName(null);
            }
            Main.players.put(p.getName(), new ActivePlayer(p.getName()));
        } finally {
            Main.lock.unlock();
        }

        return true;
    }
}