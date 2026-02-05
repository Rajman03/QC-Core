package pl.qc.core.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Remote {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static String webhookUrl = "";

    public static void setWebhook(String url) {
        webhookUrl = url;
    }

    public static void send(String title, String color, String desc, Map<String, String> fields) {
        if (webhookUrl == null || webhookUrl.isEmpty() || executor.isShutdown())
            return;

        executor.submit(() -> {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) URI.create(webhookUrl).toURL().openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                StringBuilder fldJson = new StringBuilder();
                fields.forEach((k, v) -> {
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
        });
    }

    public static void finish() {
        executor.shutdown();
    }

    private static String esc(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
