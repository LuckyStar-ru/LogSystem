package ru.luckystar.logsystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.luckystar.logsystem.LogsAPI;
import ru.luckystar.logsystem.logs.Logs;
import ru.luckystar.logsystem.logs.LogsType;
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
        if (args.length != 1 && args.length != 2 && args.length != 3) {
            sender.sendMessage(
                    "§b[Logs] §fИспользование команды: " +
                            "\n§f/history (time) (nick) (action)" +
                            "\n§7(action) - JOINS, KILLS, CHAT, TELEPORT, COMMANDS." +
                            "\n§fЕсли аргумент 'ник' или 'время' не нужен, вместо аргумента писать ';', без кавычек." +
                            "\n§fАргумент (action) можно не писать!"
            );
        } else {
            long time = 0;
            String nick = ";";
            LogsType t = null;
            // Время
            if (!args[0].equals(";")) {
                try {
                    time = System.currentTimeMillis() - Integer.parseInt(args[0]) * 1000;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§4[Logs] §c" + args[0] + " не является числом.");
                    return true;
                }
            }
            // Ник
            if (args.length == 2) {
                if (args[1].length() <= 32) {
                    nick = args[1];
                } else {
                    sender.sendMessage("§4[Logs] §c" + args[1] + " не является действительным игроком.");
                    return true;
                }
            }
            // ЛогТайп
            if (args.length == 3) {
                try {
                    t = LogsType.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(
                            "§4[Logs] §c" + args[2] + " не является одним из подходящих значений:" +
                                    "\n§4[JOINS | KILLS | CHAT | TELEPORT | COMMANDS]"
                    );
                    return true;
                }
            }
            // Поиск по ДБ в асинке
            runSearch(sender, nick, time, t);
        }
        return true;
    }

    public void runSearch(CommandSender sender, String nick, long time, LogsType t) {
        sender.sendMessage("§b[Logs] §fЭто займёт время...");
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            TreeMap<Long, Logs> history;
            if (!nick.equals(";")) {
                if (time == 0) {
                    if (t == null) {
                        history = LogsAPI.getInst().getHistory(nick);
                    } else {
                        history = LogsAPI.getInst().getHistory(nick, t);
                    }
                } else {
                    if (t == null) {
                        history = LogsAPI.getInst().getHistory(nick, time);
                    } else {
                        history = LogsAPI.getInst().getHistory(nick, time, t);
                    }
                }
            } else {
                if (time == 0) {
                    if (t == null) {
                        history = LogsAPI.getInst().getHistory(time);
                    } else {
                        history = LogsAPI.getInst().getHistory(t);
                    }
                } else {
                    if (t == null) {
                        history = LogsAPI.getInst().getHistory(time);
                    } else {
                        history = LogsAPI.getInst().getHistory(time, t);
                    }
                }
            }
            sendToPlayer(history, sender);
        });
    }

    public void sendToPlayer(TreeMap<Long, Logs> history, CommandSender sender) {
        if (history.isEmpty()) {
            sender.sendMessage("§4[Logs] §cЗа это время ничего не происходило.");
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
