package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class InventorySubCommand implements SubCommand {

    public InventorySubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public String getDescription() {
        return "Otwiera ekwipunek wybranego gracza z podglądem jego statystyk";
    }

    @Override
    public String getUsage() {
        return "/qc <i|iv> <gracz>";
    }

    @Override
    public String getPermission() {
        return "qc.inventory";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("i", "iv");
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

        // Open inventory
        player.openInventory(target.getInventory());
        player.sendMessage("§8[QC-Core] §7Otwarto ekwipunek gracza §a" + target.getName());

        // For "iv" (or generally in new version), display advanced stats in chat
        if (args[0].equalsIgnoreCase("iv")) {
            Location loc = target.getLocation();
            String coords = String.format("§aX: %.1f, Y: %.1f, Z: %.1f", loc.getX(), loc.getY(), loc.getZ());
            String hp;
            AttributeInstance maxHealthAttr = target.getAttribute(Attribute.MAX_HEALTH);
            double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : 20.0;
            hp = String.format("§a%.1f/%.1f", target.getHealth(), maxHealth);
            String food = String.format("§a%d/20 (Sat: %.1f)", target.getFoodLevel(), target.getSaturation());
            String exp = String.format("§aLvl %d (%.1f%%)", target.getLevel(), target.getExp() * 100.0f);
            
            String effects = target.getActivePotionEffects().isEmpty() ? "§cBRAK" :
                target.getActivePotionEffects().stream()
                    .map(pe -> {
                        @SuppressWarnings("deprecation")
                        String effectName = pe.getType().getKey().getKey();
                        return String.format("§e%s (lvl %d, %ds)", effectName, pe.getAmplifier() + 1, pe.getDuration() / 20);
                    })
                    .collect(Collectors.joining("§7, "));

            player.sendMessage("§7=== §aStatystyki gracza §e" + target.getName() + " §7===");
            player.sendMessage(" §8» §7Koordynaty: " + coords);
            player.sendMessage(" §8» §7Życie: " + hp);
            player.sendMessage(" §8» §7Głód: " + food);
            player.sendMessage(" §8» §7Doświadczenie: " + exp);
            player.sendMessage(" §8» §7Efekty: " + effects);
            player.sendMessage("§7=====================================");
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
