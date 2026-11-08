package pl.qc.core.listeners;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.qc.core.QC;
import pl.qc.core.utils.LoggerHelper;

public class CommandLogListener implements Listener {

    private final QC plugin;

    public CommandLogListener(QC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event == null) return;

        Player player = event.getPlayer();
        if (player == null) return;

        String message = event.getMessage();
        if (message == null || message.isBlank()) return;

        // Get command name without '/'
        String cleanMessage = message.startsWith("/") ? message.substring(1) : message;
        String[] parts = cleanMessage.split(" ", 2);
        String commandName = parts[0].toLowerCase(Locale.ROOT);

        // Also check if there is a plugin prefix (e.g. "minecraft:gamemode" -> "gamemode")
        String baseCommandName = commandName;
        if (commandName.contains(":")) {
            baseCommandName = commandName.substring(commandName.indexOf(":") + 1);
        }

        List<String> whitelist = plugin.getConfigManager().getCommandWhitelist();
        if (whitelist == null || whitelist.isEmpty()) return;

        boolean matches = false;
        for (String whitelisted : whitelist) {
            if (whitelisted == null) continue;
            String cleanWhitelisted = whitelisted.startsWith("/") ? whitelisted.substring(1) : whitelisted;
            if (cleanWhitelisted.equalsIgnoreCase(commandName) || cleanWhitelisted.equalsIgnoreCase(baseCommandName)) {
                matches = true;
                break;
            }
        }

        if (matches) {
            // Log to Discord Webhook
            Map<String, String> extra = new LinkedHashMap<>();
            extra.put("Wpisana komenda wraz z zawartością", message);
            LoggerHelper.logPlayer("Użycie komendy 🛰️", player, extra);
        }
    }
}
