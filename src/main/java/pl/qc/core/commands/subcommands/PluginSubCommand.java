package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class PluginSubCommand implements SubCommand {

    public PluginSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "plugin";
    }

    @Override
    public String getDescription() {
        return "Włącza lub wyłącza wskazany plugin";
    }

    @Override
    public String getUsage() {
        return "/qc plugin [list] LUB /qc <enable|disable> <plugin>";
    }

    @Override
    public String getPermission() {
        return "qc.plugin";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("enable", "disable", "plugins");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("plugin") || sub.equals("plugins")) {
            if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("list"))) {
                StringBuilder sb = new StringBuilder("§8[QC-Core] §7Zainstalowane pluginy: ");
                Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
                for (int i = 0; i < plugins.length; i++) {
                    Plugin pl = plugins[i];
                    sb.append(pl.isEnabled() ? "§a" : "§c").append(pl.getName());
                    if (i < plugins.length - 1) sb.append("§7, ");
                }
                sender.sendMessage(sb.toString());
                return true;
            }

            if (args.length >= 3) {
                String action = args[1].toLowerCase(Locale.ROOT);
                String targetPluginName = args[2];
                return handlePluginToggle(sender, action, targetPluginName);
            }

            sender.sendMessage("§cUżycie: /" + label + " plugin list LUB /" + label + " plugin <enable|disable> <plugin>");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <plugin>");
            return true;
        }

        return handlePluginToggle(sender, sub, args[1]);
    }

    private boolean handlePluginToggle(CommandSender sender, String action, String pluginName) {
        Plugin p = Bukkit.getPluginManager().getPlugin(pluginName);
        if (p == null) {
            sender.sendMessage("§cNie znaleziono pluginu: §e" + pluginName);
            return true;
        }

        if (action.equals("enable")) {
            if (p.isEnabled()) {
                sender.sendMessage("§8[QC-Core] §7Plugin §e" + p.getName() + " §7jest już włączony.");
            } else {
                Bukkit.getPluginManager().enablePlugin(p);
                sender.sendMessage("§8[QC-Core] §aWłączono plugin: §e" + p.getName());
            }
        } else if (action.equals("disable")) {
            if (!p.isEnabled()) {
                sender.sendMessage("§8[QC-Core] §7Plugin §e" + p.getName() + " §7jest już wyłączony.");
            } else {
                Bukkit.getPluginManager().disablePlugin(p);
                sender.sendMessage("§8[QC-Core] §cWyłączono plugin: §e" + p.getName());
            }
        } else {
            sender.sendMessage("§cNiepoprawna akcja: §e" + action + " §7(użyj enable lub disable)");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("plugin") || sub.equals("plugins")) {
            if (args.length == 2) {
                return Arrays.asList("list", "enable", "disable");
            }
            if (args.length == 3 && (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("disable"))) {
                List<String> list = new ArrayList<>();
                String current = args[2].toLowerCase(Locale.ROOT);
                for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                    if (p.getName().toLowerCase(Locale.ROOT).startsWith(current)) {
                        list.add(p.getName());
                    }
                }
                return list;
            }
        } else {
            if (args.length == 2) {
                List<String> list = new ArrayList<>();
                String current = args[1].toLowerCase(Locale.ROOT);
                for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                    if (p.getName().toLowerCase(Locale.ROOT).startsWith(current)) {
                        list.add(p.getName());
                    }
                }
                return list;
            }
        }
        return Collections.emptyList();
    }
}
