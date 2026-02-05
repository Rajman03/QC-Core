package pl.qc.core.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.qc.core.QC;

public class SilentJoin implements Listener {
    private final QC plugin;

    public SilentJoin(QC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.isAdmin(p) && plugin.getConfig().getBoolean("silent-join", false)) {
            e.setJoinMessage(null);
            p.sendMessage(ChatColor.GRAY + "[QC] Dołączyłeś w trybie cichym.");
            plugin.getProcessor().getVanishManager().setVanished(p, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.isAdmin(p) && plugin.getConfig().getBoolean("silent-join", false)) {
            e.setQuitMessage(null);
        }
    }
}
