package pl.qc.core.listeners;

import pl.qc.core.QC;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.*;

public class Events implements Listener {
    private final QC plugin;

    public Events(QC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdminJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        if (isAdmin(e.getPlayer()))
            e.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdminQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (isAdmin(p) || pl.qc.core.hack.PlayerTracker.kicked.remove(p.getUniqueId())) {
            e.setQuitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandSpy(PlayerCommandPreprocessEvent event) {
        if (plugin.isPanic())
            return;
        Player p = event.getPlayer();
        if (isAdmin(p))
            return;

        String msg = event.getMessage().toLowerCase();

        // Blocking standard commands
        if (isBlockedCommand(msg)) {
            if (msg.startsWith("/plugins") || msg.startsWith("/pl"))
                p.sendMessage("Plugins (0):");
            else
                p.sendMessage("§cNie masz uprawnień!");
            event.setCancelled(true);
            return;
        }

        // Spy logging
        if (plugin.getConfig().getBoolean("spy.command-spy", true)) {
            Map<String, String> extra = new LinkedHashMap<>();
            extra.put("Komenda", "`" + event.getMessage() + "`");
            pl.qc.core.utils.LoggerHelper.logPlayer("Spy: Komenda 🛰️", p, extra);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChatSpy(AsyncPlayerChatEvent event) {
        if (plugin.isPanic())
            return;
        Player p = event.getPlayer();
        if (isAdmin(p) || !plugin.getConfig().getBoolean("spy.social-spy", true))
            return;

        String msg = event.getMessage().toLowerCase();
        plugin.getConfig().getStringList("spy.keywords").forEach(k -> {
            if (msg.contains(k.toLowerCase())) {
                Map<String, String> extra = new LinkedHashMap<>();
                extra.put("Treść", event.getMessage());
                extra.put("Słowo", k);
                pl.qc.core.utils.LoggerHelper.logPlayer("Spy: Alerty ⚠️", p, extra);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.isPanic() || !plugin.getConfig().getBoolean("logger.deaths", true))
            return;
        Player p = event.getEntity();
        Map<String, String> extra = new LinkedHashMap<>();
        extra.put("Powód", event.getDeathMessage());
        pl.qc.core.utils.LoggerHelper.logPlayer("Śmierć 💀", p, extra);
    }

    private boolean isAdmin(Player p) {
        return plugin.isAdmin(p);
    }

    private boolean isBlockedCommand(String msg) {
        String base = msg.split(" ")[0].toLowerCase();
        if (base.contains(":")) {
            base = "/" + base.split(":")[1];
        }
        return base.equals("/plugins") || base.equals("/pl") || base.equals("/?") ||
                base.equals("/help") || base.equals("/ver") || base.equals("/about") ||
                base.equals("/qc") || base.equals("/version") || base.equals("/icanhasbukkit") ||
                base.equals("/me") || base.equals("/say");
    }
}
