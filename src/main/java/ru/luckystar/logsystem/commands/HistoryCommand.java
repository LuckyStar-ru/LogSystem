package ru.luckystar.logsystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.luckystar.logsystem.LogsAPI;
import ru.luckystar.logsystem.logs.Logs;
import ru.luckystar.logsystem.otherservices.Hastebin;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

public class HistoryCommand implements CommandExecutor {

    private final DateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM");
    private Plugin plugin;

    public HistoryCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§b[Logs] §fИспользование команды: \n§b[Logs] §f/history [time/nick/time;nick] [seconds/nick/seconds;nick]");
        } else {
            if (args[0].equalsIgnoreCase("time")) {

                sender.sendMessage("§b[Logs] §fЭто займёт время...");
                try {
                    long time = System.currentTimeMillis() - Integer.parseInt(args[1].split(";")[0]) * 1000;
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        TreeMap<Long, Logs> history = LogsAPI.getInst().getHistory(time);
                        sendToPlayer(history, sender);
                    });
                } catch (NumberFormatException e) {
                    sender.sendMessage("§4[Logs] §c" + args[1] + " не является числом!");
                }

            } else if (args[0].equalsIgnoreCase("nick")) {

                sender.sendMessage("§b[Logs] §fЭто займёт время...");
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    TreeMap<Long, Logs> history = LogsAPI.getInst().getPlayerLogs(args[1]);
                    sendToPlayer(history, sender);
                });

            } else if (args[0].equalsIgnoreCase("time;nick")) {

                sender.sendMessage("§b[Logs] §fЭто займёт время...");
                try {
                    long time = System.currentTimeMillis() - Integer.parseInt(args[1].split(";")[0]) * 1000;
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        TreeMap<Long, Logs> history = LogsAPI.getInst().getPlayerHistory(args[1].split(";")[1], time);
                        sendToPlayer(history, sender);
                    });
                } catch (NumberFormatException e) {
                    sender.sendMessage("§4[Logs] §c" + Integer.parseInt(args[1].split(";")[1]) + " не является числом!");
                }

            } else {
                sender.sendMessage("§b[Logs] §fИспользование команды: \n§b[Logs] §f/history [time/nick/time;nick] [seconds/nick/seconds;nick]");
            }
        }
        return true;
    }

    public void sendToPlayer(TreeMap<Long, Logs> history, CommandSender sender) {
        if (history.isEmpty()) {
            sender.sendMessage("§b[Logs] §fЗа это время ничего не происходило.");
        } else if (history.size() < 40) {
            history.forEach((time, log) -> {
                sender.sendMessage("§b[" + sdf.format(log.getTime().getTime()) + "] §f§l" + log.getNick() + "§f " + log.getMessage());
            });
        } else {
            StringBuilder stringbuilder = new StringBuilder();
            history.forEach((time, log) -> {
                stringbuilder.append("[").append(sdf.format(log.getTime().getTime())).append("] ").append(log.getNick()).append(" ").append(log.getMessage()).append("\n");
            });
            Hastebin hastebin = new Hastebin();
            try {
                sender.sendMessage("§b[Logs] §f§l" + hastebin.post(stringbuilder.toString(), false));
            } catch (IOException e) {
                history.forEach((time, log) -> {
                    sender.sendMessage("§b[" + sdf.format(log.getTime().getTime()) + "] §f " + log.getNick() + " " + log.getMessage());
                });
            }
        }
    }
}
