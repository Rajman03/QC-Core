package pl.qc.core.hack;

import java.util.*;

public class PlayerTracker {
    public final Set<UUID> noAdvancements = new HashSet<>();
    public final Set<UUID> reach = new HashSet<>();
    public final Set<UUID> noTarget = new HashSet<>();
    public final Map<UUID, Double> damageDealtMult = new HashMap<>();
    public final Map<UUID, Double> damageReceivedMult = new HashMap<>();
    public final Set<UUID> forcefield = new HashSet<>();
    public static final Set<UUID> kicked = new HashSet<>();

    public void toggle(UUID id, Set<UUID> set, String name, org.bukkit.command.CommandSender s, String playerName) {
        if (set.contains(id)) {
            set.remove(id);
            s.sendMessage("§7" + name + ": §cOFF (" + playerName + ")");
        } else {
            set.add(id);
            s.sendMessage("§7" + name + ": §aON (" + playerName + ")");
        }
    }

    public void clear() {
        noAdvancements.clear();
        reach.clear();
        noTarget.clear();
        damageDealtMult.clear();
        damageReceivedMult.clear();
    }
}
