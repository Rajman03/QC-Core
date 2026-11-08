package pl.qc.core.commands.subcommands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class DamageMultiplierSubCommand implements SubCommand {

    private final QC plugin;

    public DamageMultiplierSubCommand(QC plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "damagemultiplier";
    }

    @Override
    public String getDescription() {
        return "Modyfikuje mnożnik zadawanych (ma) lub otrzymywanych (mb) obrażeń";
    }

    @Override
    public String getUsage() {
        return "/qc <ma|mb> <mnożnik> LUB /qc damagemultiplier <attack|defense|ma|mb> <mnożnik>";
    }

    @Override
    public String getPermission() {
        return "qc.damagemultiplier";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ma", "mb", "dm");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        boolean isAttack;
        String valStr;

        if (sub.equals("ma")) {
            if (args.length < 2) {
                sender.sendMessage("§cUżycie: /" + label + " ma <liczba>");
                return true;
            }
            isAttack = true;
            valStr = args[1];
        } else if (sub.equals("mb")) {
            if (args.length < 2) {
                sender.sendMessage("§cUżycie: /" + label + " mb <liczba>");
                return true;
            }
            isAttack = false;
            valStr = args[1];
        } else {
            if (args.length < 3) {
                sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <attack|defense|ma|mb> <liczba>");
                return true;
            }
            String type = args[1].toLowerCase(Locale.ROOT);
            if (type.equals("attack") || type.equals("ma")) {
                isAttack = true;
            } else if (type.equals("defense") || type.equals("mb")) {
                isAttack = false;
            } else {
                sender.sendMessage("§cNiepoprawny typ. Użyj: attack, defense, ma, mb");
                return true;
            }
            valStr = args[2];
        }

        try {
            double val = Double.parseDouble(valStr);
            if (isAttack) {
                plugin.getPlayerStateManager().setAttackMultiplier(player, val);
                sender.sendMessage("§8[QC-Core] §7Ustawiono mnożnik zadawanych obrażeń na §a" + val);
            } else {
                plugin.getPlayerStateManager().setDefenseMultiplier(player, val);
                sender.sendMessage("§8[QC-Core] §7Ustawiono mnożnik otrzymywanych obrażeń na §a" + val);
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cNiepoprawna liczba: §e" + valStr);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("ma") || sub.equals("mb")) {
            if (args.length == 2) {
                return Arrays.asList("1.0", "1.5", "2.0", "3.0", "0.5", "0.0");
            }
        } else {
            if (args.length == 2) {
                return Arrays.asList("attack", "defense", "ma", "mb");
            } else if (args.length == 3) {
                return Arrays.asList("1.0", "1.5", "2.0", "3.0", "0.5", "0.0");
            }
        }
        return Collections.emptyList();
    }
}
