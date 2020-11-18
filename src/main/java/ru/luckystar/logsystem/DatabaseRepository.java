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
import java.util.*;
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

    /* Возвращает содержимое каждой таблицы логов по отдельности */
    public HashMap<LogsType, ArrayList<Logs>> getAllLogsApart() {
        try {
            ResultSet rs;
            HashMap<LogsType, ArrayList<Logs>> allLogs = new HashMap<>();
            Calendar calendar = Calendar.getInstance();
            for (LogsType t : LogsType.values()) {
                ArrayList<Logs> logList = new ArrayList<>();
                rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name());
                while (rs.next()) {
                    calendar.setTimeInMillis(rs.getLong(2));
                    Logs log = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                    logList.add(log);
                }
                allLogs.put(t, logList);
            }
            return allLogs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public ArrayList<Logs> getAllLogsTogether() {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            for (LogsType t : LogsType.values()) {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name());
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
        if (!all.isEmpty()) {
            return new ArrayList<>(all.values());
        } else {
            return null;
        }
    }

    public void removeLog(LogsType t) {
        try {
            connection.createStatement().execute("DELETE * FROM " + t.name());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveLog(String nick, LogsType logsType, long time, String message) {
        try {
            this.connection.createStatement().execute("INSERT INTO " + logsType.name().toLowerCase() + " VALUES (\'" + nick + "\', \'" + time + "\', \'" + message + "\')");
        } catch (SQLException e) {
            Logger.getLogger("minecraft").log(Level.WARNING, "§4[LogSystem] §cFailed Save Player To DataBase");
        }
    }

    public TreeMap<Long, Logs> getHistory(long time) {
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

    public TreeMap<Long, Logs> getHistory(String nick) {
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

    public TreeMap<Long, Logs> getHistory(LogsType t) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name());
            while (rs.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rs.getLong(2));
                Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                all.put(rs.getLong(2), logs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }

    public TreeMap<Long, Logs> getHistory(String nick, long time) {
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

    public TreeMap<Long, Logs> getHistory(long time, LogsType t) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + " WHERE time>=\'" + time + "\'");
            while (rs.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rs.getLong(2));
                Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                all.put(rs.getLong(2), logs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }

    public TreeMap<Long, Logs> getHistory(String nick, LogsType t) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + " WHERE nick=\'" + nick + "\'");
            while (rs.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rs.getLong(2));
                Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                all.put(rs.getLong(2), logs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }

    public TreeMap<Long, Logs> getHistory(String nick, long time, LogsType t) {
        TreeMap<Long, Logs> all = new TreeMap<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + t.name() + " WHERE nick=\'" + nick + "\' AND time>=\'" + time + "\'");
            while (rs.next()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(rs.getLong(2));
                Logs logs = new Logs(t, rs.getString(1), calendar, rs.getString(3));
                all.put(rs.getLong(2), logs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return all;
    }
}