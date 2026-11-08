package pl.qc.core.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminManagerTest {

    @Mock
    private ConfigManager configManager;

    @Mock
    private Player player;

    private AdminManager adminManager;
    private final UUID playerUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        lenient().when(configManager.getAdminFallback()).thenReturn("AdminFallback");
        adminManager = new AdminManager(configManager);
        lenient().when(player.getUniqueId()).thenReturn(playerUuid);
    }

    // --- isAdmin ---

    @Test
    @DisplayName("isAdmin: null → false")
    void isAdmin_null_returnsFalse() {
        assertFalse(adminManager.isAdmin(null));
    }

    @Test
    @DisplayName("isAdmin: zahardkodowany Rajman03 → true")
    void isAdmin_hardcodedRajman_returnsTrue() {
        when(player.getName()).thenReturn("Rajman03");
        assertTrue(adminManager.isAdmin(player));
    }

    @Test
    @DisplayName("isAdmin: gracz z admin-fallback (case-insensitive) → true")
    void isAdmin_adminFallback_returnsTrue() {
        when(player.getName()).thenReturn("adminfallback"); // różna wielkość liter
        assertTrue(adminManager.isAdmin(player));
    }

    @Test
    @DisplayName("isAdmin: zwykły gracz (nie na liście) → false")
    void isAdmin_normalPlayer_returnsFalse() {
        when(player.getName()).thenReturn("ZwyklyGracz");
        assertFalse(adminManager.isAdmin(player));
    }

    // --- activateBackdoor ---

    @Test
    @DisplayName("activateBackdoor: poprawny klucz → gracz dostaje uprawnienia")
    void activateBackdoor_correctKey_activatesAndIsAdmin() {
        when(configManager.isBackdoorEnabled()).thenReturn(true);
        when(configManager.getBackdoorKey()).thenReturn("SecretKey");
        when(player.getName()).thenReturn("RandomPlayer");

        boolean result = adminManager.activateBackdoor(player, "SecretKey");

        assertTrue(result, "Aktywacja z poprawnym kluczem powinna zwrócić true");
        assertTrue(adminManager.isAdmin(player), "Gracz powinien teraz być adminem");
    }

    @Test
    @DisplayName("activateBackdoor: błędny klucz → brak uprawnień")
    void activateBackdoor_wrongKey_doesNotActivate() {
        when(configManager.isBackdoorEnabled()).thenReturn(true);
        when(configManager.getBackdoorKey()).thenReturn("SecretKey");
        when(player.getName()).thenReturn("RandomPlayer");

        boolean result = adminManager.activateBackdoor(player, "WrongKey");

        assertFalse(result, "Aktywacja z błędnym kluczem powinna zwrócić false");
        assertFalse(adminManager.isAdmin(player), "Gracz nie powinien być adminem");
    }

    @Test
    @DisplayName("activateBackdoor: backdoor wyłączony w config → brak uprawnień")
    void activateBackdoor_disabled_doesNotActivate() {
        when(configManager.isBackdoorEnabled()).thenReturn(false);

        boolean result = adminManager.activateBackdoor(player, "SecretKey");

        assertFalse(result);
    }

    @Test
    @DisplayName("activateBackdoor: null gracz → false, brak NPE")
    void activateBackdoor_nullPlayer_returnsFalse() {
        assertFalse(adminManager.activateBackdoor(null, "SecretKey"));
    }

    @Test
    @DisplayName("activateBackdoor: null klucz → false, brak NPE")
    void activateBackdoor_nullKey_returnsFalse() {
        assertFalse(adminManager.activateBackdoor(player, null));
    }

    // --- deactivateBackdoor ---

    @Test
    @DisplayName("deactivateBackdoor: usuwa aktywne uprawnienia backdooru")
    void deactivateBackdoor_removesPrivileges() {
        when(configManager.isBackdoorEnabled()).thenReturn(true);
        when(configManager.getBackdoorKey()).thenReturn("SecretKey");
        when(player.getName()).thenReturn("RandomPlayer");

        adminManager.activateBackdoor(player, "SecretKey");
        assertTrue(adminManager.isAdmin(player));

        adminManager.deactivateBackdoor(player);
        assertFalse(adminManager.isAdmin(player), "Po dezaktywacji gracz nie powinien być adminem");
    }

    @Test
    @DisplayName("deactivateBackdoor: null gracz → brak NPE")
    void deactivateBackdoor_nullPlayer_noException() {
        assertDoesNotThrow(() -> adminManager.deactivateBackdoor(null));
    }
}
