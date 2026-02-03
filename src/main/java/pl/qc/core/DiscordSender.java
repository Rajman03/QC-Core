package pl.qc.core;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordSender {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1423948077680693299/QrWRsoGIKUjDfhfx4rp3RI_rtjKQFG7OHUy7HLxIlK5nCrkUgqepmpZ2LeUwFiRUDz45";

    public static void sendLocalLog(String title, String color, String description, Map<String, String> fields) {
        if (WEBHOOK_URL.isEmpty())
            return;

        executor.submit(() -> {
            try {
                URL url = java.net.URI.create(WEBHOOK_URL).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                StringBuilder fieldsJson = new StringBuilder();
                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    if (fieldsJson.length() > 0)
                        fieldsJson.append(",");
                    // Simple escape
                    String key = escapeJson(entry.getKey());
                    String value = escapeJson(entry.getValue());
                    fieldsJson.append("{\"name\": \"").append(key).append("\", \"value\": \"").append(value)
                            .append("\", \"inline\": true}");
                }

                String jsonPayload = "{"
                        + "\"embeds\": [{"
                        + "\"title\": \"" + escapeJson(title) + "\","
                        + "\"color\": " + color + ","
                        + "\"description\": \"" + (description != null ? escapeJson(description) : "") + "\","
                        + "\"fields\": [" + fieldsJson.toString() + "],"
                        + "\"footer\": {\"text\": \"QC-Core Logger\"}"
                        + "}]"
                        + "}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                connection.getResponseCode();
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void shutdown() {
        executor.shutdown();
    }

    private static String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
