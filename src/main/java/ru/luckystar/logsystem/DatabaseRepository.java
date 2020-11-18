package ru.luckystar.logsystem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import ru.luckystar.logsystem.logs.Logs;
import ru.luckystar.logsystem.logs.LogsType;
import ru.luckystar.logsystem.otherservices.Hastebin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseRepository {

    private final Hastebin hastebin = new Hastebin();
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM");
    public Connection connection = null;
    private Plugin plugin;

    public DatabaseRepository(Plugin plugin) {
        this.plugin = plugin;
        try {
            /* SQLite init */
            Class.forName("org.sqlite.JDBC").newInstance();
            connection = DriverManager.getConnection("jdbc:sqlite://" + plugin.getDataFolder().getAbsolutePath() + "/logs.db");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `joins` (`nick` varchar(32), `time` BIGINT, `action` varchar(255))");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `chat` (`nick` varchar(32), `time` BIGINT, `message` varchar(255))");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `commands` (`nick` varchar(32), `time` BIGINT, `command` varchar(255))");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `teleport` (`nick` varchar(32), `time` BIGINT, `position` varchar(255))");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `kills` (`nick` varchar(32), `time` BIGINT, `entity` varchar(32))");
        } catch (Exception e) {
            Logger.getLogger("minecraft").log(Level.WARNING, "§4[LogSystem] §cFailed Connect To DataBase");
            e.printStackTrace();
        }
    }

    public void saveLogsToHastebin() {
        try {
            FileConfiguration config = this.plugin.getConfig();
            ResultSet rs;
            ArrayList<String> all = new ArrayList<>();
            TreeMap<Long, Logs> all_logs = new TreeMap<>();
            // Логи по отдельности
            for (LogsType t : LogsType.values()) {
                StringBuilder temp = new StringBuilder();
                rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + "");
                while (rs.next()) {
                    temp.append("[").append(sdf.format(new Date(rs.getLong(2)))).append("] ").append(rs.getString(1)).append(" ").append(rs.getString(3)).append("\n");
                    Calendar calendar = Calendar.getInstance();
                    Calendar.getInstance().setTimeInMillis(rs.getLong(2));
                    Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                    all_logs.put(rs.getLong(2), logs);
                }
                String joinURL = hastebin.post(temp.toString(), false);
                Bukkit.getServer().getConsoleSender().sendMessage("§a[" + t.name() + "] " + joinURL);
                all.add("[" + t.name() + "]" + joinURL);
                connection.createStatement().execute("DELETE FROM " + t.name());
            }
            // Все логи вместе
            StringBuilder temp = new StringBuilder();
            all_logs.forEach((time, log) -> {
                temp.append("[").append(log.getTime()).append("] ").append(log.getNick()).append(" ").append(log.getMessage()).append("\n");
            });
            all.add(hastebin.post(temp.toString(), false));
            for (String s : all) {
                Discord.getInst().sendMessage(s);
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveLogToDatabase(String nick, LogsType logsType, long time, String message) {
        try {
            this.connection.createStatement().execute("INSERT INTO " + logsType.name().toLowerCase() + " VALUES (\'" + nick + "\', \'" + time + "\', \'" + message + "\')");
        } catch (SQLException e) {
            Logger.getLogger("minecraft").log(Level.WARNING, "§4[LogSystem] §cFailed Save Player To DataBase");
        }
    }

    public TreeMap<Long, Logs> getHistoryFromDatabase(long time) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            for (LogsType t : LogsType.values()) {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + " WHERE time>=\'" + time + "\'");
                while (rs.next()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(rs.getLong(2));
                    Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                    all.put(rs.getLong(2), logs);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }

    public TreeMap<Long, Logs> getLogsFromDatabase(String nick) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            for (LogsType t : LogsType.values()) {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + " WHERE nick=\'" + nick + "\'");
                while (rs.next()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(rs.getLong(2));
                    Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                    all.put(rs.getLong(2), logs);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }

    public TreeMap<Long, Logs> getLogsFromDatabase(String nick, long time) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            for (LogsType t : LogsType.values()) {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + " WHERE nick=\'" + nick + "\' AND time>=\'" + time + "\'");
                while (rs.next()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(rs.getLong(2));
                    Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                    all.put(rs.getLong(2), logs);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }
}