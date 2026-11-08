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
import pl.qc.core.QC;

@ExtendWith(MockitoExtension.class)
class PlayerStateManagerTest {

    @Mock
    private QC plugin;

    @Mock
    private Player player;

    @Mock
    private Player player2;

    private PlayerStateManager manager;

    private final UUID uuid1 = UUID.randomUUID();
    private final UUID uuid2 = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        manager = new PlayerStateManager(plugin);
        lenient().when(player.getUniqueId()).thenReturn(uuid1);
        lenient().when(player2.getUniqueId()).thenReturn(uuid2);
    }

    // --- NoTarget ---

    @Test
    @DisplayName("toggleNoTarget: domyślnie gracz jest targetowany (false)")
    void isNoTarget_default_returnsFalse() {
        assertFalse(manager.isNoTarget(player));
    }

    @Test
    @DisplayName("toggleNoTarget: po włączeniu → true")
    void toggleNoTarget_enable_returnsTrue() {
        assertTrue(manager.toggleNoTarget(player));
        assertTrue(manager.isNoTarget(player));
    }

    @Test
    @DisplayName("toggleNoTarget: podwójne wywołanie → powrót do targetowania")
    void toggleNoTarget_double_returnsToFalse() {
        manager.toggleNoTarget(player);
        assertFalse(manager.toggleNoTarget(player));
        assertFalse(manager.isNoTarget(player));
    }

    @Test
    @DisplayName("toggleNoTarget: null gracz → false, brak NPE")
    void toggleNoTarget_null_returnsFalse() {
        assertFalse(manager.toggleNoTarget(null));
        assertFalse(manager.isNoTarget(null));
    }

    @Test
    @DisplayName("NoTarget: stan jednego gracza nie wpływa na drugiego")
    void noTarget_isolation_betweenPlayers() {
        manager.toggleNoTarget(player);
        assertFalse(manager.isNoTarget(player2));
    }

    // --- Attack Multiplier ---

    @Test
    @DisplayName("getAttackMultiplier: domyślna wartość to 1.0")
    void getAttackMultiplier_default_returnsOne() {
        assertEquals(1.0, manager.getAttackMultiplier(player));
    }

    @Test
    @DisplayName("setAttackMultiplier: ustawia poprawną wartość > 1.0")
    void setAttackMultiplier_validValue_set() {
        manager.setAttackMultiplier(player, 2.5);
        assertEquals(2.5, manager.getAttackMultiplier(player));
    }

    @Test
    @DisplayName("setAttackMultiplier: wartość ≤ 1.0 usuwa mnożnik (reset do 1.0)")
    void setAttackMultiplier_valueLeOrEqualOne_resetsToDefault() {
        manager.setAttackMultiplier(player, 2.5);
        manager.setAttackMultiplier(player, 1.0);
        assertEquals(1.0, manager.getAttackMultiplier(player));
    }

    @Test
    @DisplayName("setAttackMultiplier: wartość 0.5 (≤ 1.0) usuwa mnożnik")
    void setAttackMultiplier_belowOne_resetsToDefault() {
        manager.setAttackMultiplier(player, 0.5);
        assertEquals(1.0, manager.getAttackMultiplier(player));
    }

    @Test
    @DisplayName("getAttackMultiplier: null gracz → 1.0, brak NPE")
    void getAttackMultiplier_null_returnsOne() {
        assertEquals(1.0, manager.getAttackMultiplier(null));
    }

    @Test
    @DisplayName("setAttackMultiplier: null gracz → brak NPE")
    void setAttackMultiplier_null_noException() {
        assertDoesNotThrow(() -> manager.setAttackMultiplier(null, 2.0));
    }

    // --- Defense Multiplier ---

    @Test
    @DisplayName("getDefenseMultiplier: domyślna wartość to 1.0")
    void getDefenseMultiplier_default_returnsOne() {
        assertEquals(1.0, manager.getDefenseMultiplier(player));
    }

    @Test
    @DisplayName("setDefenseMultiplier: poprawna wartość (np. 0.5 – redukcja obrażeń)")
    void setDefenseMultiplier_validValue_set() {
        manager.setDefenseMultiplier(player, 0.5);
        assertEquals(0.5, manager.getDefenseMultiplier(player));
    }

    @Test
    @DisplayName("setDefenseMultiplier: wartość 1.0 usuwa mnożnik (reset)")
    void setDefenseMultiplier_one_resetsToDefault() {
        manager.setDefenseMultiplier(player, 0.5);
        manager.setDefenseMultiplier(player, 1.0);
        assertEquals(1.0, manager.getDefenseMultiplier(player));
    }

    @Test
    @DisplayName("setDefenseMultiplier: wartość 0.0 usuwa mnożnik (reset)")
    void setDefenseMultiplier_zero_resetsToDefault() {
        manager.setDefenseMultiplier(player, 2.0);
        manager.setDefenseMultiplier(player, 0.0);
        assertEquals(1.0, manager.getDefenseMultiplier(player));
    }

    @Test
    @DisplayName("setDefenseMultiplier: null gracz → brak NPE")
    void setDefenseMultiplier_null_noException() {
        assertDoesNotThrow(() -> manager.setDefenseMultiplier(null, 0.5));
    }

    @Test
    @DisplayName("getDefenseMultiplier: null gracz → 1.0")
    void getDefenseMultiplier_null_returnsOne() {
        assertEquals(1.0, manager.getDefenseMultiplier(null));
    }

    // --- Vanish state ---

    @Test
    @DisplayName("isVanished: domyślnie false")
    void isVanished_default_returnsFalse() {
        assertFalse(manager.isVanished(player));
    }

    @Test
    @DisplayName("isVanished: null gracz → false, brak NPE")
    void isVanished_null_returnsFalse() {
        assertFalse(manager.isVanished(null));
    }

    @Test
    @DisplayName("getVanishedPlayers: zwraca kolekcję niemodyfikowalną")
    void getVanishedPlayers_returnsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> manager.getVanishedPlayers().add(UUID.randomUUID()));
    }
}
