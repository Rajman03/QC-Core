package pl.qc.core.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class GameModeSubCommand implements SubCommand {

    public GameModeSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "gamemode";
    }

    @Override
    public String getDescription() {
        return "Ustawia tryb gry (creative/survival/spectator/adventure)";
    }

    @Override
    public String getUsage() {
        return "/qc <gms|gmc|gmsp|gma> LUB /qc gamemode <tryb>";
    }

    @Override
    public String getPermission() {
        return "qc.gamemode";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("gms", "gmc", "gmsp", "gma");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        GameMode mode = null;

        if (sub.equals("gms")) {
            mode = GameMode.SURVIVAL;
        } else if (sub.equals("gmc")) {
            mode = GameMode.CREATIVE;
        } else if (sub.equals("gmsp")) {
            mode = GameMode.SPECTATOR;
        } else if (sub.equals("gma")) {
            mode = GameMode.ADVENTURE;
        } else if (args.length > 1) {
            String targetMode = args[1].toLowerCase(Locale.ROOT);
            mode = switch (targetMode) {
                case "survival", "0", "s" -> GameMode.SURVIVAL;
                case "creative", "1", "c" -> GameMode.CREATIVE;
                case "adventure", "2", "a" -> GameMode.ADVENTURE;
                case "spectator", "3", "sp" -> GameMode.SPECTATOR;
                default -> null;
            };
        }

        if (mode == null) {
            sender.sendMessage("§cNiepoprawny tryb gry. Użyj: /" + label + " <gms|gmc|gmsp|gma> lub /" + label + " gamemode <tryb>");
            return true;
        }

        player.setGameMode(mode);
        player.sendMessage("§8[QC-Core] §7Zmieniono tryb gry na: §a" + mode.name());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("gamemode")) {
            List<String> modes = Arrays.asList("survival", "creative", "adventure", "spectator");
            List<String> list = new ArrayList<>();
            String current = args[1].toLowerCase(Locale.ROOT);
            for (String m : modes) {
                if (m.startsWith(current)) {
                    list.add(m);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
