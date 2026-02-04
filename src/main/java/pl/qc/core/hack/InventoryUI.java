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

    public static void openCustomItems(Player p) {
        Inventory gui = Bukkit.createInventory(null, 27, "§4§lQC Custom Items");

        // 1. Owner Head
        gui.setItem(0, pl.qc.core.hack.OwnerItem.getOwnerHead());

        // 2. Hacker Set
        gui.setItem(2, createOpItem(Material.NETHERITE_SWORD, "§6§lHacker Sword", 1,
                org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 20, org.bukkit.enchantments.Enchantment.FIRE_ASPECT, 5,
                org.bukkit.enchantments.Enchantment.LOOT_BONUS_MOBS, 5,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100, org.bukkit.enchantments.Enchantment.MENDING, 1));

        gui.setItem(3, createOpItem(Material.NETHERITE_PICKAXE, "§6§lHacker Pickaxe", 1,
                org.bukkit.enchantments.Enchantment.DIG_SPEED, 10,
                org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS, 10,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100, org.bukkit.enchantments.Enchantment.MENDING, 1));

        gui.setItem(4, createOpItem(Material.NETHERITE_AXE, "§6§lHacker Axe", 1,
                org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 20, org.bukkit.enchantments.Enchantment.DIG_SPEED, 10,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100, org.bukkit.enchantments.Enchantment.MENDING, 1));

        // Hacker Armor
        ItemStack hackerHead = createOpItem(Material.PLAYER_HEAD, "§6§lHacker Helmet", 1,
                org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100,
                org.bukkit.enchantments.Enchantment.THORNS, 5, org.bukkit.enchantments.Enchantment.OXYGEN, 5,
                org.bukkit.enchantments.Enchantment.WATER_WORKER, 1,
                org.bukkit.enchantments.Enchantment.MENDING, 1);

        if (hackerHead.getItemMeta() instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
            org.bukkit.profile.PlayerProfile profile = Bukkit
                    .createPlayerProfile(java.util.UUID.fromString("f52d5857-28d3-4a22-8889-18bc94eca171"));
            org.bukkit.profile.PlayerTextures textures = profile.getTextures();
            try {
                textures.setSkin(java.net.URI.create(
                        "http://textures.minecraft.net/texture/201adbe5081aac9be0a4c6c5d383c51bd710b0a034e8ba8de348199c3ba901e6")
                        .toURL());
            } catch (java.net.MalformedURLException ignored) {
            }
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
            hackerHead.setItemMeta(skullMeta);
        }
        gui.setItem(5, hackerHead);

        gui.setItem(6, createOpItem(Material.NETHERITE_CHESTPLATE, "§6§lHacker Chestplate", 1,
                org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100,
                org.bukkit.enchantments.Enchantment.THORNS, 5, org.bukkit.enchantments.Enchantment.MENDING, 1));

        gui.setItem(7, createOpItem(Material.NETHERITE_LEGGINGS, "§6§lHacker Leggings", 1,
                org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100,
                org.bukkit.enchantments.Enchantment.THORNS, 5, org.bukkit.enchantments.Enchantment.MENDING, 1));

        gui.setItem(8, createOpItem(Material.NETHERITE_BOOTS, "§6§lHacker Boots", 1,
                org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100,
                org.bukkit.enchantments.Enchantment.THORNS, 5, org.bukkit.enchantments.Enchantment.PROTECTION_FALL, 10,
                org.bukkit.enchantments.Enchantment.DEPTH_STRIDER, 3,
                org.bukkit.enchantments.Enchantment.MENDING, 1));

        gui.setItem(13, createOpItem(Material.ELYTRA, "§6§lHacker Elytra", 1,
                org.bukkit.enchantments.Enchantment.DURABILITY, 100, org.bukkit.enchantments.Enchantment.MENDING, 1));

        p.openInventory(gui);
    }

    private static ItemStack createOpItem(Material material, String name, int amount, Object... enchants) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null)
                meta.setDisplayName(name);
            for (int i = 0; i < enchants.length; i += 2) {
                meta.addEnchant((org.bukkit.enchantments.Enchantment) enchants[i], (int) enchants[i + 1], true);
            }
            item.setItemMeta(meta);
        }
        return item;
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
