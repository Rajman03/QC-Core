package pl.qc.core.commands.subcommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import pl.qc.core.QC;
import pl.qc.core.commands.SubCommand;

public class ReloadSubCommand implements SubCommand {

    private final QC plugin;

    public ReloadSubCommand(QC plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Przeładowuje konfigurację pluginu QC-Core";
    }

    @Override
    public String getUsage() {
        return "/qc reload";
    }

    @Override
    public String getPermission() {
        return "qc.reload";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        plugin.reloadPlugin();
        sender.sendMessage("§8[QC-Core] §aKonfiguracja została przeładowana.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}
