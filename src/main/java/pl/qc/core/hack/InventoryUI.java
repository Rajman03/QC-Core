package pl.qc.core.hack;

import pl.qc.core.QC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryUI {

        private static Inventory customItemsGui;

        // --- Main Control Panel ---

        public static void openControlPanel(Player p) {
                Inventory gui = Bukkit.createInventory(null, 54, "§c§lQC-Core Panel");

                // 1. Serwerowe
                gui.setItem(10, info(Material.REDSTONE_BLOCK, "§cReload Config", "§7Przeładuj konfigurację"));
                gui.setItem(11, info(Material.TNT, "§cPanic Mode", "§7Włącz/Wyłącz tryb paniki"));
                gui.setItem(12, info(Material.CLOCK, "§6Start/Stop Plugins", "§7Zarządzaj pluginami"));

                // 2. Gracz & Troll
                gui.setItem(14, info(Material.PLAYER_HEAD, "§eLista Graczy", "§7Zarządzaj graczami online"));
                gui.setItem(15, info(Material.ENDER_PEARL, "§bTeleportacja", "§7Menu teleportacji"));
                gui.setItem(16, info(Material.NETHERITE_SWORD, "§4Hack Items", "§7Odbierz przedmioty"));

                // 3. Efekty
                gui.setItem(19, info(Material.POTION, "§aEfekty", "§7Nadaj sobie efekty"));
                gui.setItem(20, info(Material.BEACON, "§bBuffy", "§7Nadaj sobie buffy"));
                gui.setItem(21, info(Material.ENDER_CHEST, "§5Tajny Schowek", "§7Otwórz zdalny ekwipunek"));

                // 4. Inne
                gui.setItem(23, info(Material.COMMAND_BLOCK, "§7Console Command", "§7Wykonaj komendę jako konsola"));
                gui.setItem(24, info(Material.BARRIER, "§cReset All", "§7Wyczyść bazy danych"));

                // Wypełnienie tła
                ItemStack glass = info(Material.BLACK_STAINED_GLASS_PANE, " ");
                for (int i = 0; i < 54; i++) {
                        if (gui.getItem(i) == null)
                                gui.setItem(i, glass);
                }

                p.openInventory(gui);
        }

        // --- Player Selector GUI ---

        public static void openPlayerSelector(Player p) {
                int size = 54;
                Inventory gui = Bukkit.createInventory(null, size, "§8Lista Graczy");

                int i = 0;
                for (Player online : Bukkit.getOnlinePlayers()) {
                        if (i >= size)
                                break;

                        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) head.getItemMeta();
                        if (meta != null) {
                                meta.setOwningPlayer(online);
                                meta.setDisplayName("§e" + online.getName());
                                meta.setLore(Arrays.asList(
                                                "§7UUID: " + online.getUniqueId().toString(),
                                                "§7-----------------",
                                                "§aKliknij, aby zarządzać"));
                                head.setItemMeta(meta);
                        }
                        gui.setItem(i++, head);
                }
                gui.setItem(53, info(Material.ARROW, "§cPowrót", "§7Wróć do głównego panelu"));
                p.openInventory(gui);
        }

        // --- Player Options GUI ---

        public static void openPlayerOptions(Player admin, Player target) {
                Inventory gui = Bukkit.createInventory(null, 27, "§8Opcje: " + target.getName());
                Processor proc = QC.getInstance().getProcessor();

                // Status checks
                boolean isVanished = proc.getVanishManager().isVanished(target.getUniqueId());
                boolean hasReach = proc.getPlayerTracker().reach.contains(target.getUniqueId());
                boolean noTarget = proc.getPlayerTracker().noTarget.contains(target.getUniqueId());
                boolean noAdv = proc.getPlayerTracker().noAdvancements.contains(target.getUniqueId());
                boolean ff = proc.getPlayerTracker().forcefield.contains(target.getUniqueId());

                gui.setItem(0, toggleItem(Material.ENDER_EYE, "§eVanish", isVanished));
                gui.setItem(1, toggleItem(Material.FISHING_ROD, "§eReach (300m)", hasReach));
                gui.setItem(2, toggleItem(Material.SHIELD, "§eNoTarget", noTarget));
                gui.setItem(3, toggleItem(Material.BOOK, "§eNoAdvancements", noAdv));

                gui.setItem(4, info(Material.DIAMOND_SWORD, "§cMnożnik Dmg x100", "§7Kliknij, aby włączyć dla gracza"));
                gui.setItem(5, info(Material.GOLDEN_APPLE, "§aGodMode", "§7Kliknij, aby włączyć nieśmiertelność"));
                gui.setItem(6, toggleItem(Material.NETHER_STAR, "§bForcefield", ff));

                gui.setItem(8, info(Material.BARRIER, "§cWyrzuć (Kick)", "§7Kliknij, aby wyrzucić z serwera"));
                gui.setItem(26, info(Material.ARROW, "§cPowrót", "§7Wróć do listy graczy"));

                admin.openInventory(gui);
        }

        private static ItemStack toggleItem(Material m, String name, boolean state) {
                ItemStack item = new ItemStack(m);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                        meta.setDisplayName(name);
                        meta.setLore(Arrays.asList(
                                        state ? "§a[WŁĄCZONE]" : "§c[WYŁĄCZONE]",
                                        "§7Kliknij, aby przełączyć"));
                        if (state)
                                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                        item.setItemMeta(meta);
                }
                return item;
        }

        // --- Existing Methods ---

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

                // Safely get Max Health
                double maxHealth = 20.0;
                if (target.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                        maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                }

                gui.setItem(47, info(Material.REDSTONE, "§cZdrowie",
                                "§7HP: §c" + String.format("%.1f", target.getHealth()) + "§7/§c" + (int) maxHealth,
                                "§7Absorpcja: §6" + (int) target.getAbsorptionAmount()));

                gui.setItem(48, info(Material.COOKED_BEEF, "§eGłód",
                                "§7Food: " + target.getFoodLevel(),
                                "§7Saturacja: " + String.format("%.1f", target.getSaturation())));

                List<String> effects = target.getActivePotionEffects().stream()
                                .map(e -> "§7" + e.getType().getName() + " " + (e.getAmplifier() + 1) + " ("
                                                + (e.getDuration() / 20)
                                                + "s)")
                                .toList();
                if (effects.isEmpty())
                        effects = List.of("§7Brak efektów");
                gui.setItem(49, info(Material.POTION, "§eEfekty", effects.toArray(new String[0])));

                p.openInventory(gui);
        }

        public static void openCustomItems(Player p) {
                if (customItemsGui == null) {
                        initializeCustomItemsGui();
                }
                p.openInventory(customItemsGui);
        }

        private static void initializeCustomItemsGui() {
                customItemsGui = Bukkit.createInventory(null, 27, "§4§lQC Custom Items");

                // 1. Owner Head
                customItemsGui.setItem(0, pl.qc.core.hack.OwnerItem.getOwnerHead());

                // 2. Hacker Set
                customItemsGui.setItem(2, createOpItem(Material.NETHERITE_SWORD, "§6§lHacker Sword", 1,
                                Enchantment.DAMAGE_ALL, 20, Enchantment.FIRE_ASPECT, 5,
                                Enchantment.LOOT_BONUS_MOBS, 5,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                customItemsGui.setItem(3, createOpItem(Material.NETHERITE_PICKAXE, "§6§lHacker Pickaxe", 1,
                                Enchantment.DIG_SPEED, 10,
                                Enchantment.LOOT_BONUS_BLOCKS, 10,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                customItemsGui.setItem(4, createOpItem(Material.NETHERITE_AXE, "§6§lHacker Axe", 1,
                                Enchantment.DAMAGE_ALL, 20, Enchantment.DIG_SPEED, 10,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                // Hacker Armor
                ItemStack hackerHead = createOpItem(Material.PLAYER_HEAD, "§6§lHacker Helmet", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                                Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.OXYGEN, 5,
                                Enchantment.WATER_WORKER, 1,
                                Enchantment.MENDING, 1);

                if (hackerHead.getItemMeta() instanceof SkullMeta skullMeta) {
                        PlayerProfile profile = Bukkit
                                        .createPlayerProfile(UUID.fromString("f52d5857-28d3-4a22-8889-18bc94eca171"));
                        PlayerTextures textures = profile.getTextures();
                        try {
                                textures.setSkin(URI.create(
                                                "http://textures.minecraft.net/texture/201adbe5081aac9be0a4c6c5d383c51bd710b0a034e8ba8de348199c3ba901e6")
                                                .toURL());
                        } catch (MalformedURLException ignored) {
                        }
                        profile.setTextures(textures);
                        skullMeta.setOwnerProfile(profile);
                        hackerHead.setItemMeta(skullMeta);
                }
                customItemsGui.setItem(5, hackerHead);

                customItemsGui.setItem(6, createOpItem(Material.NETHERITE_CHESTPLATE, "§6§lHacker Chestplate", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                                Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.MENDING, 1));

                customItemsGui.setItem(7, createOpItem(Material.NETHERITE_LEGGINGS, "§6§lHacker Leggings", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                                Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.MENDING, 1));

                customItemsGui.setItem(8, createOpItem(Material.NETHERITE_BOOTS, "§6§lHacker Boots", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100,
                                Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.PROTECTION_FALL, 10,
                                Enchantment.DEPTH_STRIDER, 3,
                                Enchantment.MENDING, 1));

                customItemsGui.setItem(13, createOpItem(Material.ELYTRA, "§6§lHacker Elytra", 1,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                // 3. Consumables
                customItemsGui.setItem(18, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 64));
                customItemsGui.setItem(19, new ItemStack(Material.TOTEM_OF_UNDYING, 4));
                customItemsGui.setItem(20, new ItemStack(Material.FIREWORK_ROCKET, 64));

                // 4. Buffs
                customItemsGui.setItem(26, info(Material.POTION, "§a§lHacker Buffs", "§7Kliknij, aby otrzymać efekty",
                                "§7GodMode, Fly, Speed, Strength..."));

                // Return
                customItemsGui.setItem(22, info(Material.ARROW, "§cPowrót", "§7Wróć do głównego panelu"));
        }

        public static void applyHackerBuffs(Player p) {
                int d = 1200000;
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, d, 9));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, d, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, d, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, d, 4));

                p.setInvulnerable(true);
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setFoodLevel(20);
                if (p.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                }

                p.sendMessage("§a§l[!] §fOtrzymałeś §6Buffy Hackera§f!");
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }

        private static ItemStack createOpItem(Material material, String name, int amount, Object... enchants) {
                ItemStack item = new ItemStack(material, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                        if (name != null)
                                meta.setDisplayName(name);
                        for (int i = 0; i < enchants.length; i += 2) {
                                meta.addEnchant((Enchantment) enchants[i], (int) enchants[i + 1], true);
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
