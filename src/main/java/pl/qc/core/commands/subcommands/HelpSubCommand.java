package pl.qc.core.commands.subcommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import pl.qc.core.commands.SubCommand;

public class HelpSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Wyświetla listę dostępnych komend";
    }

    @Override
    public String getUsage() {
        return "/qc help";
    }

    @Override
    public String getPermission() {
        return "qc.status";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("h");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // Help is handled by QCCommand.sendHelp()
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}
