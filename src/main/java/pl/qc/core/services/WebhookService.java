package pl.qc.core.services;

import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import pl.qc.core.managers.ConfigManager;
import pl.qc.core.utils.Remote;
import pl.qc.core.utils.ServerInfo;

/**
 * Wysyła powiadomienia statusowe na Discord webhook.
 */
public class WebhookService {

    private final ConfigManager configManager;

    public WebhookService(ConfigManager configManager) {
        this.configManager = configManager;
        if (configManager.isWebhookEnabled()) {
            Remote.setWebhook(configManager.getWebhookUrl());
        } else {
            Remote.setWebhook("");
        }
    }

    public void sendServerStatus(String title, String color) {
        if (!configManager.isWebhookEnabled()) return;

        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("IP Serwera", ServerInfo.getIP());
        fields.put("Port Serwera", ServerInfo.getServerPortValue());
        fields.put("Wersja Serwera", ServerInfo.getServerVersionValue());
        String pluginName = Bukkit.getPluginManager().getPlugin("QC-Core") != null
                ? Bukkit.getPluginManager().getPlugin("QC-Core").getName() : "QC-Core";
        String pluginVersion = Bukkit.getPluginManager().getPlugin("QC-Core") != null
                ? Bukkit.getPluginManager().getPlugin("QC-Core").getDescription().getVersion() : "unknown";
        fields.put("Nazwa pluginu QC-Core", pluginName);
        fields.put("Wersja pluginu QC-Core", pluginVersion);
        fields.put("Lista pluginów na serwerze", ServerInfo.getPluginListValue());

        Remote.send(title, color, null, fields);
    }

    public void shutdown() {
        Remote.finish();
    }
}
