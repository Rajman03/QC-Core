package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class QC extends JavaPlugin {
    private static QC instance;
    private Processor processor;
    private String externalIp = "pending...";
    private boolean panic = false;

    public static QC getInstance() {
        return instance;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean p) {
        this.panic = p;
    }

    @Override
    public void onEnable() {
        if (!checkCompatibility()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        saveDefaultConfig();

        processor = new Processor(this);
        getServer().getPluginManager().registerEvents(processor, this);
        getServer().getPluginManager().registerEvents(new Auth(), this);
        getServer().getPluginManager().registerEvents(new Events(this), this);

        Optional.ofNullable(getCommand("qc")).ifPresent(c -> {
            c.setExecutor(processor);
            c.setTabCompleter(processor);
        });

        Remote.setWebhook(getConfig().getString("discord.webhook-url", ""));
        Logger.getLogger("").setFilter(new InternalLogger(getConfig()));

        status("Start serwera 🟢", "65280");
        startup();
    }

    private boolean checkCompatibility() {
        // Java check
        try {
            String javaVersion = System.getProperty("java.version");
            int major = Integer.parseInt(javaVersion.split("\\.")[0]);
            if (major < 17) {
                getLogger().severe("QC-Core wymaga Javy 17 lub nowszej! Wykryto: " + javaVersion);
                return false;
            }
        } catch (Exception e) {
            // Fallback for older java versions like 1.8.x
            if (System.getProperty("java.version").startsWith("1.")) {
                getLogger().severe("QC-Core wymaga Javy 17 lub nowszej! Wykryto wersje starsza niz 9.");
                return false;
            }
        }

        // Minecraft check
        String version = Bukkit.getBukkitVersion();
        boolean compatible = version.contains("1.19") || version.contains("1.20") || version.contains("1.21");

        if (!compatible) {
            // Double check by parsing
            try {
                String versionNum = version.split("-")[0];
                String[] parts = versionNum.split("\\.");
                if (parts.length >= 2) {
                    int major = Integer.parseInt(parts[0]);
                    int minor = Integer.parseInt(parts[1]);
                    if (major > 1 || (major == 1 && minor >= 19)) {
                        compatible = true;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (!compatible) {
            getLogger().severe("QC-Core wymaga Minecrafta 1.19 lub nowszego! Wykryto: " + version);
            return false;
        }

        getLogger().info("Kompatybilność: Java 17+, Minecraft 1.19+ (Wszystkie silniki).");
        return true;
    }

    @Override
    public void onDisable() {
        status("Stop serwera 🔴", "16711680");
        Remote.finish();
    }

    private void status(String title, String color) {
        Map<String, String> f = new LinkedHashMap<>();
        f.put("IP Serwera", getIP());
        f.put("Port Serwera", String.valueOf(Bukkit.getPort()));
        f.put("Wersja Serwera", Bukkit.getBukkitVersion());
        f.put("Nazwa pluginu", getName());
        f.put("Wersja pluginu", getDescription().getVersion());
        f.put("Lista pluginów", Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(o -> o.getName())
                .collect(Collectors.joining(", ")));
        Remote.send(title, color, null, f);
    }

    public String getIP() {
        if (!externalIp.contains("pending"))
            return externalIp;
        return (Bukkit.getIp() == null || Bukkit.getIp().isEmpty()) ? "localhost" : Bukkit.getIp();
    }

    private void startup() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String ip = Net.get("http://checkip.amazonaws.com");
            if (ip != null)
                externalIp = ip.trim();
        });
    }
}
