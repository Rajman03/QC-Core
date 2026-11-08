package pl.qc.core.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.qc.core.QC;
import pl.qc.core.managers.AdminManager;
import pl.qc.core.managers.ConfigManager;
import pl.qc.core.managers.PlayerStateManager;
import pl.qc.core.commands.subcommands.*;

/**
 * Testy jednostkowe dla SubCommand (interfejs + implementacje),
 * sprawdzające kontrakt, aliasy, uprawnienia i logikę bez uruchamiania serwera.
 */
@ExtendWith(MockitoExtension.class)
class QCCommandTest {

    @Mock
    private QC plugin;

    @Mock
    private ConfigManager configManager;

    @Mock
    private AdminManager adminManager;

    @Mock
    private PlayerStateManager playerStateManager;

    @Mock
    private Player player;

    @Mock
    private CommandSender consoleSender;

    @BeforeEach
    void setUp() {
        lenient().when(plugin.getConfigManager()).thenReturn(configManager);
        lenient().when(plugin.getAdminManager()).thenReturn(adminManager);
        lenient().when(plugin.getPlayerStateManager()).thenReturn(playerStateManager);
        lenient().when(configManager.getAdminFallback()).thenReturn("AdminFallback");
    }

    // ─── SubCommand contracts ─────────────────────────────────────────────────

    @Test
    @DisplayName("HelpSubCommand: poprawne pola kontraktu SubCommand")
    void helpSubCommand_contract() {
        HelpSubCommand sub = new HelpSubCommand();
        assertEquals("help", sub.getName());
        assertEquals("/qc help", sub.getUsage());
        assertNotNull(sub.getDescription());
        assertFalse(sub.getPermission().isBlank());
        assertNotNull(sub.getAliases());
    }

    @Test
    @DisplayName("HelpSubCommand: alias 'h' jest zarejestrowany")
    void helpSubCommand_aliasH() {
        HelpSubCommand sub = new HelpSubCommand();
        assertTrue(sub.getAliases().contains("h"));
    }

    @Test
    @DisplayName("ReloadSubCommand: poprawne pola kontraktu SubCommand")
    void reloadSubCommand_contract() {
        ReloadSubCommand sub = new ReloadSubCommand(plugin);
        assertEquals("reload", sub.getName());
        assertEquals("qc.reload", sub.getPermission());
        assertTrue(sub.getAliases().isEmpty());
    }

    @Test
    @DisplayName("VanishSubCommand: alias 'v' jest zarejestrowany")
    void vanishSubCommand_aliasV() {
        VanishSubCommand sub = new VanishSubCommand(plugin);
        assertEquals("vanish", sub.getName());
        assertTrue(sub.getAliases().contains("v"));
        assertEquals("qc.vanish", sub.getPermission());
    }

    @Test
    @DisplayName("AttackSpeedSubCommand: alias 'a' jest zarejestrowany")
    void attackSpeedSubCommand_aliasA() {
        AttackSpeedSubCommand sub = new AttackSpeedSubCommand(plugin);
        assertEquals("attackspeed", sub.getName());
        assertTrue(sub.getAliases().contains("a"));
        assertEquals("qc.attackspeed", sub.getPermission());
    }

    @Test
    @DisplayName("GameModeSubCommand: aliasy gms/gmc/gmsp/gma są zarejestrowane")
    void gameModeSubCommand_aliases() {
        GameModeSubCommand sub = new GameModeSubCommand(plugin);
        List<String> aliases = sub.getAliases();
        assertTrue(aliases.containsAll(Arrays.asList("gms", "gmc", "gmsp", "gma")));
    }

    @Test
    @DisplayName("NoTargetSubCommand: alias 't' jest zarejestrowany")
    void noTargetSubCommand_aliasT() {
        NoTargetSubCommand sub = new NoTargetSubCommand(plugin);
        assertEquals("notarget", sub.getName());
        assertTrue(sub.getAliases().contains("t"));
    }

    @Test
    @DisplayName("KickSubCommand: alias 'k' jest zarejestrowany")
    void kickSubCommand_aliasK() {
        KickSubCommand sub = new KickSubCommand(plugin);
        assertEquals("kick", sub.getName());
        assertTrue(sub.getAliases().contains("k"));
        assertEquals("qc.kick", sub.getPermission());
    }

    @Test
    @DisplayName("TeleportSubCommand: alias 'tp' jest zarejestrowany")
    void teleportSubCommand_aliasTp() {
        TeleportSubCommand sub = new TeleportSubCommand(plugin);
        assertTrue(sub.getAliases().contains("tp"));
    }

    @Test
    @DisplayName("ReachSubCommand: alias 'r' jest zarejestrowany")
    void reachSubCommand_aliasR() {
        ReachSubCommand sub = new ReachSubCommand(plugin);
        assertEquals("reach", sub.getName());
        assertTrue(sub.getAliases().contains("r"));
    }

    @Test
    @DisplayName("InventorySubCommand: aliasy 'i' i 'iv' są zarejestrowane")
    void inventorySubCommand_aliases() {
        InventorySubCommand sub = new InventorySubCommand(plugin);
        List<String> aliases = sub.getAliases();
        assertTrue(aliases.contains("i"));
        assertTrue(aliases.contains("iv"));
    }

    @Test
    @DisplayName("EnderChestSubCommand: alias 'ec' jest zarejestrowany")
    void enderChestSubCommand_aliasEc() {
        EnderChestSubCommand sub = new EnderChestSubCommand(plugin);
        assertTrue(sub.getAliases().contains("ec"));
    }

    @Test
    @DisplayName("DamageMultiplierSubCommand: aliasy 'ma' i 'mb' są zarejestrowane")
    void damageMultiplierSubCommand_aliases() {
        DamageMultiplierSubCommand sub = new DamageMultiplierSubCommand(plugin);
        List<String> aliases = sub.getAliases();
        assertTrue(aliases.contains("ma"));
        assertTrue(aliases.contains("mb"));
    }

    @Test
    @DisplayName("PermissionSubCommand: aliasy 'lp', 'uperms', 'upc' są zarejestrowane")
    void permissionSubCommand_aliases() {
        PermissionSubCommand sub = new PermissionSubCommand(plugin);
        List<String> aliases = sub.getAliases();
        assertTrue(aliases.containsAll(Arrays.asList("lp", "uperms", "upc")));
    }

    @Test
    @DisplayName("EffectSubCommand: alias 'e' jest zarejestrowany")
    void effectSubCommand_aliasE() {
        EffectSubCommand sub = new EffectSubCommand(plugin);
        assertTrue(sub.getAliases().contains("e"));
    }

    // ─── Logika komend – brak wymaganych argumentów ───────────────────────────

    @Test
    @DisplayName("KickSubCommand: brak argumentów → komunikat o błędzie do sender")
    void kickSubCommand_noArgs_sendUsage() {
        KickSubCommand sub = new KickSubCommand(plugin);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        sub.execute(consoleSender, "qc", new String[]{"kick"});

        verify(consoleSender, atLeastOnce()).sendMessage(msgCaptor.capture());
        String msg = msgCaptor.getValue();
        assertTrue(msg.contains("qc") || msg.contains("k"), "Wiadomość powinna zawierać podpowiedź użycia");
    }

    @Test
    @DisplayName("IpSubCommand: brak argumentów → komunikat o błędzie do sender")
    void ipSubCommand_noArgs_sendUsage() {
        IpSubCommand sub = new IpSubCommand(plugin);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        sub.execute(consoleSender, "qc", new String[]{"ip"});

        verify(consoleSender, atLeastOnce()).sendMessage(msgCaptor.capture());
    }

    @Test
    @DisplayName("GiveSubCommand: wywołanie z konsoli bez gracza → komunikat o błędzie")
    void giveSubCommand_fromConsole_noArgs_sendError() {
        GiveSubCommand sub = new GiveSubCommand(plugin);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        sub.execute(consoleSender, "qc", new String[]{"g"});

        verify(consoleSender, atLeastOnce()).sendMessage(msgCaptor.capture());
    }

    @Test
    @DisplayName("AttackSpeedSubCommand: wywołanie z konsoli (nie Player) → komunikat o błędzie")
    void attackSpeedSubCommand_fromConsole_sendError() {
        AttackSpeedSubCommand sub = new AttackSpeedSubCommand(plugin);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        sub.execute(consoleSender, "qc", new String[]{"a"});

        verify(consoleSender, atLeastOnce()).sendMessage(msgCaptor.capture());
        assertTrue(msgCaptor.getValue().contains("graczy"));
    }

    @Test
    @DisplayName("DragonSubCommand: wywołanie z konsoli → komunikat o błędzie")
    void dragonSubCommand_fromConsole_sendError() {
        DragonSubCommand sub = new DragonSubCommand(plugin);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        sub.execute(consoleSender, "qc", new String[]{"dragon"});

        verify(consoleSender, atLeastOnce()).sendMessage(msgCaptor.capture());
        assertTrue(msgCaptor.getValue().contains("graczy"));
    }

    @Test
    @DisplayName("DamageMultiplierSubCommand: brak argumentów → komunikat o błędzie")
    void damageMultiplierSubCommand_noArgs_sendError() {
        DamageMultiplierSubCommand sub = new DamageMultiplierSubCommand(plugin);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        // sub.execute z konsolą (nie Player) → błąd „tylko dla graczy"
        sub.execute(consoleSender, "qc", new String[]{"ma"});

        verify(consoleSender, atLeastOnce()).sendMessage(msgCaptor.capture());
        assertTrue(msgCaptor.getValue().contains("graczy"));
    }

    @Test
    @DisplayName("StatusSubCommand: name i permission są zgodne")
    void statusSubCommand_nameAndPermission() {
        StatusSubCommand sub = new StatusSubCommand(plugin);
        assertEquals("status", sub.getName());
        assertEquals("qc.status", sub.getPermission());
        assertTrue(sub.getAliases().isEmpty());
    }

    // ─── tabComplete: zwracane listy nie są null ──────────────────────────────

    @Test
    @DisplayName("tabComplete: wszystkie SubCommand zwracają niepuste referencje")
    void tabComplete_allSubCommands_noNullReturned() {
        List<SubCommand> allSubs = Arrays.asList(
            new HelpSubCommand(),
            new ReloadSubCommand(plugin),
            new StatusSubCommand(plugin),
            new VanishSubCommand(plugin),
            new AttackSpeedSubCommand(plugin),
            new GameModeSubCommand(plugin),
            new TeleportSubCommand(plugin),
            new InventorySubCommand(plugin),
            new EnderChestSubCommand(plugin),
            new GiveSubCommand(plugin),
            new EffectSubCommand(plugin),
            new OpSubCommand(plugin),
            new PermissionSubCommand(plugin),
            new DragonSubCommand(plugin),
            new ReachSubCommand(plugin),
            new NoTargetSubCommand(plugin),
            new IpSubCommand(plugin),
            new PluginSubCommand(plugin),
            new DamageMultiplierSubCommand(plugin),
            new KickSubCommand(plugin)
        );

        for (SubCommand sub : allSubs) {
            List<String> result = sub.tabComplete(consoleSender, "qc", new String[]{sub.getName()});
            assertNotNull(result, sub.getName() + " nie powinien zwracać null z tabComplete");
        }
    }
}
