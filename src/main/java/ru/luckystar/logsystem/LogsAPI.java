package ru.luckystar.logsystem;

import ru.luckystar.logsystem.logs.Logs;
import ru.luckystar.logsystem.logs.LogsType;

import java.util.TreeMap;

public class LogsAPI {

    private static LogsAPI logs;
    private DatabaseRepository dbRepo;

    private LogsAPI(DatabaseRepository dbRepo) {
        this.dbRepo = dbRepo;
    }

    static void init(DatabaseRepository dbRepo) {
        logs = new LogsAPI(dbRepo);
    }

    public static LogsAPI getInst() {
        return logs;
    }

    public TreeMap<Long, Logs> getPlayerLogs(String nick) {
        return this.dbRepo.getLogsFromDatabase(nick);
    }

    public TreeMap<Long, Logs> getPlayerHistory(String nick, long time) {
        return this.dbRepo.getLogsFromDatabase(nick, time);
    }

    public TreeMap<Long, Logs> getHistory(long time) {
        return this.dbRepo.getHistoryFromDatabase(time);
    }

    public void saveLog(LogsType logsType, String nick, String message) {
        this.dbRepo.saveLogToDatabase(nick, logsType, System.currentTimeMillis(), message);
    }

}
