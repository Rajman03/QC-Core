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

public class EnderChestSubCommand implements SubCommand {

    public EnderChestSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "enderchest";
    }

    @Override
    public String getDescription() {
        return "Otwiera skrzynię kresu (EnderChest) wskazanego gracza";
    }

    @Override
    public String getUsage() {
        return "/qc ec <gracz> LUB /qc enderchest <gracz>";
    }

    @Override
    public String getPermission() {
        return "qc.inventory";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ec");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <gracz>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cGracz §e" + args[1] + " §cnie jest online.");
            return true;
        }

        player.openInventory(target.getEnderChest());
        player.sendMessage("§8[QC-Core] §7Otwarto skrzynię kresu gracza §a" + target.getName());
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
