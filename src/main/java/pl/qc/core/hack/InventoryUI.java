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

        // GUI Titles - Constants
        public static final String TITLE_CONTROL_PANEL = "§0§lQC-Core Panel";
        public static final String TITLE_CUSTOM_ITEMS = "§4§lQC Custom Items";
        public static final String TITLE_PLAYER_LIST = "§8Lista Graczy";
        public static final String TITLE_SECRET_STASH = "§0Tajny Schowek";
        public static final String TITLE_PREVIEW_PREFIX = "§0Podgląd: ";
        public static final String TITLE_OPTIONS_PREFIX = "§8Opcje: ";

        // --- Main Control Panel ---

        public static void openControlPanel(Player p) {
                Inventory gui = Bukkit.createInventory(null, 54, TITLE_CONTROL_PANEL);

                // --- Row 2: Server Management ---
                gui.setItem(10, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQyODRkOGEyMWEyODg5Y2IyOGFiN2RkOWE2NDI4ZDE5ZDUyMWRhNDljZDhiM2I3ZGEzNzY1NmY5NjVlMiJ9fX0=",
                                "§c§lReload Config", "§7Przeładuj konfigurację pluginu"));

                gui.setItem(11, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI0MzE5MTFmNDE3OGI0ZDJiNDEzYWE3ZjVjNzhhNTQ0ZmU5NmU5MzI1MTU5NjA0ODQ4ZDM5MzkyN2QzIn19fQ==",
                                "§4§lPanic Mode", "§7Włącz/Wyłącz tryb paniki"));

                gui.setItem(12, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFhNmRlNDQ1NjMzOTgwM2Q1M2MwZDA1NjIzN2I0MTQ2NTU2ZDBkYzE5YmFiM2NlODVhNjkyMDk2ZTEzZCJ9fX0=",
                                "§6§lPlugin Manager", "§7Zarządzaj pluginami"));

                gui.setItem(16, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkMzNiODU5NjMzNjUifX19",
                                "§c§lConsole Command", "§7Wykonaj komendę", "§7jako konsola"));

                // --- Row 3: Player & Troll ---
                gui.setItem(19, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTIwMzM5MDEwZDMyN2Q1MzJhN2FkNTgzODQ1YzcifX19",
                                "§e§lLista Graczy", "§7Zarządzaj graczami online", "§7(Kick, Ban, Troll)"));

                gui.setItem(20, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjE3ZTIwN2M5ZDYwNGY3MTJhM2M4MmM5ODc5OGEzODIwMzdlZDU4NDc0Yzc5ZTczMTdlYmViM2U0YzFmOCJ9fX0=",
                                "§b§lTeleportacja", "§7Menu teleportacji"));

                gui.setItem(21, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmEzMmE3ZDFmYjZlMjZhOTk2ZDJiNTM5MDBhOTE0ZTYxMiUzNjZmNTk3YzU4ZDAzN2ZlMjczMTU1ZjcifX19",
                                "§5§lTajny Schowek", "§7Otwórz zdalny ekwipunek"));

                gui.setItem(25, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM5NmJlNzg4NmViN2RmNzU1MjRkNTBlNjIxMjRkZGYzZmFjOTQ2M2QzZjZhMzQ1N2I3YmEyODM5MzYwIn19fQ==",
                                "§4§lHack Items", "§7Odbierz niszczycielskie przedmioty"));

                // --- Row 4: Effects & Utils ---
                gui.setItem(28, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2M1Y2EwOGYyZTE4Y2Q4YzQ4OGRkZmMzMjc2ZGU0MzFjY2ZiMzAzNmQzZGVmNTJjYmMzZDI4MzNlZGM4ZSJ9fX0=",
                                "§a§lEfekty", "§7Nadaj sobie efekty", "§7mikstur"));

                gui.setItem(29, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU4MzgwYjRhM2UyYjFhN2FhM2E5OGJiOTE2ODVlMzFiM2IzNzY4Y2I2NTQxNGFkYmU4Mz IyYjQ3In19fQ==",
                                "§6§lBuffy", "§7Szybkie zestawy buffów"));

                gui.setItem(34, getCustomHead(
                                "e3RleHR1cmVzhjp7eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MjY2YjZmNzFjZTQ3Y2E0ZC5lMmMyM2I2ZGMyIn19fQ==",
                                "§c§lReset All", "§7Wyczyść bazy danych", "§7(Ostatnia deska ratunku)"));

                // Wypełnienie tła
                ItemStack glass = info(Material.BLACK_STAINED_GLASS_PANE, " ");
                for (int i = 0; i < 54; i++) {
                        if (gui.getItem(i) == null)
                                gui.setItem(i, glass);
                }

                p.openInventory(gui);
        }

        private static ItemStack getCustomHead(String b64, String name, String... lore) {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                if (meta != null) {
                        meta.setDisplayName(name);
                        meta.setLore(Arrays.asList(lore));

                        try {
                                String decoded = new String(java.util.Base64.getDecoder().decode(b64));
                                String url = decoded.substring(decoded.indexOf("https://"), decoded.indexOf("\"}}}"));

                                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                                PlayerTextures textures = profile.getTextures();
                                textures.setSkin(URI.create(url).toURL());
                                profile.setTextures(textures);
                                meta.setOwnerProfile(profile);
                        } catch (Exception e) {
                                // Fallback to Steve
                        }
                        item.setItemMeta(meta);
                }
                return item;
        }

        // --- Player Selector GUI ---

        public static void openPlayerSelector(Player p) {
                int size = 54;
                Inventory gui = Bukkit.createInventory(null, size, TITLE_PLAYER_LIST);

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
                Inventory gui = Bukkit.createInventory(null, 27, TITLE_OPTIONS_PREFIX + target.getName());
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

                gui.setItem(12, info(Material.CHEST, "§eZapisz Eq", "§7Utwórz kopię zapasową ekwipunku"));
                gui.setItem(14, info(Material.ENDER_CHEST, "§bWczytaj Eq", "§7Przywróć kopię zapasową"));

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
                Inventory gui = Bukkit.createInventory(null, 54, TITLE_PREVIEW_PREFIX + target.getName());

                // Main inventory
                for (int i = 0; i < 36; i++)
                        gui.setItem(i, target.getInventory().getItem(i));

                // Separator
                ItemStack glass = info(Material.BLACK_STAINED_GLASS_PANE, " ");
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
                customItemsGui = Bukkit.createInventory(null, 27, TITLE_CUSTOM_ITEMS);

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
