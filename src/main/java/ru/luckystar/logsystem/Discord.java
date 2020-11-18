package ru.luckystar.logsystem;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Discord {

    private static Discord discord;
    JDA api;
    long channelID;

    private Discord(String token, long channelID) {
        try {
            this.channelID = channelID;
            this.api = JDABuilder.createDefault(token).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    static void init(String token, long channelID) {
        discord = new Discord(token, channelID);
    }

    public static Discord getInst() {
        return discord;
    }

    public void sendMessage(String message) {
        this.api.getTextChannelById(this.channelID).sendMessage(message);
    }
}
