package pl.qc.core.hack;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

    public void repair(Player p) {
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getItemMeta() instanceof Damageable d) {
                d.setDamage(0);
                i.setItemMeta((ItemMeta) d);
            }
        }
        p.sendMessage("§7Ekwipunek: §aNaprawiono");
    }

    public void dupe(Player p) {
        ItemStack i = p.getInventory().getItemInMainHand();
        if (i.getType() != Material.AIR) {
            p.getInventory().addItem(i.clone());
            p.sendMessage("§7Przedmiot: §aZduplikowano");
        }
    }

    public void give(Player p, String materialName) {
        Material m = Material.matchMaterial(materialName.toUpperCase());
        if (m != null) {
            p.getInventory().addItem(new ItemStack(m, 1));
        }
    }

    public void giveItem(Player p, Material m, int amount) {
        p.getInventory().addItem(new ItemStack(m, amount));
    }

    public void giveEnchantedBook(Player p, Enchantment e, int level) {
        ItemStack b = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta m = (EnchantmentStorageMeta) b.getItemMeta();
        if (m != null) {
            m.addStoredEnchant(e, level, true);
            b.setItemMeta(m);
        }
        p.getInventory().addItem(b);
    }
}
