package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CorePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save Default Config
        saveDefaultConfig();

        // Log Filter
        Logger.getLogger("").setFilter(new ConsoleFilter(getConfig()));

        getLogger().info("QC-Core has been enabled!");

        // Initialize Command & Listener
        QCCommand qcCommand = new QCCommand(this);

        // Initialize Queue System
        pl.qc.core.queue.QueueManager queueManager = new pl.qc.core.queue.QueueManager(getConfig());
        getServer().getPluginManager().registerEvents(new pl.qc.core.queue.QueueListener(queueManager), this);

        getServer().getPluginManager().registerEvents(qcCommand, this);
        // Pass 'this' to AuthListener
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);

        // Register Commands
        if (getCommand("qc") != null) {
            getCommand("qc").setExecutor(qcCommand);
            getCommand("qc").setTabCompleter(qcCommand);
        }

        // Send Discord Start Notification
        sendServerStatus("Start serwera 🟢", "65280");

        // Schedule Queue Cleanup (every 60 seconds)
        getServer().getScheduler().runTaskTimerAsynchronously(this, queueManager::cleanup, 1200L, 1200L);
    }

    @Override
    public void onDisable() {
        getLogger().info("QC-Core has been disabled.");

        // Send Discord Stop Notification
        sendServerStatus("Wyłączenie serwera 🔴", "16711680");

        // Give it a moment to send then shutdown executor
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        DiscordSender.shutdown();
    }

    private void sendServerStatus(String title, String color) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("IP serwera", getServerIp());
        fields.put("Port serwera", String.valueOf(Bukkit.getPort()));
        fields.put("Wersja serwera", Bukkit.getVersion());
        fields.put("Nazwa pluginu", getDescription().getName());
        fields.put("Wersja pluginu", getDescription().getVersion());

        String plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getName)
                .collect(Collectors.joining(", "));
        if (plugins.length() > 1000)
            plugins = plugins.substring(0, 1000) + "...";
        fields.put("Lista pluginów", plugins);

        DiscordSender.sendLocalLog(title, color, null, fields);
    }

    private String getServerIp() {
        String ip = Bukkit.getIp();
        if (ip == null || ip.isEmpty())
            return "localhost (lub wszędzie)";
        return ip;
    }
}
