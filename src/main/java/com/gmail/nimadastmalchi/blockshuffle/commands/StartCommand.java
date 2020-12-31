package com.gmail.nimadastmalchi.blockshuffle.commands;

import com.gmail.nimadastmalchi.blockshuffle.ActivePlayer;
import com.gmail.nimadastmalchi.blockshuffle.Main;
import com.gmail.nimadastmalchi.blockshuffle.Team;

import com.gmail.nimadastmalchi.blockshuffle.listeners.MoveListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    private Main plugin;

    public StartCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("start").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check for an invalid command:
        if (args.length >= 2) {
            p.sendMessage(ChatColor.RED + "The name of a team cannot contain any spaces. Try again.");
            return true;
        }

        Main.lock.lock();
        try {
            // ActivePlayer objects are added to players in Join listener
            ActivePlayer ap = Main.players.get(p.getName());

            if (ap.hasStarted()) {
                p.sendMessage(ChatColor.RED + "Block Shuffle has not been stopped yet. Use '/end'.");
                return true;
            }

            p.sendMessage(ChatColor.GREEN + "Welcome to Block Shuffle!");

            // If the player has specified a team:
            if (args.length == 1) {
                String teamName = args[0];
                if (Main.teams.containsKey(teamName)) {
                    Team t = Main.teams.get(teamName);
                    t.addMember(ap);
                    if (t.getMaterial() != null) {
                        double timeLeftInMinutes = (MoveListener.ALLOWED_TIME - (System.currentTimeMillis() - t.getTime()) / 1000.0) / 60.0;
                        p.sendMessage("Find " + Main.capitalize(t.getMaterial().name().replace("_", " ")) + ". You have " + String.format("%.2f", timeLeftInMinutes) + " minutes.");
                    }
                } else {
                    Team team = new Team(teamName);
                    team.addMember(ap);
                    Main.teams.put(teamName, team);
                }
            }
            // If the player did not specify a team:
            else {
                Main.players.get(p.getName()).start();
            }
        }
        finally {
            Main.lock.unlock();
        }

        return true;
    }
}
