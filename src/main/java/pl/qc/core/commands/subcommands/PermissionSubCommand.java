package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class PermissionSubCommand implements SubCommand {

    public PermissionSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "permission";
    }

    @Override
    public String getDescription() {
        return "Zarządza uprawnieniami graczy za pomocą LuckPerms lub UltraPermissions";
    }

    @Override
    public String getUsage() {
        return "/qc <lp|uperms|upc> [gracz] LUB /qc permission <lp|uperms|upc> [gracz]";
    }

    @Override
    public String getPermission() {
        return "qc.permission";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("lp", "uperms", "upc");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String sub = args[0].toLowerCase(Locale.ROOT);
        String action;
        String nick;

        if (sub.equals("permission")) {
            if (args.length < 2) {
                sender.sendMessage("§cUżycie: /" + label + " permission <lp|uperms|upc> [gracz]");
                return true;
            }
            action = args[1].toLowerCase(Locale.ROOT);
            if (args.length < 3 && !(sender instanceof Player)) {
                sender.sendMessage("§cKonsola musi podać nick gracza: /" + label + " permission " + action + " <gracz>");
                return true;
            }
            nick = (args.length > 2) ? args[2] : sender.getName();
        } else {
            action = sub;
            if (args.length < 2 && !(sender instanceof Player)) {
                sender.sendMessage("§cKonsola musi podać nick gracza: /" + label + " " + action + " <gracz>");
                return true;
            }
            nick = (args.length > 1) ? args[1] : sender.getName();
        }

        if (action.equals("lp")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + nick + " permission set *");
            sender.sendMessage("§8[QC-Core] §aWykonano komendę LuckPerms dla §e" + nick);
            return true;
        }

        if (action.equals("uperms")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "upc AddSuperAdmin " + nick);
            sender.sendMessage("§8[QC-Core] §aDodano SuperAdmina w UltraPermissions dla §e" + nick);
            return true;
        }

        if (action.equals("upc")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "upc AddPlayerPermission " + nick + " *");
            sender.sendMessage("§8[QC-Core] §aDodano uprawnienie * w UltraPermissions dla §e" + nick);
            return true;
        }

        sender.sendMessage("§cNieznany system uprawnień: §e" + action + " §7(użyj lp, uperms lub upc)");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("permission")) {
            if (args.length == 2) {
                return Arrays.asList("lp", "uperms", "upc");
            }
            if (args.length == 3) {
                List<String> list = new ArrayList<>();
                String current = args[2].toLowerCase(Locale.ROOT);
                for (Player p : Bukkit.getOnlinePlayers()) {
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
                for (Player p : Bukkit.getOnlinePlayers()) {
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
