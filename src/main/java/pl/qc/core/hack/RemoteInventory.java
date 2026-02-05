package pl.qc.core.hack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.qc.core.QC;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RemoteInventory {

    private static final String FILE_NAME = "PrivateInv.yml";

    public static void open(Player p) {
        Inventory gui = Bukkit.createInventory(null, 54, "§0Tajny Schowek");
        File file = new File(QC.getInstance().getDataFolder(), FILE_NAME);

        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<?> list = config.getList("inventory");
            if (list != null) {
                for (int i = 0; i < Math.min(list.size(), 54); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof ItemStack) {
                        gui.setItem(i, (ItemStack) obj);
                    }
                }
            }
        }

        p.openInventory(gui);
    }

    public static void save(Inventory inv) {
        File file = new File(QC.getInstance().getDataFolder(), FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("inventory", Arrays.asList(inv.getContents()));

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
