package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class GiveSubCommand implements SubCommand {

    public GiveSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Daje przedmiot graczowi";
    }

    @Override
    public String getUsage() {
        return "/qc g <material> [ilość] LUB /qc give <material> [ilość]";
    }

    @Override
    public String getPermission() {
        return "qc.give";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("g");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <material> [ilość]");
            return true;
        }

        Material mat = Material.matchMaterial(args[1].toUpperCase(Locale.ROOT));
        if (mat == null) {
            sender.sendMessage("§cNiepoprawna nazwa materiału: §e" + args[1]);
            return true;
        }

        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Math.max(1, Math.min(6400, Integer.parseInt(args[2])));
            } catch (NumberFormatException ignored) {
            }
        }

        player.getInventory().addItem(new ItemStack(mat, amount));
        player.sendMessage("§8[QC-Core] §7Dodano do ekwipunku: §a" + mat.name() + " x" + amount);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            String current = args[1].toUpperCase(Locale.ROOT);
            for (Material mat : Material.values()) {
                if (mat.name().startsWith(current)) {
                    list.add(mat.name().toLowerCase(Locale.ROOT));
                }
                if (list.size() > 50) {
                    break; // Cap to keep packet sizes small and optimized
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
