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

public class VanishSubCommand implements SubCommand {

    private final QC plugin;

    public VanishSubCommand(QC plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "vanish";
    }

    @Override
    public String getDescription() {
        return "Przełącza tryb vanish (niewidzialności) dla siebie lub wybranego gracza";
    }

    @Override
    public String getUsage() {
        return "/qc vanish [gracz]";
    }

    @Override
    public String getPermission() {
        return "qc.vanish";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("v");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cGracz §e" + args[1] + " §cjest offline.");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cPodaj nazwę gracza: /" + label + " v <gracz>");
                return true;
            }
            target = (Player) sender;
        }

        boolean vanished = plugin.getPlayerStateManager().toggleVanish(target);
        String statusStr = vanished ? "§awłączony" : "§cwyłączony";
        target.sendMessage("§8[QC-Core] §7Vanish został " + statusStr + "§7.");
        if (!target.equals(sender)) {
            sender.sendMessage("§8[QC-Core] §7Vanish dla §e" + target.getName() + " §7został " + statusStr + "§7.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
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
        return Collections.emptyList();
    }
}
