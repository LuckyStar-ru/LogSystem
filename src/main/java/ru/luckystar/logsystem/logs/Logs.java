package ru.luckystar.logsystem.logs;

import java.util.Calendar;

public class Logs {
    private LogsType logsType;
    private String nick;
    private Calendar time;
    private String message;

    public Logs(LogsType logsType, String nick, Calendar time, String message) {
        this.logsType = logsType;
        this.nick = nick;
        this.time = time;
        this.message = message;
    }

    public String getNick() {
        return nick;
    }

    public Calendar getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

}
