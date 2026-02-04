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
        for (int i = 0; i < 36; i++)
            gui.setItem(i, target.getInventory().getItem(i));

        gui.setItem(45, info(Material.COMPASS, "§ePozycja",
                "§7X: " + target.getLocation().getBlockX(),
                "§7Y: " + target.getLocation().getBlockY(),
                "§7Z: " + target.getLocation().getBlockZ()));

        gui.setItem(46, info(Material.EXPERIENCE_BOTTLE, "§eStatystyki",
                "§7Level: " + target.getLevel(),
                "§7EXP: " + target.getExp()));

        gui.setItem(47, info(Material.REDSTONE, "§eZdrowie",
                "§7HP: " + (int) target.getHealth() + "/"
                        + (int) target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));

        gui.setItem(48, info(Material.COOKED_BEEF, "§eGłód",
                "§7Food: " + target.getFoodLevel(),
                "§7Saturacja: " + target.getSaturation()));

        List<String> effects = target.getActivePotionEffects().stream()
                .map(e -> "§7" + e.getType().getName() + " " + (e.getAmplifier() + 1))
                .toList();
        gui.setItem(49, info(Material.POTION, "§eEfekty", effects.toArray(new String[0])));

        p.openInventory(gui);
    }

    private static ItemStack info(Material m, String name, String... lore) {
        ItemStack i = new ItemStack(m);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) {
            mt.setDisplayName(name);
            mt.setLore(Arrays.asList(lore));
            i.setItemMeta(mt);
        }
        return i;
    }
}
