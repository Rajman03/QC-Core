package pl.qc.core.commands.subcommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class AttackSpeedSubCommand implements SubCommand {

    public AttackSpeedSubCommand(QC plugin) {
    }

    @Override
    public String getName() {
        return "attackspeed";
    }

    @Override
    public String getDescription() {
        return "Zwiększa prędkość ataku o 30000 jednostek";
    }

    @Override
    public String getUsage() {
        return "/qc attackspeed";
    }

    @Override
    public String getPermission() {
        return "qc.attackspeed";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("a");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attr != null) {
            attr.setBaseValue(attr.getBaseValue() + 30000.0);
            sender.sendMessage("§8[QC-Core] §7Zwiększono attack speed o §e30000§7. (Aktualnie: §e" + attr.getBaseValue() + "§7)");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}
