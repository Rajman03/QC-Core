package pl.qc.core;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import java.util.*;

public class Auth implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Map<String, String> f = new LinkedHashMap<>();
        f.put("Nick", p.getName());
        f.put("IP", p.getAddress() != null ? p.getAddress().getAddress().getHostAddress() : "unknown");
        f.put("OP", String.valueOf(p.isOp()));
        Remote.send("Gracz dołączył 👤", "65280", null, f);
    }
}
