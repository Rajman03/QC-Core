package pl.qc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import pl.qc.core.QC;

public class MobTargetListener implements Listener {

    private final QC plugin;

    public MobTargetListener(QC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event == null) return;

        if (event.getTarget() instanceof Player player) {
            if (plugin.getPlayerStateManager().isNoTarget(player)) {
                event.setCancelled(true);
            }
        }
    }
}
