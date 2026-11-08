package pl.qc.core.commands.subcommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class NoTargetSubCommand implements SubCommand {

    private final QC plugin;

    public NoTargetSubCommand(QC plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "notarget";
    }

    @Override
    public String getDescription() {
        return "Przełącza ignorowanie gracza przez moby";
    }

    @Override
    public String getUsage() {
        return "/qc t LUB /qc notarget";
    }

    @Override
    public String getPermission() {
        return "qc.notarget";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("t");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy.");
            return true;
        }

        boolean noTarget = plugin.getPlayerStateManager().toggleNoTarget(player);
        sender.sendMessage("§8[QC-Core] §7Targetowanie przez moby zostało: " + (noTarget ? "§cwyłączone" : "§awłączone"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}
