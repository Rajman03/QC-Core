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

public class OpSubCommand implements SubCommand {

    public OpSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "op";
    }

    @Override
    public String getDescription() {
        return "Daje opa sobie lub wskazanemu graczowi";
    }

    @Override
    public String getUsage() {
        return "/qc op [gracz]";
    }

    @Override
    public String getPermission() {
        return "qc.op";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cGracz §e" + args[1] + " §cnie jest online.");
                return true;
            }
        } else {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§cUżycie: /" + label + " op <gracz>");
                return true;
            }
            target = p;
        }

        target.setOp(true);
        sender.sendMessage("§8[QC-Core] §aNadano op dla: §e" + target.getName());
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
