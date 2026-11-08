package pl.qc.core.listeners;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.qc.core.QC;

public class AdminJoinListener implements Listener {

    private final QC plugin;

    public AdminJoinListener(QC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdminJoin(PlayerJoinEvent e) {
        if (e == null) return;

        Player p = e.getPlayer();
        if (p == null) return;

        // Apply vanish visibility sync
        for (UUID vanishedUuid : plugin.getPlayerStateManager().getVanishedPlayers()) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedUuid);
            if (vanishedPlayer != null && vanishedPlayer.isOnline()) {
                p.hidePlayer(plugin, vanishedPlayer);
            }
        }

        if (plugin.getPlayerStateManager().isVanished(p)) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(p)) {
                    online.hidePlayer(plugin, p);
                }
            }
        }

        // Display admin panel welcome
        if (plugin.getAdminManager().isAdmin(p)) {
            p.sendMessage("§8[QC-Core] §aWitaj " + p.getName() + " (Panel Administracyjny)");
            p.sendMessage("§8[QC-Core] §7Seed świata: §f" + p.getWorld().getSeed());

            String plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .map(pl -> pl != null ? pl.getName() : "")
                    .collect(Collectors.joining("§7, §a"));
            p.sendMessage("§8[QC-Core] §7Pluginy: §a" + plugins);
        }
    }
}
