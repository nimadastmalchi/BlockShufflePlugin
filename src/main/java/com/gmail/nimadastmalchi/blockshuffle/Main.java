package com.gmail.nimadastmalchi.blockshuffle;

import com.gmail.nimadastmalchi.blockshuffle.commands.EndCommand;
import com.gmail.nimadastmalchi.blockshuffle.commands.ListCommand;
import com.gmail.nimadastmalchi.blockshuffle.listeners.QuitListener;
import org.bukkit.plugin.java.JavaPlugin;
import com.gmail.nimadastmalchi.blockshuffle.commands.StartCommand;
import com.gmail.nimadastmalchi.blockshuffle.listeners.JoinListener;
import com.gmail.nimadastmalchi.blockshuffle.listeners.MoveListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends JavaPlugin {

    public static HashMap<String, ActivePlayer> players;
    public static HashMap<String, Team> teams;
    public static ReentrantLock lock;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        players = new HashMap<>();
        teams = new HashMap<>();
        lock = new ReentrantLock();

        // Commands and Listeners:
        new StartCommand(this);
        new EndCommand(this);
        new ListCommand(this);
        new JoinListener(this);
        new MoveListener(this);
        new QuitListener(this);

        Thread th = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        for (Map.Entry mapElement : teams.entrySet()) {
                            Team t = (Team) mapElement.getValue();
                            Team.checkAndPrintStatus(t);
                        }
                        for (Map.Entry mapElement : players.entrySet()) {
                            ActivePlayer ap = (ActivePlayer) mapElement.getValue();
                            if (!ap.isOnTeam()) {
                                ActivePlayer.checkAndPrintStatus(ap);
                            }
                        }
                    }
                    finally {
                        lock.unlock();
                    }
                }
            }
        });
        th.start();
    }

    public static String capitalize(String input) {
        String words[] = input.split("\\s");
        String capitalized = "";
        for (String word : words) {
            String first = word.substring(0, 1);
            String afterFirst = word.substring(1).toLowerCase();
            capitalized += first.toUpperCase() + afterFirst + " ";
        }
        return capitalized.trim();
    }
}