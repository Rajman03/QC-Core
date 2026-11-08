package pl.qc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.qc.core.QC;
import pl.qc.core.utils.LoggerHelper;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        if (event == null)
            return;

        Player p = event.getPlayer();
        QC qc = QC.getInstance();
        if (qc == null || (qc.getAdminManager() != null && qc.getAdminManager().isAdmin(p))) {
            event.setJoinMessage(null);
            return;
        }

        LoggerHelper.logPlayer("Gracz dołączył 👤", p, null);
    }
}
