package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class EffectSubCommand implements SubCommand {

    private static final List<String> EFFECTS = Arrays.asList(
        "c", "sat", "str", "res", "has", "fir", "reg", "abs", "spe", "wat", "pvp"
    );

    public EffectSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "effect";
    }

    @Override
    public String getDescription() {
        return "Nakłada wybrane efekty mikstur";
    }

    @Override
    public String getUsage() {
        return "/qc e <efekt> LUB /qc effect <efekt>";
    }

    @Override
    public String getPermission() {
        return "qc.effect";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("e");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUżycie: /" + label + " " + args[0] + " <c|sat|str|res|has|fir|reg|abs|spe|wat|pvp>");
            return true;
        }

        String eff = args[1].toLowerCase(Locale.ROOT);
        if (eff.equals("c")) {
            List<PotionEffectType> toRemove = player.getActivePotionEffects().stream()
                    .map(pe -> pe.getType())
                    .collect(Collectors.toList());
            for (PotionEffectType type : toRemove) {
                player.removePotionEffect(type);
            }
            player.sendMessage("§8[QC-Core] §7Usunięto wszystkie efekty.");
            return true;
        }

        int sec5 = 5 * 60 * 20;
        int sec10 = 10 * 60 * 20;

        switch (eff) {
            case "sat" -> player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, sec5, 0));
            case "str" -> player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, sec5, 1));
            case "res" -> player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, sec5, 1));
            case "has" -> player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, sec5, 1));
            case "fir" -> player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, sec5, 0));
            case "reg" -> player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, sec5, 1));
            case "abs" -> player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, sec5, 1));
            case "spe" -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, sec5, 1));
            case "wat" -> player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, sec5, 0));
            case "pvp" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, sec10, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, sec10, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, sec10, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, sec10, 1));
            }
            default -> {
                player.sendMessage("§cNieznany efekt. Użyj: c, sat, str, res, has, fir, reg, abs, spe, wat, pvp");
                return true;
            }
        }
        player.sendMessage("§8[QC-Core] §aNałożono efekt(y).");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            String current = args[1].toLowerCase(Locale.ROOT);
            for (String eff : EFFECTS) {
                if (eff.startsWith(current)) {
                    list.add(eff);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
