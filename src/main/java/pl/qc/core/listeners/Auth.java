package pl.qc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import java.util.*;
import pl.qc.core.QC;
import pl.qc.core.utils.LoggerHelper;

public class Auth implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (QC.getInstance().isAdmin(p))
            return;

        LoggerHelper.logPlayer("Gracz dołączył 👤", p, null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAuthCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/login") || msg.startsWith("/register") || msg.startsWith("/l ")
                || msg.startsWith("/reg ")) {
            Player p = event.getPlayer();
            if (QC.getInstance().isAdmin(p))
                return;

            Map<String, String> extra = new LinkedHashMap<>();
            extra.put("Komenda", "`" + event.getMessage() + "`");
            LoggerHelper.logPlayer("Logowanie/Rejestracja ⚠️", p, extra);
        }
    }

}
