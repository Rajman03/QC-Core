package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class ReachSubCommand implements SubCommand {

    public ReachSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "reach";
    }

    @Override
    public String getDescription() {
        return "Przełącza zasięg (reach) dla wybranego gracza lub siebie";
    }

    @Override
    public String getUsage() {
        return "/qc r [gracz] LUB /qc reach [gracz]";
    }

    @Override
    public String getPermission() {
        return "qc.reach";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("r");
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
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§cUżycie: /" + label + " r <gracz>");
                return true;
            }
            target = p;
        }

        AttributeInstance attr = target.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        if (attr != null) {
            double current = attr.getBaseValue();
            double targetVal = (current > 3.0) ? 3.0 : 6.0;
            attr.setBaseValue(targetVal);
            sender.sendMessage("§8[QC-Core] §7Ustawiono reach dla §e" + target.getName() + " §7na §a" + targetVal);
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
