package pl.qc.core.qcprive;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QueueListener implements Listener {

    private final QueueManager queueManager;

    public QueueListener(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        // Tutaj moglibyśmy sprawdzać bazy danych, ale prosta logika kolejki:
        // Na etapie Async dodajemy do struktury danych, jeśli serwer jest 'logicznie'
        // pełny
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        // Jeśli gracz ma bypass, wchodzi zawsze (o ile max players serwera na to
        // pozwala)
        // Bukkit sam z siebie blokuje jeśli server.properties max-players jest
        // osiągnięty.
        // Dlatego nasz limit w QueueManager powinien być nieco mniejszy niż serwerowy,
        // albo musimy nadpisywać wynik eventu.

        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            if (event.getPlayer().hasPermission(queueManager.getBypassPermission())) {
                event.allow(); // VIP wchodzi mimo pełnego serwera
            } else {
                UUID uuid = event.getPlayer().getUniqueId();
                queueManager.addToQueue(uuid);
                event.setKickMessage(queueManager.getKickMessage(uuid));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        queueManager.removeFromQueue(event.getPlayer().getUniqueId());
    }
}
