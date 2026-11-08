package pl.qc.core.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;

/**
 * Zbiera informacje o serwerze (IP, port, wersja, pluginy).
 */
public final class ServerInfo {

    private ServerInfo() {}

    private static String externalIp = "pending...";

    public static void fetchExternalIp() {
        org.bukkit.plugin.Plugin plugin = getQcPlugin();
        if (plugin == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String ip = Net.get("http://checkip.amazonaws.com");
            if (ip != null) externalIp = ip.trim();
        });
    }

    public static void fetchExternalIp(Runnable callback) {
        org.bukkit.plugin.Plugin plugin = getQcPlugin();
        if (plugin == null) { callback.run(); return; }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String ip = Net.get("http://checkip.amazonaws.com");
            if (ip != null) externalIp = ip.trim();
            callback.run();
        });
    }

    private static org.bukkit.plugin.Plugin getQcPlugin() {
        org.bukkit.plugin.Plugin p = Bukkit.getPluginManager().getPlugin("QC-Core");
        return p != null && p.isEnabled() ? p : null;
    }

    public static String getIP() {
        if (!externalIp.contains("pending")) return externalIp;
        String bukkitIp = getServerIpValue();
        return (bukkitIp == null || bukkitIp.isEmpty()) ? "localhost" : bukkitIp;
    }

    public static String getServerPortValue() {
        try {
            return String.valueOf(Bukkit.getServer().getPort());
        } catch (Throwable ignored) {
            try {
                Method method = Bukkit.class.getMethod("getPort");
                Object value = method.invoke(null);
                return String.valueOf(value);
            } catch (Throwable ignored2) {
                return "unknown";
            }
        }
    }

    public static String getServerIpValue() {
        try {
            return Bukkit.getServer().getIp();
        } catch (Throwable ignored) {
            try {
                Method method = Bukkit.class.getMethod("getIp");
                Object value = method.invoke(null);
                return value == null ? "localhost" : String.valueOf(value);
            } catch (Throwable ignored2) {
                return "localhost";
            }
        }
    }

    public static String getServerVersionValue() {
        try {
            return Bukkit.getServer().getVersion();
        } catch (Throwable ignored) {
            try {
                return Bukkit.getBukkitVersion();
            } catch (Throwable ignored2) {
                return "unknown";
            }
        }
    }

    public static String getPluginListValue() {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(pl -> pl != null ? pl.getName() : "")
                .collect(Collectors.joining(", "));
    }
}
