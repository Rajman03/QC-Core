package pl.qc.core.commands.subcommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class DragonSubCommand implements SubCommand {

    public DragonSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "dragon";
    }

    @Override
    public String getDescription() {
        return "Zmusza Ender Dragona do wylądowania przy portalu";
    }

    @Override
    public String getUsage() {
        return "/qc dragon";
    }

    @Override
    public String getPermission() {
        return "qc.dragon";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        World world = player.getWorld();
        boolean found = false;
        for (EnderDragon dragon : world.getEntitiesByClass(EnderDragon.class)) {
            dragon.setPhase(EnderDragon.Phase.LAND_ON_PORTAL);
            found = true;
        }

        if (found) {
            sender.sendMessage("§8[QC-Core] §aZmuszono Ender Dragona do wylądowania na portalu!");
        } else {
            sender.sendMessage("§cW tym świecie nie znaleziono Ender Dragona.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}
