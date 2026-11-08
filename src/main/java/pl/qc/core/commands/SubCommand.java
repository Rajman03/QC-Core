package pl.qc.core.commands;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface SubCommand {

    String getName();

    String getDescription();

    String getUsage();

    String getPermission();

    List<String> getAliases();

    boolean execute(CommandSender sender, String label, String[] args);

    List<String> tabComplete(CommandSender sender, String label, String[] args);
}
