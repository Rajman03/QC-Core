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

public class KickSubCommand implements SubCommand {

    public KickSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Wyrzuca gracza z serwera z pustym powodem";
    }

    @Override
    public String getUsage() {
        return "/qc k <gracz> LUB /qc kick <gracz>";
    }

    @Override
    public String getPermission() {
        return "qc.kick";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("k");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <gracz>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cGracz §e" + args[1] + " §cnie jest online.");
            return true;
        }

        target.kickPlayer("");
        sender.sendMessage("§8[QC-Core] §7Wyrzucono gracza §e" + target.getName() + " §7z pustym powodem.");
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
