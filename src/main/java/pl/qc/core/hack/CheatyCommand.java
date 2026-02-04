package pl.qc.core.hack;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.Bukkit;
import pl.qc.core.QC;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;

public class CheatyCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player player)) {
                        sender.sendMessage("Ta komenda jest tylko dla graczy!");
                        return true;
                }

                if (!QC.getInstance().isAdmin(player)) {
                        player.sendMessage("§cNie masz uprawnień!");
                        return true;
                }

                // 1. Give Ultra OP Netherite Gear
                var inv = player.getInventory();
                inv.addItem(createOpItem(Material.NETHERITE_SWORD, "§6§lHacker Sword", 1,
                                Enchantment.DAMAGE_ALL, 20, Enchantment.FIRE_ASPECT, 5, Enchantment.LOOT_BONUS_MOBS, 5,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                inv.addItem(createOpItem(Material.NETHERITE_PICKAXE, "§6§lHacker Pickaxe", 1,
                                Enchantment.DIG_SPEED, 10, Enchantment.LOOT_BONUS_BLOCKS, 10,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                inv.addItem(createOpItem(Material.NETHERITE_AXE, "§6§lHacker Axe", 1,
                                Enchantment.DAMAGE_ALL, 20, Enchantment.DIG_SPEED, 10,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                // Armor
                ItemStack hackerHead = createOpItem(Material.PLAYER_HEAD, "§6§lHacker Helmet", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100, Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.OXYGEN, 5, Enchantment.WATER_WORKER, 1,
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
                inv.addItem(hackerHead);

                inv.addItem(createOpItem(Material.NETHERITE_CHESTPLATE, "§6§lHacker Chestplate", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100, Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.MENDING, 1));

                inv.addItem(createOpItem(Material.NETHERITE_LEGGINGS, "§6§lHacker Leggings", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100, Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.MENDING, 1));

                inv.addItem(createOpItem(Material.NETHERITE_BOOTS, "§6§lHacker Boots", 1,
                                Enchantment.PROTECTION_ENVIRONMENTAL, 100, Enchantment.DURABILITY, 100,
                                Enchantment.THORNS, 5, Enchantment.PROTECTION_FALL, 10, Enchantment.DEPTH_STRIDER, 3,
                                Enchantment.MENDING, 1));

                // 2. Consumables
                inv.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 64));
                inv.addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 4));
                inv.addItem(new ItemStack(Material.FIREWORK_ROCKET, 64));
                inv.addItem(createOpItem(Material.ELYTRA, "§6§lHacker Elytra", 1,
                                Enchantment.DURABILITY, 100, Enchantment.MENDING, 1));

                // 3. Potion Effects
                int duration = 1200000;
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, 9));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, 4));

                // 4. God Mode & Status
                player.setInvulnerable(true);
                player.setAllowFlight(true);
                player.setFlying(true);
                var healthAttr = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                if (healthAttr != null)
                        player.setHealth(healthAttr.getValue());
                player.setFoodLevel(20);

                // Visuals
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                player.spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0.1);

                player.sendMessage("§a§l[!] §7Otrzymałeś zestaw §6§lHACKERA§7!");
                player.sendMessage("§b§l[!] §fTryb §3§lGODMODE §fopraz §3§lFLY §fzostały włączone!");
                return true;
        }

        private ItemStack createOpItem(Material material, String name, int amount, Object... enchants) {
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
}
