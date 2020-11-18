package ru.luckystar.logsystem;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.luckystar.logsystem.commands.HistoryCommand;
import ru.luckystar.logsystem.events.HandlerEvents;
import ru.luckystar.logsystem.logs.Logs;
import ru.luckystar.logsystem.logs.LogsType;
import ru.luckystar.logsystem.otherservices.Hastebin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class SpigotMain extends JavaPlugin {

    private DatabaseRepository dbRepo;

    @Override
    public void onEnable() {
        getConfigFile();
        dbRepo = new DatabaseRepository(this);
        Bukkit.getPluginManager().registerEvents(new HandlerEvents(), this);
        getCommand("history").setExecutor(new HistoryCommand(this));
        LogsAPI.init(dbRepo);
        Discord.init(getConfig().getString("discord.token"), getConfig().getString("discord.channelID"));
    }

    @Override
    public void onDisable() {
        Hastebin hastebin = new Hastebin();
        Discord discord = Discord.getInst();
        HashMap<LogsType, ArrayList<Logs>> allLogsApart = dbRepo.getAllLogsApart();
        ArrayList<Logs> allLogsTogether = dbRepo.getAllLogsTogether();
        if (allLogsApart != null && allLogsTogether != null) {

            for (LogsType t : LogsType.values()) {
                String url = hastebin.postLogs(allLogsApart.get(t));
                if (url != null) {
                    Bukkit.getConsoleSender().sendMessage("§a[Logs] §bSAVE - " + url);
                    discord.sendMessage("LOG TYPE - **" + t.name() + "** = __**" + url + "**__");
                    dbRepo.removeLog(t);
                } else {
                    Bukkit.getConsoleSender().sendMessage("§c[Logs] §4Exception when saved log - " + t.name());
                }
            }
            String url = hastebin.postLogs(allLogsTogether);
            Bukkit.getConsoleSender().sendMessage("§a[Logs] §bALL logs - " + url);
            discord.sendMessage("ALL LOGS - __**" + url + "**__");

        }
    }

    public File getConfigFile() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try (InputStream in = getResource("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
