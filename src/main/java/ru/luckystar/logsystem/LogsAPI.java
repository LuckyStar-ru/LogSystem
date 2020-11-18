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

    public TreeMap<Long, Logs> getHistory(String nick) {
        return this.dbRepo.getHistory(nick);
    }

    public TreeMap<Long, Logs> getHistory(long time) {
        return this.dbRepo.getHistory(time);
    }

    public TreeMap<Long, Logs> getHistory(LogsType t) {
        return this.dbRepo.getHistory(t);
    }

    public TreeMap<Long, Logs> getHistory(String nick, long time) {
        return this.dbRepo.getHistory(nick, time);
    }

    public TreeMap<Long, Logs> getHistory(String nick, LogsType t) {
        return this.dbRepo.getHistory(nick, t);
    }

    public TreeMap<Long, Logs> getHistory(long time, LogsType t) {
        return this.dbRepo.getHistory(time, t);
    }

    public TreeMap<Long, Logs> getHistory(String nick, long time, LogsType t) {
        return this.dbRepo.getHistory(nick, time, t);
    }

    public void saveLog(LogsType logsType, String nick, String message) {
        this.dbRepo.saveLog(nick, logsType, System.currentTimeMillis(), message);
    }

}
