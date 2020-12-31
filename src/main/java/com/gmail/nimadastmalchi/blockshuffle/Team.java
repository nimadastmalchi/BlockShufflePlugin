package com.gmail.nimadastmalchi.blockshuffle;

import com.gmail.nimadastmalchi.blockshuffle.listeners.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class Team {
    private String name;
    private ArrayList<ActivePlayer> members;
    private Material randomMat;

    private boolean started;
    private int state;
    private long startTime;

    private int timePrintIndex;

    // Accessors
    public Team(String name) {
        members = new ArrayList<>();
        this.name = name;
        started = true; // if a team is created, then the game has started for every player on that team
        startTime = 0;
        state = -1; // this state indicates that a randomMat must be assigned in Move
        randomMat = null;
        timePrintIndex = 0;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return members.size();
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

    public ArrayList<ActivePlayer> getMembers() {
        return members;
    }

    public int getTimePrintIndex() {
        return timePrintIndex;
    }

    public void addMember(ActivePlayer ap) {
        ap.setMaterial(randomMat);
        ap.setTime(startTime);
        ap.setState(state);
        ap.setTeamName(name);
        ap.setOnTeam(true);
        ap.start();
        ap.setTimePrintIndex(timePrintIndex);
        members.add(ap);
    }

    // Mutators
    public void removeMember(ActivePlayer ap) {
        ap.end(); // possibly pointless
        members.remove(ap);
    }

    public void setMaterial(Material m) {
        randomMat = m;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setMaterial(m);
        }
    }

    public void start() {
        started = true;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).start();
        }
    }

    public void setState(int state) {
        this.state = state;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setState(state);
        }
    }

    public void setTime(long time) {
        startTime = time;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setTime(time);
        }
    }

    public void incrementTimePrintIndex() {
        timePrintIndex++;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).incrementTimePrintIndex();
        }
    }

    public void resetTimePrintIndex() {
        timePrintIndex = 0;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).resetTimePrintIndex();
        }
    }

    public void setTimePrintIndex(int timePrintIndex) {
        this.timePrintIndex = timePrintIndex;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setTimePrintIndex(timePrintIndex);
        }
    }

    public void sendMessage(String msg) {
        for (int i = 0; i < members.size(); i++) {
            Player p = Bukkit.getPlayer(members.get(i).getName());
            p.sendMessage(msg);
        }
    }

    public static void checkAndPrintStatus(Team t) {
        Random rand = new Random(System.currentTimeMillis());
        int randomIndex = rand.nextInt(MoveListener.blocks.size());
        Material randomMat = MoveListener.blocks.get(randomIndex);

        // Check if the game has just started:
        if (t.getState() == -1) {
            t.setMaterial(randomMat);
            t.setTime(System.currentTimeMillis());
            t.setState(0);
            t.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", MoveListener.ALLOWED_TIME / 60.0) + " minutes.");
        }

        // Check if the team has lost:
        if (System.currentTimeMillis() - t.getTime() >= MoveListener.ALLOWED_TIME * 1000.0) {
            t.sendMessage(ChatColor.RED + "Time's up. You lost.");
            t.setState(1); // finished
        }

        // Check whether the game has ended:
        if (t.getState() == 1) {
            t.setMaterial(randomMat);
            // Reset the starting time:
            t.setTime(System.currentTimeMillis());
            t.resetTimePrintIndex();
            t.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", MoveListener.ALLOWED_TIME / 60.0) + " minutes.");
            t.setState(0); // started
        }

        // Print time if necessary:
        double timeLeft = MoveListener.ALLOWED_TIME - (System.currentTimeMillis() - t.getTime()) / 1000.0;
        if (t.getTimePrintIndex() < MoveListener.secsToPrint.length && timeLeft < MoveListener.secsToPrint[t.getTimePrintIndex()]) {
            t.sendMessage(ChatColor.RED + String.valueOf((int) MoveListener.secsToPrint[t.getTimePrintIndex()]) + "s remaining.");
            t.incrementTimePrintIndex();
        }
    }
}
