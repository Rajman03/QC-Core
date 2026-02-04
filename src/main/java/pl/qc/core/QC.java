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
