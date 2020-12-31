package com.gmail.nimadastmalchi.blockshuffle.listeners;

import com.gmail.nimadastmalchi.blockshuffle.ActivePlayer;
import com.gmail.nimadastmalchi.blockshuffle.Main;

import com.gmail.nimadastmalchi.blockshuffle.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;


public class MoveListener implements Listener {

    private static Main plugin;
    public static ArrayList<Material> blocks;
    public static final long ALLOWED_TIME = 60;
    public static final double secsToPrint[] = {30, 20, 10, 5, 4, 3, 2, 1};

    public MoveListener(Main plugin) {
        this.plugin = plugin;

        // Initialize the blocks array:
        blocks = new ArrayList<>();
        for (Material mat : Material.values()) {
            if (mat.isSolid()) {
                blocks.add(mat);
            }
        }

        Bukkit.getPluginManager().registerEvents((Listener) this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());

            // Must check that material is not null as it may not be generated in time by checkAndPrintStatus functions.
            if (ap.hasStarted() && ap.getMaterial() != null) {
                if (ap.isOnTeam()) {
                    Team t = Main.teams.get(ap.getTeamName());

                    Location loc = e.getTo();
                    // Check the block that the player is standing on:
                    Block blo = loc.clone().subtract(0, 1, 0).getBlock();
                    if (blo.getType().name().equals(t.getMaterial().name())) {
                        t.sendMessage(ChatColor.GREEN + "Success!");
                        t.setState(1);
                    }
                } else {
                    Location loc = e.getTo();
                    // Check the block that the player is standing on:
                    Block blo = loc.clone().subtract(0, 1, 0).getBlock();
                    if (blo.getType().name().equals(ap.getMaterial().name())) {
                        p.sendMessage(ChatColor.GREEN + "Success!");
                        ap.setState(1);
                    }
                }
            }
        } finally {
            Main.lock.unlock();
        }
    }

}
