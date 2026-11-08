package pl.qc.core.commands.subcommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class StatusSubCommand implements SubCommand {

    private final QC plugin;

    public StatusSubCommand(QC plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Wyświetla aktualny status pluginu QC-Core";
    }

    @Override
    public String getUsage() {
        return "/qc status";
    }

    @Override
    public String getPermission() {
        return "qc.status";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§8[QC-Core] §7Status pluginu:");
        sender.sendMessage(" §aWebhook: §f" + (plugin.getConfigManager().isWebhookEnabled() ? "włączony" : "wyłączony"));
        sender.sendMessage(" §aFallback admina: §f" + plugin.getConfigManager().getAdminFallback());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}
