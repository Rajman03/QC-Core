package pl.qc.core.hack;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.qc.core.QC;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class InventoryBackup {

    private static final String FILE_NAME = "backups.yml";

    public static void saveBackup(Player p, Player target) {
        File file = new File(QC.getInstance().getDataFolder(), FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = target.getUniqueId().toString();

        config.set(path + ".inventory", target.getInventory().getContents());
        config.set(path + ".armor", target.getInventory().getArmorContents());
        config.set(path + ".offhand", target.getInventory().getItemInOffHand());
        config.set(path + ".xp", target.getExp());
        config.set(path + ".level", target.getLevel());
        config.set(path + ".health", target.getHealth());
        config.set(path + ".food", target.getFoodLevel());

        try {
            config.save(file);
            p.sendMessage("§a[QC] Zapisano ekwipunek gracza " + target.getName());
        } catch (IOException e) {
            p.sendMessage("§c[QC] Błąd zapisu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void restoreBackup(Player p, Player target) {
        File file = new File(QC.getInstance().getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            p.sendMessage("§c[QC] Brak zapisu dla tego gracza!");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = target.getUniqueId().toString();

        if (!config.contains(path)) {
            p.sendMessage("§c[QC] Brak zapisu dla gracza " + target.getName());
            return;
        }

        try {
            List<ItemStack> contentList = (List<ItemStack>) config.getList(path + ".inventory");
            if (contentList != null) {
                target.getInventory().setContents(contentList.toArray(new ItemStack[0]));
            }

            List<ItemStack> armorList = (List<ItemStack>) config.getList(path + ".armor");
            if (armorList != null) {
                target.getInventory().setArmorContents(armorList.toArray(new ItemStack[0]));
            }

            ItemStack offhand = config.getItemStack(path + ".offhand");
            if (offhand != null) {
                target.getInventory().setItemInOffHand(offhand);
            }

            target.setExp((float) config.getDouble(path + ".xp"));
            target.setLevel(config.getInt(path + ".level"));
            target.setHealth(config.getDouble(path + ".health"));
            target.setFoodLevel(config.getInt(path + ".food"));

            p.sendMessage("§a[QC] Przywrócono stan gracza " + target.getName());
        } catch (Exception e) {
            p.sendMessage("§c[QC] Błąd przywracania: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
