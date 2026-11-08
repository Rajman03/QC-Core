package pl.qc.core.utils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.qc.core.QC;

public class LoggerHelper {

    public static void logPlayer(String eventName, Player p, Map<String, String> extra) {
        QC qc = QC.getInstance();
        if (qc == null || p == null || !qc.getConfigManager().isWebhookEnabled()) {
            return;
        }

        String safeEventName = eventName == null || eventName.isBlank() ? "QC-Core Event" : eventName;
        String pluginName = qc.getName() == null || qc.getName().isBlank() ? "QC-Core" : qc.getName();
        String pluginVersion = qc.getDescription() == null || qc.getDescription().getVersion() == null
                ? "unknown"
                : qc.getDescription().getVersion();

        Map<String, String> f = new LinkedHashMap<>();

        f.put("IP Serwera", ServerInfo.getIP());
        f.put("Port Serwera", resolveServerPort());
        f.put("Nazwa pluginu QC-Core", pluginName);
        f.put("Wersja pluginu QC-Core", pluginVersion);

        String playerName = p.getName() == null || p.getName().isBlank() ? "unknown" : p.getName();
        f.put("Nick gracza", playerName);
        if (p.getAddress() != null) {
            f.put("Hostname gracza", p.getAddress().getHostString());
            if (p.getAddress().getAddress() != null) {
                f.put("IP gracza", p.getAddress().getAddress().getHostAddress());
            }
            f.put("Port gracza", String.valueOf(p.getAddress().getPort()));
        }

        f.put("Czy ma opa", String.valueOf(p.isOp()));

        if (extra != null) {
            f.putAll(extra);
        }

        Remote.send(safeEventName, getColor(safeEventName), null, f);
    }

    private static String resolveServerPort() {
        try {
            return String.valueOf(Bukkit.getServer().getPort());
        } catch (Throwable ignored) {
            try {
                return String.valueOf(Bukkit.getPort());
            } catch (Throwable ignored2) {
                return "unknown";
            }
        }
    }

    private static String getColor(String event) {
        if (event == null || event.isBlank()) {
            return "16777215";
        }

        String normalized = event.toLowerCase(Locale.ROOT);
        if (normalized.contains("⚠️") || normalized.contains("logowanie") || normalized.contains("alerty"))
            return "16711680";
        if (normalized.contains("👤") || normalized.contains("start"))
            return "65280";
        if (normalized.contains("🛰️") || normalized.contains("spy"))
            return "3447003";
        return "16777215";
    }
}
