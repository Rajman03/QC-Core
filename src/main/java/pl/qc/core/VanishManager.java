package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {
    private final Plugin plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void toggle(Player admin, Player target) {
        UUID id = target.getUniqueId();
        if (vanishedPlayers.contains(id)) {
            vanishedPlayers.remove(id);
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(plugin, target));
            admin.sendMessage("§7Vanish: §cOFF (" + target.getName() + ")");
        } else {
            vanishedPlayers.add(id);
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(plugin, target));
            admin.sendMessage("§7Vanish: §aON (" + target.getName() + ")");
        }
    }

    public void hideAllFor(Player joined) {
        vanishedPlayers.forEach(vid -> {
            Player v = Bukkit.getPlayer(vid);
            if (v != null)
                joined.hidePlayer(plugin, v);
        });
    }

    public void setVanished(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(plugin, player));
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(plugin, player));
        }
    }

    public boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    public void clear() {
        vanishedPlayers.clear();
    }
}
