package pl.qc.core.queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.UUID;

public class QueueManager {

    private final LinkedList<UUID> queue = new LinkedList<>();
    // Mapa do śledzenia ostatniej próby wejścia, aby uniknąć wycieków pamięci
    private final java.util.Map<UUID, Long> lastHeartbeat = new java.util.HashMap<>();
    private final int maxPlayers;
    private final long timeoutMs;
    private final String bypassPermission;

    public QueueManager(org.bukkit.configuration.file.FileConfiguration config) {
        this.maxPlayers = config.getInt("queue.max-players", 50);
        this.timeoutMs = config.getLong("queue.timeout-minutes", 2) * 60 * 1000;
        this.bypassPermission = config.getString("queue.bypass-permission", "qc.queue.bypass");
    }

    public void addToQueue(UUID playerId) {
        lastHeartbeat.put(playerId, System.currentTimeMillis());
        if (!queue.contains(playerId)) {
            queue.add(playerId);
        }
    }

    public void cleanup() {
        long limit = System.currentTimeMillis() - timeoutMs;

        java.util.Iterator<UUID> it = queue.iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            Long last = lastHeartbeat.get(id);
            if (last == null || last < limit) {
                it.remove();
                lastHeartbeat.remove(id);
            }
        }
        // Czyszczenie sierot z mapy
        lastHeartbeat.entrySet().removeIf(entry -> entry.getValue() < limit);
    }

    public void removeFromQueue(UUID playerId) {
        queue.remove(playerId);
        lastHeartbeat.remove(playerId);
    }

    public boolean isQueued(UUID playerId) {
        return queue.contains(playerId);
    }

    public int getPosition(UUID playerId) {
        return queue.indexOf(playerId) + 1;
    }

    public boolean canJoin(Player player) {
        // Logika VIP / Bypass
        if (player.hasPermission(bypassPermission)) {
            return true;
        }

        // Jeśli serwer nie jest pełny, wpuszczamy
        if (Bukkit.getOnlinePlayers().size() < maxPlayers) {
            return true;
        }

        // Jeśli gracz jest pierwszy w kolejce i zwolniło się miejsce (ta logika
        // wymagałaby taska sprawdzającego)
        // W prostym modelu na pojedynczym serwerze zazwyczaj po prostu wyrzucamy z
        // informacją
        return false;
    }

    public String getKickMessage(UUID playerId) {
        int pos = getPosition(playerId);
        int total = queue.size();
        return ChatColor.RED + "Serwer jest pełny!\n" +
                ChatColor.YELLOW + "Jesteś " + ChatColor.AQUA + pos + ChatColor.YELLOW + " z " + ChatColor.AQUA + total
                + ChatColor.YELLOW + " w kolejce.\n" +
                ChatColor.GRAY + "Spróbuj połączyć się ponownie za chwilę.";
    }
}
