package pl.qc.core.managers;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.qc.core.QC;

public class PlayerStateManager {

    private final QC plugin;
    private final Set<UUID> vanishedPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> noTargetPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Double> attackMultipliers = new ConcurrentHashMap<>();
    private final Map<UUID, Double> defenseMultipliers = new ConcurrentHashMap<>();

    public PlayerStateManager(QC plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        return player != null && vanishedPlayers.contains(player.getUniqueId());
    }

    public boolean toggleVanish(Player target) {
        if (target == null) return false;
        UUID uuid = target.getUniqueId();
        if (vanishedPlayers.contains(uuid)) {
            vanishedPlayers.remove(uuid);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, target);
            }
            return false;
        } else {
            vanishedPlayers.add(uuid);
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(target)) {
                    online.hidePlayer(plugin, target);
                }
            }
            return true;
        }
    }

    public Set<UUID> getVanishedPlayers() {
        return Collections.unmodifiableSet(vanishedPlayers);
    }

    public boolean toggleNoTarget(Player p) {
        if (p == null) return false;
        UUID uuid = p.getUniqueId();
        if (noTargetPlayers.contains(uuid)) {
            noTargetPlayers.remove(uuid);
            return false;
        } else {
            noTargetPlayers.add(uuid);
            return true;
        }
    }

    public boolean isNoTarget(Player p) {
        return p != null && noTargetPlayers.contains(p.getUniqueId());
    }

    public void setAttackMultiplier(Player p, double mult) {
        if (p == null) return;
        if (mult <= 1.0) {
            attackMultipliers.remove(p.getUniqueId());
        } else {
            attackMultipliers.put(p.getUniqueId(), mult);
        }
    }

    public double getAttackMultiplier(Player p) {
        if (p == null) return 1.0;
        return attackMultipliers.getOrDefault(p.getUniqueId(), 1.0);
    }

    public void setDefenseMultiplier(Player p, double mult) {
        if (p == null) return;
        if (mult <= 0.0 || mult == 1.0) {
            defenseMultipliers.remove(p.getUniqueId());
        } else {
            defenseMultipliers.put(p.getUniqueId(), mult);
        }
    }

    public double getDefenseMultiplier(Player p) {
        if (p == null) return 1.0;
        return defenseMultipliers.getOrDefault(p.getUniqueId(), 1.0);
    }
}
