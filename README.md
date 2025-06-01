# KPMovies

**KPMovies** to prosty prototyp aplikacji mobilnej tworzonej w Android Studio (Kotlin + AndroidX + Material 3). Aplikacja umożliwia:

- Rejestrację i logowanie użytkownika (demo, bez prawdziwego backendu/Firebase).
- Resetowanie hasła (demo).
- Ekran główny (“Home”) wyświetlający przykładowe rekomendacje filmowe i aktywność znajomych.
- Dolną nawigację z ikonami: Home, Search, Menu.
- Wysuwane menu boczne (Drawer) z prawej strony, które zawiera nagłówek (logo + nickname + przycisk wylogowania) oraz listę pozycji (Home page, Watch list, Friends, Browse, Settings). Tło pod Drawerem jest lekko przyciemnione (dimmer) lub rozmyte (od Android 12+).

Poniżej szczegółowy opis aktualnego stanu projektu.

---

## Spis treści

1. [Funkcjonalności](#funkcjonalności)  
2. [Struktura projektu](#struktura-projektu)  
3. [Wymagania systemowe](#wymagania-systemowe)  
4. [Instrukcja uruchomienia](#instrukcja-uruchomienia)  
5. [Opis głównych ekranów i logika](#opis-głównych-ekranów-i-logika)  


---

## Funkcjonalności

1. **Ekran logowania (LoginActivity)**
   - Pola: Login (tekst), Password (tekst typu „hasło”).
   - Przycisk **Log in** – walidacja, dla demo zawsze przechodzi do `HomeActivity`.
   - Linki:
     - „Register” → przekierowanie do ekranu rejestracji (`RegisterActivity`).
     - „Forgot password?” → przekierowanie do ekranu resetowania hasła (`ResetPasswordActivity`).

2. **Ekran rejestracji (RegisterActivity)**
   - Pola: Login, Password, Repeat Password.
   - Przycisk **Register** – walidacja (hasła muszą być identyczne, niepuste), demo → przekierowanie do `LoginActivity`.
   - Link “Log in” → powrót na ekran logowania.

3. **Ekran resetowania hasła (ResetPasswordActivity)**
   - Pola: New password, Repeat password.
   - Przycisk **Reset password** – walidacja (hasła identyczne, niepuste), demo → toast „Hasło zresetowane – demo” i powrót do `LoginActivity`.
   - Link “Log in” → powrót do `LoginActivity`.

4. **Ekran główny (HomeActivity)**  
   Po zalogowaniu (lub rejestracji) uruchamia się `HomeActivity` z:
   - Ustawionym nickname (przekazanym z `LoginActivity` jako String extra “nick”).
   - Widokiem z rekomendacjami („Today’s recommendations”) – przykładowe prostokąty (`ImageView`) + podpis „Title”.
   - Widokiem „Friends recent activity” – analogicznie.
   - Dolną nawigacją (`BottomNavigationView`) z ikonami:
     - Home (nic nie robi, bo jest już na tym ekranie).
     - Search (na razie brak implementacji).
     - Menu (hamburger) – otwiera/zamyka Drawer z prawej strony.
   - **Drawer (NavigationView)** wysuwane z prawej:
     - Nagłówek (`drawer_header.xml`) zawiera:
       - Logo aplikacji (ikona).
       - Tekst „Nickname” (zmieniany dynamicznie).
       - Ikona wylogowania – po kliknięciu przekierowuje do `LoginActivity` i czyści stack.
     - Lista pozycji w `drawer_menu.xml`:
       - Home page → zamyka Drawer, pokazuje toast „Home page”.
       - Watch list, Friends, Browse, Settings → brak implementacji (klikają, ale nic się nie dzieje).

5. **Tło za Drawerem**  
   - Ustawiono `binding.drawerLayout.setScrimColor(0x66000000)` (40 % czerni), więc reszta ekranu jest przyciemniona.
   - Na Android 12+ (API 31) można dodatkowo dodawać blur przy użyciu `RenderEffect`, ale w bieżącej wersji jest tylko dimmer.

---
- **`ActivityHomeBinding`** generuje wiązania do pliku `activity_home.xml`.  
- W `strings.xml` znajdują się wszystkie etykiety (tekst “Nickname”, “Logout” itp.).  
- W `drawer_menu.xml` definiujemy listę elementów menu (ikony + tytuły).

---

## Wymagania systemowe

- Android Studio (zalecane wersja 2022.3 lub nowsza).  
- Gradle 7.x (widoczne w `build.gradle.kts`).  
- Kompilacja na urządzenia lub emulatorze z minimum **Android API 21 (Lollipop)**.  
- Dla efektu blur wymagana jest przynajmniej **Android 12 (API 31)**. Bez blur na niższych systemach — używany tylko półprzezroczysty scrim.

---

## Instrukcja uruchomienia

1. **Sklonuj lub pobierz** repozytorium (archiwum ZIP).  
2. Otwórz Android Studio i wybierz **Open an Existing Project**, wskaż katalog `KPMovies`.  
3. Poczekaj, aż Gradle zsynchronizuje zależności i zbuduje projekt.  
4. Podłącz urządzenie z włączonym debugowaniem USB lub uruchom **Android Emulator** (min. API 21).  
5. Kliknij zieloną strzałkę „Run” → wybierz urządzenie/emulator. Aplikacja zostanie zainstalowana i uruchomiona.

---

## Opis głównych ekranów i logika

### 1) Ekran logowania (`LoginActivity.kt`)

- Layout: `res/layout/activity_login.xml`  
- Komponenty:  
  - `TextInputEditText` → `etLogin`, `etPassword`  
  - `Button` → `btnLogin`  
  - `TextView` → `tvGoRegister` (przekierowanie do `RegisterActivity`)  
  - `TextView` → `tvForgot` (przekierowanie do `ResetPasswordActivity`)  
- Kod:
  - Waliduje, czy pola nie są puste.
  - Jeśli poprawne, nowa aktywność:  
    ```kotlin
    val intent = Intent(this, HomeActivity::class.java)
    intent.putExtra("nick", loginText)  // przekazanie nicku
    startActivity(intent)
    finish()
    ```

### 2) Ekran rejestracji (`RegisterActivity.kt`)

- Layout: `res/layout/activity_register.xml`  
- Komponenty:
  - `TextInputEditText` → `etLogin`, `etPassword`, `etRepeat`
  - `Button` → `btnRegister`
  - `TextView` → `tvGoLogin`  
- Kod:
  - Sprawdzenie, czy `etPassword.text == etRepeat.text` i niepuste.
  - Po rejestracji toast „Registered (demo)”, `finish()` → powrót do `LoginActivity`.

### 3) Ekran resetowania hasła (`ResetPasswordActivity.kt`)

- Layout: `res/layout/activity_reset_password.xml`  
- Komponenty:
  - `TextInputEditText` → `etNewPass`, `etRepeatPass`
  - `Button` → `btnReset`
  - `TextView` → `tvGoLogin`  
- Kod:
  - Sprawdzenie, czy nowe hasło i powtórzenie są identyczne i niepuste.
  - Toast „Hasło zresetowane – demo 🔑”, `finish()` → powrót do `LoginActivity`.

### 4) Ekran główny (`HomeActivity.kt`)

- Layout: `res/layout/activity_home.xml`  
  - Główny `DrawerLayout`:
    - `FrameLayout` (zagnieżdżony) → zawartość ekranu:
      - `ScrollView` → `LinearLayout` z:
        - “Awatar + Nickname” (ikona + TextView)
        - “Today’s recommendations” (ConstraintLayout z dwoma `ImageView` + tytuły)
        - “Friends recent activity” (analogicznie ConstraintLayout)
      - `BottomNavigationView` (ikony Home, Search, Menu)
    - `NavigationView` (wysuwane menu od prawej):
      - `app:headerLayout="@layout/drawer_header"`
      - `app:menu="@menu/drawer_menu"`

- Kod kluczowy w `HomeActivity.kt`:
  ```kotlin
  // 1. Ustawianie przekazanego nicku:
  val nick = intent.getStringExtra("nick")
  if (!nick.isNullOrEmpty()) {
      binding.tvNickname.text = nick
      val header = binding.navView.getHeaderView(0)
      header.findViewById<TextView>(R.id.drawerNickname).text = nick
  }

  // 2. BottomNavigationView:
  binding.bottomNav.setOnItemSelectedListener { item ->
      when (item.itemId) {
          R.id.nav_home   -> { /* nic */ }
          R.id.nav_search -> { /* TODO */ }
          R.id.nav_menu   -> toggleDrawer()
      }
      true
  }

  // 3. NavigationView (Drawer) – obsługa kliknięć:
  binding.navView.setNavigationItemSelectedListener { menuItem ->
      when (menuItem.itemId) {
          R.id.nav_homepage -> {
              binding.drawerLayout.closeDrawer(GravityCompat.END)
              toast("Home page")
          }
          // pozostałe (nav_watchlist, nav_friends, nav_browse, nav_settings) -> brak akcji
      }
      true
  }

  // 4. Przycisk wylogowania w headerze Drawer:
  binding.navView.getHeaderView(0)
      .findViewById<ImageView>(R.id.btnLogout)
      .setOnClickListener {
          val i = Intent(this, LoginActivity::class.java)
          i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          startActivity(i)
          finish()
      }

  // 5. Dimmer / Blur pod Drawerem:
  binding.drawerLayout.setScrimColor(0x66000000)          // 40% czerni
  // (lub: .setScrimColor(Color.TRANSPARENT) + blur na Android 12+, jeśli zaimplementowane)

  // 6. Obsługa Back:
  @Suppress("DEPRECATION")
  override fun onBackPressed() {
      if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
          binding.drawerLayout.closeDrawer(GravityCompat.END)
      } else {
          super.onBackPressed()
      }
  }
