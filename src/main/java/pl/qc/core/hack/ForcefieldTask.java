package pl.qc.core.hack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ForcefieldTask implements Runnable {

    private final PlayerTracker tracker;

    public ForcefieldTask(PlayerTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void run() {
        for (UUID uid : tracker.forcefield) {
            Player p = Bukkit.getPlayer(uid);
            if (p != null && p.isOnline()) {
                runForcefield(p);
            }
        }
    }

    private void runForcefield(Player p) {
        double radius = 4.0;
        Location center = p.getLocation();

        // Visuals
        p.getWorld().spawnParticle(Particle.PORTAL, center.clone().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);

        for (Entity e : p.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof LivingEntity && e != p) {
                // Determine vector away from player
                Vector direction = e.getLocation().toVector().subtract(center.toVector()).normalize();

                // Add upward force
                direction.setY(0.5);
                direction.multiply(1.5);

                e.setVelocity(direction);

                // Effects
                e.getWorld().playSound(e.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f);
                e.getWorld().spawnParticle(Particle.CLOUD, e.getLocation(), 10, 0.2, 0.2, 0.2, 0.1);

                if (e instanceof Player target) {
                    target.sendMessage("§cOdsun sie!");
                }
            }
        }
    }
}
