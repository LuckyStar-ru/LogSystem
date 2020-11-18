package ru.luckystar.logsystem;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Discord {

    private static Discord discord;
    private JDA api;
    private String channelID;

    private Discord(String token, String channelID) {
        try {
            this.channelID = channelID;
            this.api = JDABuilder.createDefault(token).build();
            this.api.setAutoReconnect(true);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    static void init(String token, String channelID) {
        discord = new Discord(token, channelID);
    }

    public static Discord getInst() {
        return discord;
    }

    public void sendMessage(String message) {
        this.api.getTextChannelById(this.channelID).sendMessage(message).queue();
    }
}
