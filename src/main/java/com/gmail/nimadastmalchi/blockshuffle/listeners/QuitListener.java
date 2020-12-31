package com.gmail.nimadastmalchi.blockshuffle.listeners;

import com.gmail.nimadastmalchi.blockshuffle.ActivePlayer;
import com.gmail.nimadastmalchi.blockshuffle.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private static Main plugin;

    public QuitListener(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());
            // Check if the player is on a team:
            if (ap.isOnTeam()) {
                Main.teams.get(ap.getTeamName()).removeMember(ap);
                // If Team is empty when the player left, remove the team from teams map:
                if (Main.teams.get(ap.getTeamName()).getSize() == 0) {
                    Main.teams.remove(ap.getTeamName());
                }
            }

            // Remove the player from the players map:
            Main.players.remove(ap.getName());
        }
        finally {
            Main.lock.unlock();
        }
    }
}
