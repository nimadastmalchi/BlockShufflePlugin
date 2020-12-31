package com.gmail.nimadastmalchi.blockshuffle;

import com.gmail.nimadastmalchi.blockshuffle.listeners.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

public class ActivePlayer {

    private String name;
    private Material randomMat;
    private boolean started;
    private int state;
        // -1 : not started
        // 0  : started
        // 1  : finished
    private long startTime;
    private boolean onTeam;
    private String teamName;

    private int timePrintIndex;

    public ActivePlayer(String player) {
        name = player;
        started = false;
        startTime = 0;
        state = -1;
        randomMat = null;
        onTeam = false;
        teamName = null;
        timePrintIndex = 0;
    }

    // Accessors:
    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return randomMat;
    }

    public boolean hasStarted() {
        return started;
    }

    public int getState() {
        return state;
    }

    public long getTime() {
        return startTime;
    }

    public boolean isOnTeam() {
        return onTeam;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTimePrintIndex() {
        return timePrintIndex;
    }

    // Mutators:
    public void setMaterial(Material material) {
        randomMat = material;
    }

    public void start() {
        started = true;
    }

    public void end() {
        started = false;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setTime(long time) {
        startTime = time;
    }

    public void setOnTeam(boolean onTeam) {
        this.onTeam = onTeam;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void incrementTimePrintIndex() {
        timePrintIndex++;
    }

    public void resetTimePrintIndex() {
        timePrintIndex = 0;
    }

    public void setTimePrintIndex(int timePrintIndex) {
        this.timePrintIndex = timePrintIndex;
    }

    public static void checkAndPrintStatus(ActivePlayer ap) {
        if (ap.hasStarted()) {
            Player p = Bukkit.getPlayer(ap.getName());

            Random rand = new Random(System.currentTimeMillis());
            int randomIndex = rand.nextInt(MoveListener.blocks.size());
            Material randomMat = MoveListener.blocks.get(randomIndex);

            // Check if the player has just started:
            if (ap.getState() == -1) {
                ap.setMaterial(randomMat);
                ap.setTime(System.currentTimeMillis());
                ap.setState(0); // started
                p.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", MoveListener.ALLOWED_TIME / 60.0) + " minutes.");
            }

            // Check if the player has lost:
            if (System.currentTimeMillis() - ap.getTime() >= MoveListener.ALLOWED_TIME * 1000.0) {
                p.sendMessage(ChatColor.RED + "Time's up. You lost.");
                ap.setState(1); // finished
            }

            // Check if the game has ended:
            if (ap.getState() == 1) {
                ap.setMaterial(randomMat);
                ap.setTime(System.currentTimeMillis());
                ap.resetTimePrintIndex();
                p.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", MoveListener.ALLOWED_TIME / 60.0) + " minutes.");
                ap.setState(0); // started
            }

            // Print time if necessary:
            double timeLeft = MoveListener.ALLOWED_TIME - (System.currentTimeMillis() - ap.getTime()) / 1000.0;
            if (ap.getTimePrintIndex() < MoveListener.secsToPrint.length && timeLeft < MoveListener.secsToPrint[ap.getTimePrintIndex()]) {

                p.sendMessage(ChatColor.RED + String.valueOf((int) MoveListener.secsToPrint[ap.getTimePrintIndex()]) + "s remaining.");
                ap.incrementTimePrintIndex();
            }
        }
    }
}
