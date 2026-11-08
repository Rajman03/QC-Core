package pl.qc.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class QCConfigTest {

    @Test
    void configFileExistsAndIsReadable() {
        Path path = Path.of("src/main/resources/config.yml");
        assertTrue(Files.exists(path), "Default config should exist");
        assertDoesNotThrow(() -> Files.readString(path));
    }

    @Test
    void defaultConfigContainsLicenseSection() {
        String content = assertDoesNotThrow(() -> Files.readString(Path.of("src/main/resources/config.yml")));
        assertTrue(content.contains("license:"), "Config should contain license section");
        assertTrue(content.contains("key:"), "Config should contain key field");
    }
}
