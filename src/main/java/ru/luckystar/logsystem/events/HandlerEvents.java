package ru.luckystar.logsystem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import ru.luckystar.logsystem.LogsAPI;
import ru.luckystar.logsystem.logs.LogsType;

public class HandlerEvents implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        LogsAPI.getInst().saveLog(LogsType.JOINS, event.getPlayer().getName(), "зашёл на сервер");
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        LogsAPI.getInst().saveLog(LogsType.JOINS, event.getPlayer().getName(), "вышел с сервера");
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        LogsAPI.getInst().saveLog(LogsType.JOINS, event.getPlayer().getName(), "Кикнут: " + event.getReason());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            LogsAPI.getInst().saveLog(LogsType.KILLS, event.getEntity().getKiller().getName(), "убил игрока " + event.getEntity().getName());
        }
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        LogsAPI.getInst().saveLog(LogsType.CHAT, event.getPlayer().getName(), "Сообщение: " + event.getMessage());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        LogsAPI.getInst().saveLog(LogsType.COMMANDS, event.getPlayer().getName(), "Команда: " + event.getMessage());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN)
                || event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            LogsAPI.getInst().saveLog(LogsType.TELEPORT, event.getPlayer().getName(), "Телепортировался в X=" +
                    event.getTo().getX() + " Y=" + event.getTo().getY() + "Z=" + event.getTo().getZ());
        }
    }
}
