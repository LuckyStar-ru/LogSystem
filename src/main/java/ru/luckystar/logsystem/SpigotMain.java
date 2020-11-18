package ru.luckystar.logsystem;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.luckystar.logsystem.commands.HistoryCommand;
import ru.luckystar.logsystem.events.HandlerEvents;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SpigotMain extends JavaPlugin {

    public static DatabaseRepository dbRepo;

    @Override
    public void onEnable() {
        getConfigFile();
        dbRepo = new DatabaseRepository(this);
        Bukkit.getPluginManager().registerEvents(new HandlerEvents(), this);
        getCommand("history").setExecutor(new HistoryCommand(this));
        LogsAPI.init(dbRepo);
        Discord.init(getConfig().getString("discord.token"), getConfig().getLong("discord.channelID"));
    }

    @Override
    public void onDisable() {
        dbRepo.saveLogsToHastebin();
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
