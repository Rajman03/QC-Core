package pl.qc.core.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Remote {

    private static final Object LOCK = new Object();

    /** Dostęp do URL i egzekutora musi być synchronizowany przez LOCK. */
    private static volatile String webhookUrl = "";
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Ustawia adres webhook i (jeśli poprzedni executor był zamknięty po reload/shutdown)
     * tworzy nowy wątek roboczy.
     */
    public static void setWebhook(String url) {
        synchronized (LOCK) {
            webhookUrl = url == null ? "" : url;
            if (executor.isShutdown()) {
                executor = Executors.newSingleThreadExecutor();
            }
        }
    }

    /**
     * Wysyła wiadomość na Discord Webhook asynchronicznie.
     * Ignoruje wywołanie jeśli URL jest pusty lub executor jest już zamknięty.
     */
    public static void send(String title, String color, String desc, Map<String, String> fields) {
        synchronized (LOCK) {
            if (webhookUrl == null || webhookUrl.isEmpty() || executor.isShutdown()) {
                return;
            }

            // Snapshot lokalny – bezpieczny do użycia wewnątrz wątku
            final String urlSnapshot = webhookUrl;

            try {
                executor.submit(() -> doSend(title, color, desc, fields, urlSnapshot));
            } catch (Exception ignored) {
                // RejectedExecutionException – executor mógł zostać zamknięty
                // dokładnie w tym samym momencie; ignorujemy bezpiecznie
            }
        }
    }

    /**
     * Gracefully wyłącza executor; czeka maksymalnie 3 sekundy na zakończenie
     * zakolejkowanych żądań webhook, po czym wymusza zatrzymanie.
     */
    public static void finish() {
        synchronized (LOCK) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // --- Internals ---

    private static void doSend(String title, String color, String desc, Map<String, String> fields, String url) {
        HttpURLConnection conn = null;
        try {
            Map<String, String> safeFields = fields == null ? Collections.emptyMap() : fields;
            conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            StringBuilder fldJson = new StringBuilder();
            safeFields.forEach((k, v) -> {
                if (fldJson.length() > 0)
                    fldJson.append(",");
                fldJson.append("{\"name\": \"").append(esc(k)).append("\", \"value\": \"")
                        .append(esc(v)).append("\", \"inline\": true}");
            });

            String body = "{\"embeds\": [{\"title\": \"" + esc(title) + "\",\"color\": " + color +
                    ",\"description\": \"" + (desc != null ? esc(desc) : "") + "\",\"fields\": [" + fldJson + "]," +
                    "\"author\": {\"name\": \"QC System\"}," +
                    "\"footer\": {\"text\": \"v2.2\"}," +
                    "\"timestamp\": \"" + OffsetDateTime.now() + "\"}]}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            conn.getResponseCode();
        } catch (Exception ignored) {
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    private static String esc(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
