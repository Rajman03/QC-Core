package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class TeleportSubCommand implements SubCommand {

    public TeleportSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleportuje do innego gracza lub teleportuje gracza do drugiego gracza";
    }

    @Override
    public String getUsage() {
        return "/qc tp <do> LUB /qc tp <od> <do>";
    }

    @Override
    public String getPermission() {
        return "qc.teleport";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("tp");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <do> LUB /" + label + " " + args[0] + " <od> <do>");
            return true;
        }

        if (args.length == 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cKonsola musi podać obu graczy: /" + label + " " + args[0] + " <od> <do>");
                return true;
            }
            Player targetTo = Bukkit.getPlayer(args[1]);
            if (targetTo == null) {
                sender.sendMessage("§cGracz §e" + args[1] + " §cnie jest online.");
                return true;
            }
            player.teleport(targetTo.getLocation());
            player.sendMessage("§8[QC-Core] §7Przeteleportowano do gracza §a" + targetTo.getName());
            return true;
        }

        Player targetFrom = Bukkit.getPlayer(args[1]);
        Player targetTo = Bukkit.getPlayer(args[2]);
        if (targetFrom == null || targetTo == null) {
            sender.sendMessage("§cJeden z graczy jest offline.");
            return true;
        }
        targetFrom.teleport(targetTo.getLocation());
        sender.sendMessage("§8[QC-Core] §7Przeteleportowano §e" + targetFrom.getName() + " §7do §a" + targetTo.getName());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2 || args.length == 3) {
            String current = args[args.length - 1].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(current)) {
                    list.add(p.getName());
                }
            }
        }
        return list;
    }
}
