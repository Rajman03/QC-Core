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

public class IpSubCommand implements SubCommand {

    public IpSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "ip";
    }

    @Override
    public String getDescription() {
        return "Pokazuje adres IP wybranego gracza";
    }

    @Override
    public String getUsage() {
        return "/qc ip <gracz>";
    }

    @Override
    public String getPermission() {
        return "qc.ip";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " ip <gracz>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || target.getAddress() == null) {
            sender.sendMessage("§cGracz §e" + args[1] + " §cnie jest online.");
            return true;
        }

        String ip = (target.getAddress().getAddress() != null)
                ? target.getAddress().getAddress().getHostAddress()
                : target.getAddress().getHostString();
        sender.sendMessage("§8[QC-Core] §7Adres IP gracza §e" + target.getName() + "§7: §a" + ip);
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
