package pl.qc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.qc.core.QC;

public class CombatListener implements Listener {

    private final QC plugin;

    public CombatListener(QC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event == null) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof org.bukkit.entity.Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker != null) {
            double mult = plugin.getPlayerStateManager().getAttackMultiplier(attacker);
            if (mult > 1.0) {
                event.setDamage(event.getDamage() * mult);
            }
        }

        if (event.getEntity() instanceof Player defender) {
            double mult = plugin.getPlayerStateManager().getDefenseMultiplier(defender);
            if (mult != 1.0) {
                event.setDamage(event.getDamage() * mult);
            }
        }
    }
}
