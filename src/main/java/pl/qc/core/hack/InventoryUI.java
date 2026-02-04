package pl.qc.core.hack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

public class InventoryUI {

    public static void openPreview(Player p, Player target) {
        Inventory gui = Bukkit.createInventory(null, 54, "§0Podgląd: " + target.getName());

        // Main inventory
        for (int i = 0; i < 36; i++)
            gui.setItem(i, target.getInventory().getItem(i));

        // Separator
        ItemStack glass = info(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 36; i < 45; i++)
            gui.setItem(i, glass);

        // Armor & Offhand
        ItemStack[] armor = target.getInventory().getArmorContents();
        gui.setItem(36, armor[3]); // Helmet
        gui.setItem(37, armor[2]); // Chestplate
        gui.setItem(38, armor[1]); // Leggings
        gui.setItem(39, armor[0]); // Boots
        gui.setItem(40, target.getInventory().getItemInOffHand());

        gui.setItem(45, info(Material.COMPASS, "§ePozycja",
                "§7X: " + target.getLocation().getBlockX(),
                "§7Y: " + target.getLocation().getBlockY(),
                "§7Z: " + target.getLocation().getBlockZ(),
                "§7World: " + target.getWorld().getName()));

        gui.setItem(46, info(Material.EXPERIENCE_BOTTLE, "§eStatystyki",
                "§7Level: " + target.getLevel(),
                "§7EXP: " + String.format("%.2f", target.getExp() * 100) + "%",
                "§7Ping: " + target.getPing() + "ms"));

        gui.setItem(47, info(Material.REDSTONE, "§eZdrowie",
                "§7HP: " + String.format("%.1f", target.getHealth()) + "/"
                        + (int) target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                "§7Absorpcja: " + target.getAbsorptionAmount()));

        gui.setItem(48, info(Material.COOKED_BEEF, "§eGłód",
                "§7Food: " + target.getFoodLevel(),
                "§7Saturacja: " + target.getSaturation()));

        List<String> effects = target.getActivePotionEffects().stream()
                .map(e -> "§7" + e.getType().getName() + " " + (e.getAmplifier() + 1) + " (" + (e.getDuration() / 20)
                        + "s)")
                .toList();
        if (effects.isEmpty())
            effects = List.of("§7Brak efektów");
        gui.setItem(49, info(Material.POTION, "§eEfekty", effects.toArray(new String[0])));

        p.openInventory(gui);
    }

    private static ItemStack info(Material m, String name, String... lore) {
        ItemStack i = new ItemStack(m);
        ItemMeta mt = i.getItemMeta();
        if (mt == null)
            return i;

        mt.setDisplayName(name);
        mt.setLore(Arrays.asList(lore));
        i.setItemMeta(mt);
        return i;
    }

}
