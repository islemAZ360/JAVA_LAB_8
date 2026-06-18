package client.gui.core;

import java.util.Locale;

public final class Messages {
    private Messages() {
    }

    public enum Lang {
        RU("Русский", "ru", "RU"),
        SK("Slovenčina", "sk", "SK"),
        SQ("Shqip", "sq", "AL"),
        //        VI("Vietnam", "vi", "VI"),
        EN_CA("English (Canada)", "en", "CA");

        private final String displayName;
        private final Locale locale;

        Lang(String displayName, String language, String country) {
            this.displayName = displayName;
            this.locale = new Locale(language, country);
        }

        public String displayName() {
            return displayName;
        }

        public Locale locale() {
            return locale;
        }
    }

    private record Translation(String ru, String sk, String sq, String enCa) {
        String get(Lang lang) {
            return switch (lang) {
                case RU -> ru;
                case SK -> sk;
                case SQ -> sq;
                case EN_CA -> enCa;
            };
        }
    }

    public enum Key {
        LOGIN_TITLE(new Translation("Авторизация", "Autorizácia", "Autorizimi", "Login")),
        LOGIN(new Translation("Войти", "Prihlásiť sa", "Hyr", "Login")),
        REGISTER(new Translation("Зарегистрироваться", "Registrovať sa", "Regjistrohu", "Register")),
        USERNAME(new Translation("Логин", "Používateľ", "Përdoruesi", "Username")),
        PASSWORD(new Translation("Пароль", "Heslo", "Fjalëkalimi", "Password")),

        // ========================================
        // AUTH SCREENS
        // ========================================

        LOGIN_SUBTITLE(new Translation("Коллекция HumanBeing", "Kolekcia HumanBeing", "Koleksioni HumanBeing", "HumanBeing Collection")),
        REGISTER_TITLE(new Translation("Регистрация", "Registrácia", "Regjistrimi", "Register")),
        REGISTER_SUBTITLE(new Translation("Создать новый аккаунт", "Vytvoriť nový účet", "Krijo llogari të re", "Create new account")),
        CONFIRM_PASSWORD(new Translation("Подтвердите пароль", "Potvrďte heslo", "Konfirmoni fjalëkalimin", "Confirm Password")),
        SWITCH_TO_REGISTER(new Translation("Нет аккаунта? Зарегистрироваться", "Nemáte účet? Zaregistrujte sa", "Nuk keni llogari? Regjistrohu", "Don't have an account? Register")),
        SWITCH_TO_LOGIN(new Translation("Уже есть аккаунт? Войти", "Už máte účet? Prihláste sa", "Keni llogari? Hyni", "Already have an account? Login")),

        // Validation errors
        ERROR_USERNAME_EMPTY(new Translation("Имя пользователя не может быть пустым", "Používateľské meno nemôže byť prázdne", "Përdoruesi nuk mund të jetë bosh", "Username cannot be empty")),
        ERROR_USERNAME_TOO_SHORT(new Translation("Имя пользователя должно содержать минимум 3 символа", "Používateľské meno musí mať aspoň 3 znaky", "Përdoruesi duhet të ketë të paktën 3 karaktere", "Username must be at least 3 characters")),
        ERROR_PASSWORD_EMPTY(new Translation("Пароль не может быть пустым", "Heslo nemôže byť prázdne", "Fjalëkalimi nuk mund të jetë bosh", "Password cannot be empty")),
        ERROR_PASSWORD_TOO_SHORT(new Translation("Пароль должен содержать минимум 6 символов", "Heslo musí mať aspoň 6 znakov", "Fjalëkalimi duhet të ketë të paktën 6 karaktere", "Password must be at least 6 characters")),
        ERROR_CONFIRM_PASSWORD_EMPTY(new Translation("Пожалуйста, подтвердите пароль", "Prosím, potvrďte heslo", "Ju lutemi konfirmoni fjalëkalimin", "Please confirm your password")),
        ERROR_PASSWORD_MISMATCH(new Translation("Пароли не совпадают", "Heslá sa nezhodujú", "Fjalëkalimet nuk përputhen", "Passwords do not match")),

        CURRENT_USER(new Translation("Текущий пользователь", "Aktuálny používateľ", "Përdoruesi aktual", "Current user")),
        LANGUAGE(new Translation("Язык", "Jazyk", "Gjuha", "Language")),
        THEME_DARK(new Translation("Тёмная тема", "Tmavá téma", "Tema e errët", "Dark theme")),
        THEME_LIGHT(new Translation("Светлая тема", "Svetlá téma", "Tema e ndritshme", "Light theme")),
        LOGOUT(new Translation("Выйти", "Odhlásiť sa", "Dil", "Logout")),
        // ========================================
        // DASHBOARD NAVIGATION
        // ========================================
        NAVIGATION(new Translation(
                "Навигация", "Navigácia", "Navigimi", "Navigation"
        )),
        COLLECTION(new Translation(
                "Коллекция объектов", "Kolekcia objektov", "Koleksioni i objekteve", "Object collection"
        )),
        COMMANDS(new Translation(
                "Команды", "Príkazy", "Komandat", "Commands"
        )),
        VISUALIZATION(new Translation(
                "Визуализация", "Vizualizácia", "Vizualizimi", "Visualization"
        )),
        SETTINGS(new Translation(
                "Настройки", "Nastavenia", "Cilësimet", "Settings"
        )),

        // ========================================
        // COMMAND BAR ACTIONS
        // ========================================
        CLEAR_MINE(new Translation(
                "Очистить мои", "Vymazať moje", "Pastro të mijat", "Clear mine"
        )),
        INFO(new Translation(
                "Инфо", "Info", "Info", "Info"
        )),
        ADD_IF_MAX(new Translation(
                "Добавить если max", "Pridať ak max", "Shto nëse max", "Add if max"
        )),
        ADD_IF_MIN(new Translation(
                "Добавить если min", "Pridať ak min", "Shto nëse min", "Add if min"
        )),
        REMOVE_GREATER(new Translation(
                "Удалить большие", "Odstrániť väčšie", "Hiq më të mëdhatë", "Remove greater"
        )),
        REFRESH(new Translation(
                "Обновить", "Obnoviť", "Rifresko", "Refresh"
        )),

        // ========================================
        // TABLE PANEL
        // ========================================
        OBJECT_TABLE_TITLE(new Translation(
                "Таблица объектов", "Tabuľka objektov", "Tabela e objekteve", "Object table"
        )),
        OBJECT_TABLE_DESCRIPTION(new Translation(
                "Каждое поле объекта показано отдельной колонкой.",
                "Každé pole objektu je zobrazené v samostatnom stĺpci.",
                "Çdo fushë e objektit shfaqet në një kolonë të veçantë.",
                "Each object field is shown in a separate column."
        )),
        FILTER_PROMPT(new Translation(
                "name / owner / mood / car",
                "name / owner / mood / car",
                "name / owner / mood / car",
                "name / owner / mood / car"
        )),
        APPLY_STREAM_FILTER(new Translation(
                "Применить (Stream)", "Použiť (Stream)", "Apliko (Stream)", "Apply (Stream)"
        )),
        RESET(new Translation(
                "Сбросить", "Resetovať", "Rivendos", "Reset"
        )),
        SORT_BY_NAME(new Translation(
                "Сортировать по имени", "Triediť podľa mena", "Rendit sipas emrit", "Sort by name"
        )),
        STATUS_FILTER_APPLIED(new Translation(
                "Фильтр выполнен через Streams API",
                "Filter vykonaný cez Streams API",
                "Filtrimi u krye nëpërmjet Streams API",
                "Filter applied via Streams API"
        )),
        STATUS_FILTER_RESET(new Translation(
                "Фильтр/сортировка сброшены",
                "Filter/triedenie resetované",
                "Filtrimi/renditja u rivendos",
                "Filter/sort reset"
        )),
        STATUS_SORT_APPLIED(new Translation(
                "Сортировка по имени выполнена через Streams API",
                "Triedenie podľa mena vykonané cez Streams API",
                "Renditja sipas emrit u krye nëpërmjet Streams API",
                "Sort by name applied via Streams API"
        )),
        // ========================================
        // VISUALIZATION PANEL
        // ========================================
        VISUALIZATION_TITLE(new Translation(
                "Визуализация объектов", "Vizualizácia objektov", "Vizualizimi i objekteve", "Object visualization"
        )),
        VISUALIZATION_DESCRIPTION(new Translation(
                "Объекты рисуются графическими примитивами на Canvas.",
                "Objekty sa vykresľujú grafickými primitívami na Canvas.",
                "Objektet vizatohen me primitive grafike në Canvas.",
                "Objects are rendered as graphical primitives on Canvas."
        )),
        CLICK_OBJECT(new Translation(
                "Кликните по объекту", "Kliknite na objekt", "Klikoni mbi objektin", "Click an object"
        )),
        CLICK_OBJECT_DESCRIPTION(new Translation(
                "Чтобы увидеть его детали", "Pre zobrazenie detailov", "Për të parë detajet e tij", "To see its details"
        )),
        SELECT_OBJECT_HINT(new Translation(
                "Выберите объект для отображения информации",
                "Vyberte objekt pre zobrazenie informácií",
                "Zgjidhni një objekt për të shfaqur informacionin",
                "Select an object to display information"
        )),
        OBJECT_INFO_ID(new Translation("id", "id", "id", "id")),
        OBJECT_INFO_NAME(new Translation("имя", "meno", "emri", "name")),
        OBJECT_INFO_COORDINATES(new Translation("координаты", "súradnice", "koordinatat", "coordinates")),
        OBJECT_INFO_OWNER(new Translation("владелец", "vlastník", "pronari", "owner")),
        OBJECT_INFO_MOOD(new Translation("настроение", "nálada", "dispozita", "mood")),
        OBJECT_INFO_IMPACT_SPEED(new Translation("скорость", "rýchlosť", "shpejtësia", "impactSpeed")),
        OBJECT_INFO_MINUTES(new Translation("минуты", "minúty", "minutat", "minutes")),
        OBJECT_INFO_WEAPON(new Translation("оружие", "zbraň", "arma", "weapon")),
        OBJECT_INFO_CAR(new Translation("машина", "auto", "makina", "car")),
        OBJECT_INFO_COOL(new Translation("крутая", "skvelé", "cool", "cool")),

        ADD(new Translation("Добавить", "Pridať", "Shto", "Add")),
        EDIT(new Translation("Изменить", "Upraviť", "Ndrysho", "Edit")),
        DELETE(new Translation("Удалить", "Odstrániť", "Fshi", "Delete")),
        SAVE(new Translation("Сохранить", "Uložiť", "Ruaj", "Save")),
        CANCEL(new Translation("Отмена", "Zrušiť", "Anulo", "Cancel")),

        READY(new Translation("Готово", "Pripravené", "Gati", "Ready")),
        EMPTY_TITLE(new Translation("Нет данных", "Žiadne údaje", "Nuk ka të dhëna", "No data")),
        EMPTY_DESCRIPTION(new Translation("Коллекция пока пустая", "Kolekcia je zatiaľ prázdna", "Koleksioni është bosh", "The collection is empty")),

        // ========================================
        // DASHBOARD TOP BAR
        // ========================================
        APP_LOGO(new Translation(
                "HumanBeing GUI", "HumanBeing GUI", "HumanBeing GUI", "HumanBeing GUI"
        )),
        LANGUAGE_BUTTON(new Translation(
                "Язык", "Jazyk", "Gjuha", "Lang"
        )),
        THEME_BUTTON_DARK(new Translation(
                "Тёмная тема", "Tmavá téma", "Tema e errët", "Dark theme"
        )),
        THEME_BUTTON_LIGHT(new Translation(
                "Светлая тема", "Svetlá téma", "Tema e lehtë", "Light theme"
        )),


        // ========================================
        // DASHBOARD CONTENT STATUS
        // ========================================
        STATUS_READY(new Translation(
                "Готово", "Pripravené", "Gati", "Ready"
        )),
        STATUS_DATA_LOADED(new Translation(
                "Данные загружены из gateway",
                "Dáta načítané z gateway",
                "Të dhënat u ngarkuan nga gateway",
                "Data loaded from gateway"
        )),
        STATUS_COMMAND_CANCELED(new Translation(
                "Команда отменена",
                "Príkaz zrušený",
                "Komanda u anulua",
                "Command canceled"
        )),
        STATUS_CONDITION_NOT_MET(new Translation(
                "Условие команды не выполнено: объект не добавлен",
                "Podmienka príkazu nesplnená: objekt nepridaný",
                "Kushti i komandës nuk u plotësua: objekti nuk u shtua",
                "Command condition not met: object not added"
        )),
        STATUS_COMMAND_DONE(new Translation(
                "Команда выполнена: добавлен объект id=",
                "Príkaz vykonaný: pridaný objekt id=",
                "Komanda u krye: u shtua objekti id=",
                "Command done: added object id="
        )),
        STATUS_CANNOT_EDIT_OTHER(new Translation(
                "Нельзя редактировать объект другого пользователя: owner=",
                "Nemožno upraviť objekt iného používateľa: owner=",
                "Nuk mund të modifikohet objekti i përdoruesit tjetër: owner=",
                "Cannot edit another user's object: owner="
        )),
        STATUS_EDIT_CANCELED(new Translation(
                "Редактирование отменено",
                "Úprava zrušená",
                "Modifikimi u anulua",
                "Edit canceled"
        )),
        STATUS_OBJECT_UPDATED(new Translation(
                "Объект изменён: id=",
                "Objekt upravený: id=",
                "Objekti u ndryshua: id=",
                "Object updated: id="
        )),
        STATUS_CANNOT_DELETE_OTHER(new Translation(
                "Нельзя удалить объект другого пользователя: owner=",
                "Nemožno vymazať objekt iného používateľa: owner=",
                "Nuk mund të fshihet objekti i përdoruesit tjetër: owner=",
                "Cannot delete another user's object: owner="
        )),
        STATUS_OBJECT_DELETED(new Translation(
                "Объект удалён: id=",
                "Objekt vymazaný: id=",
                "Objekti u fshi: id=",
                "Object deleted: id="
        )),
        STATUS_OBJECT_NOT_DELETED(new Translation(
                "Объект не был удалён",
                "Objekt nebol vymazaný",
                "Objekti nuk u fshi",
                "Object was not deleted"
        )),
        STATUS_CLEAR_DONE(new Translation(
                "Команда clear выполнена. Удалено объектов: ",
                "Príkaz clear vykonaný. Vymazané objekty: ",
                "Komanda clear u krye. Objektet e fshira: ",
                "Clear command done. Deleted objects: "
        )),
        STATUS_REMOVE_GREATER_DONE(new Translation(
                "Команда remove_greater выполнена. Удалено объектов: ",
                "Príkaz remove_greater vykonaný. Vymazané objekty: ",
                "Komanda remove_greater u krye. Objektet e fshira: ",
                "Remove greater command done. Deleted objects: "
        )),
        STATUS_DATA_REFRESHED(new Translation(
                "Данные обновлены",
                "Dáta obnovené",
                "Të dhënat u rifreskuan",
                "Data refreshed"
        )),

        // ========================================
        // FORM DIALOG
        // ========================================
        FORM_DIALOG_ADD_TITLE(new Translation(
                "Добавить объект", "Pridať objekt", "Shto objekt", "Add object"
        )),
        FORM_DIALOG_EDIT_TITLE(new Translation(
                "Редактировать объект", "Upraviť objekt", "Modifiko objektin", "Edit object"
        )),
        FORM_SAVE(new Translation(
                "Сохранить", "Uložiť", "Ruaj", "Save"
        )),
        FORM_CANCEL(new Translation(
                "Отмена", "Zrušiť", "Anulo", "Cancel"
        )),
        FORM_ERROR_TITLE(new Translation(
                "Ошибка", "Chyba", "Gabim", "Error"
        )),
        FORM_ERROR_INVALID_DATA(new Translation(
                "Некорректные данные", "Nesprávne údaje", "Të dhëna të pasakta", "Invalid data"
        )),
        FORM_FIELD_REQUIRED(new Translation(
                "Поле {0} обязательно", "Pole {0} je povinné", "Fusha {0} është e detyrueshme", "Field {0} is required"
        )),

        // Field labels
        FIELD_NAME(new Translation("name", "name", "name", "name")),
        FIELD_COORDINATES_X(new Translation("coordinates.x", "coordinates.x", "coordinates.x", "coordinates.x")),
        FIELD_COORDINATES_Y(new Translation("coordinates.y", "coordinates.y", "coordinates.y", "coordinates.y")),
        FIELD_REAL_HERO(new Translation("realHero", "realHero", "realHero", "realHero")),
        FIELD_HAS_TOOTHPICK(new Translation("hasToothpick", "hasToothpick", "hasToothpick", "hasToothpick")),
        FIELD_IMPACT_SPEED(new Translation("impactSpeed", "impactSpeed", "impactSpeed", "impactSpeed")),
        FIELD_MINUTES_OF_WAITING(new Translation("minutesOfWaiting", "minutesOfWaiting", "minutesOfWaiting", "minutesOfWaiting")),
        FIELD_WEAPON_TYPE(new Translation("weaponType", "weaponType", "weaponType", "weaponType")),
        FIELD_MOOD(new Translation("mood", "mood", "mood", "mood")),
        FIELD_CAR_NAME(new Translation("car.name", "car.name", "car.name", "car.name")),
        FIELD_CAR_COOL(new Translation("car.cool", "car.cool", "car.cool", "car.cool")),

        // ========================================
        // SECONDARY PANELS (Commands / Settings)
        // ========================================
        COMMANDS_PANEL_TITLE(new Translation(
                "Команды", "Príkazy", "Komandat", "Commands"
        )),
        COMMANDS_PANEL_DESCRIPTION(new Translation(
                "Все команды доступны через нижнюю панель действий.",
                "Všetky príkazy sú dostupné cez spodný panel akcií.",
                "Të gjitha komandat janë të disponueshme nëpërmjet panelit të poshtëm.",
                "All commands are accessible through the bottom action bar."
        )),
        COMMANDS_PANEL_EMPTY_TITLE(new Translation(
                "Команды через GUI", "Príkazy cez GUI", "Komandat nëpërmjet GUI", "Commands via GUI"
        )),
        COMMANDS_PANEL_EMPTY_DESC(new Translation(
                "Используйте кнопки ниже: добавить, редактировать, удалить, очистить мои, инфо, добавить если max, добавить если min, удалить большие, обновить.",
                "Použite tlačidlá nižšie: pridať, upraviť, vymazať, vymazať moje, info, pridať ak max, pridať ak min, odstrániť väčšie, obnoviť.",
                "Përdorni butonat më poshtë: shto, modifiko, fshi, pastro të mijat, info, shto nëse max, shto nëse min, hiq më të mëdhatë, rifresko.",
                "Use the buttons below: add, edit, delete, clear mine, info, add if max, add if min, remove greater, refresh."
        )),
        SETTINGS_PANEL_TITLE(new Translation(
                "Настройки", "Nastavenia", "Cilësimet", "Settings"
        )),
        SETTINGS_PANEL_DESCRIPTION(new Translation(
                "Язык и тема управляются в верхней панели.",
                "Jazyk a téma sa ovládajú v hornom paneli.",
                "Gjuha dhe tema kontrollohen në panelin e sipërm.",
                "Language and theme are controlled in the top bar."
        )),
        SETTINGS_PANEL_EMPTY_TITLE(new Translation(
                "Настройки", "Nastavenia", "Cilësimet", "Settings"
        )),
        SETTINGS_PANEL_EMPTY_DESC(new Translation(
                "Используйте верхнюю панель для переключения языка и темы.",
                "Použite horný panel na prepnutie jazyka a témy.",
                "Përdorni panelin e sipërm për të ndryshuar gjuhën dhe temën.",
                "Use the top bar to switch language and theme."
        )),

        // ========================================
        // MENU ITEMS
        // ========================================
        MENU_PROTOTYPE(new Translation("Прототип Lab 8", "Prototype Lab 8", "Prototip Lab 8", "Lab 8 Prototype")),
        MENU_COMPONENTS(new Translation("Система компонентов", "Systém komponentov", "Sistemi i komponentëve", "UI Component System")),
        MENU_DATA_TABLE(new Translation("Таблица данных", "Tabuľka dát", "Tabela e të dhënave", "Data Table + Streams")),
        MENU_VISUALIZATION(new Translation("Визуализация Canvas", "Vizualizácia Canvas", "Vizualizimi Canvas", "Canvas Visualization")),
        MENU_FILM(new Translation("Фильмы", "Filmy", "Filma", "Movies")),
        MENU_BROWSER(new Translation("Браузер", "Prehliadač", "Shfletuesi", "Browser")),
        MENU_TERMINAL(new Translation("Терминал", "Terminál", "Terminali", "Terminal")),
        MENU_SETTINGS(new Translation("Тема / RTL / i18n", "Téma / RTL / i18n", "Tema / RTL / i18n", "Theme / RTL / i18n")),

        // ========================================
        // PAGES
        // ========================================
        PAGE_PROTOTYPE_TITLE(new Translation("Прототип Lab 8", "Prototype Lab 8", "Prototip Lab 8", "Lab 8 Prototype")),
        PAGE_PROTOTYPE_DESCRIPTION(new Translation("Основной интерфейс с dashboard, таблицей и визуализацией.", "Hlavné rozhranie s dashboardom, tabuľkou a vizualizáciou.", "Ndërfaqja kryesore me dashboard, tabelë dhe vizualizim.", "Main interface with dashboard, table and visualization.")),

        PAGE_COMPONENTS_TITLE(new Translation("Система UI компонентов", "Systém UI komponentov", "Sistemi i komponentëve UI", "UI Component System")),
        PAGE_COMPONENTS_DESCRIPTION(new Translation("Полная витрина JavaFX-компонентов.", "Kompletná vitrína JavaFX komponentov.", "Vitrina e plotë e komponentëve JavaFX.", "Complete showcase of JavaFX components.")),

        PAGE_DATATABLE_TITLE(new Translation("Таблица данных + Streams API", "Tabuľka dát + Streams API", "Tabela e të dhënave + Streams API", "Data Table + Streams API")),
        PAGE_DATATABLE_DESCRIPTION(new Translation("Фильтрация и сортировка через Stream API.", "Filtrovanie a triedenie cez Stream API.", "Filtrimi dhe renditja nëpërmjet Stream API.", "Filtering and sorting via Stream API.")),

        PAGE_VISUALIZATION_TITLE(new Translation("Визуализация Canvas", "Vizualizácia Canvas", "Vizualizimi Canvas", "Canvas Visualization")),
        PAGE_VISUALIZATION_DESCRIPTION(new Translation("Объекты рисуются графическими примитивами.", "Objekty sa vykresľujú grafickými primitívami.", "Objektet vizatohen me primitive grafike.", "Objects are rendered as graphical primitives.")),

        PAGE_SETTINGS_TITLE(new Translation("Тема / RTL / i18n", "Téma / RTL / i18n", "Tema / RTL / i18n", "Theme / RTL / i18n")),
        PAGE_SETTINGS_DESCRIPTION(new Translation("Переключение темы, направления и языка.", "Prepínanie témy, smeru a jazyka.", "Ndërrimi i temës, drejtimit dhe gjuhës.", "Switch theme, direction and language.")),

        // ========================================
        // SHOWCASES
        // ========================================
        SHOWCASE_ALERT_TITLE(new Translation("Alert", "Alert", "Alert", "Alert")),
        SHOWCASE_ALERT_DESC(new Translation("Компонент для сообщений разных типов.", "Komponent pre rôzne typy správ.", "Komponent për mesazhe të llojeve të ndryshme.", "Component for messages of different types.")),
        SHOWCASE_BUTTON_TITLE(new Translation("Button", "Tlačidlo", "Buton", "Button")),
        SHOWCASE_BUTTON_DESC(new Translation("Варианты кнопок и размеры.", "Varianty tlačidiel a veľkosti.", "Variantet dhe madhësitë e butonave.", "Button variants and sizes.")),
        SHOWCASE_BUTTON_VARIANTS(new Translation("Варианты:", "Varianty:", "Variantet:", "Variants:")),
        SHOWCASE_BUTTON_SIZES(new Translation("Размеры:", "Veľkosti:", "Madhësitë:", "Sizes:")),
        SHOWCASE_FORM_TITLE(new Translation("Field / InputGroup / Select", "Field / InputGroup / Select", "Field / InputGroup / Select", "Field / InputGroup / Select")),
        SHOWCASE_FORM_DESC(new Translation("Компоненты для форм HumanBeing.", "Komponenty pre formuláre HumanBeing.", "Komponentë për formularët HumanBeing.", "Components for HumanBeing forms.")),
        SHOWCASE_BASIC_TITLE(new Translation("Card / Empty / Avatar / Spinner", "Card / Empty / Avatar / Spinner", "Card / Empty / Avatar / Spinner", "Card / Empty / Avatar / Spinner")),
        SHOWCASE_BASIC_DESC(new Translation("Базовые компоненты интерфейса.", "Základné komponenty rozhrania.", "Komponentët bazë të ndërfaqes.", "Basic interface components.")),
        SHOWCASE_RESIZABLE_TITLE(new Translation("Resizable / Dialog", "Resizable / Dialog", "Resizable / Dialog", "Resizable / Dialog")),
        SHOWCASE_RESIZABLE_DESC(new Translation("SplitPane и модальное окно.", "SplitPane a modálne okno.", "SplitPane dhe dritare modale.", "SplitPane and modal dialog.")),
        SHOWCASE_TABLE_TITLE(new Translation("Data Table", "Tabuľka dát", "Tabela e të dhënave", "Data Table")),
        SHOWCASE_TABLE_DESC(new Translation("Короткая таблица внутри showcase.", "Krátky tabuľka v showcase.", "Tabelë e shkurtër në showcase.", "Short table inside showcase.")),

        SHOWCASE_LEFT_PANEL(new Translation("Левая панель", "Ľavý panel", "Paneli i majtë", "Left panel")),
        SHOWCASE_LEFT_PANEL_DESC(new Translation("Например таблица объектов.", "Napríklad tabuľka objektov.", "Për shembull tabela e objekteve.", "For example object table.")),
        SHOWCASE_RIGHT_PANEL(new Translation("Правая панель", "Pravý panel", "Paneli i djathtë", "Right panel")),
        SHOWCASE_RIGHT_PANEL_DESC(new Translation("Например область визуализации.", "Napríklad oblast vizualizácie.", "Për shembull zona e vizualizimit.", "For example visualization area.")),
        SHOWCASE_OPEN_DIALOG(new Translation("Открыть Dialog", "Otvoriť Dialog", "Hap Dialog", "Open Dialog")),
        SHOWCASE_DIALOG_TITLE(new Translation("Демо Dialog", "Demo Dialog", "Demo Dialog", "Demo Dialog")),
        SHOWCASE_DIALOG_COMPONENT(new Translation("Компонент Dialog", "Komponent Dialog", "Komponenti Dialog", "Dialog component")),
        SHOWCASE_DIALOG_MSG(new Translation("Это пример модального окна.", "Toto je príklad modálneho okna.", "Ky është një shembull i dritares modale.", "This is an example of a modal window.")),
        SHOWCASE_DIALOG_CLOSE(new Translation("Закрыть", "Zavrieť", "Mbyll", "Close")),
        SHOWCASE_FOOTER_ACTION(new Translation("Действие в футере", "Akcia v pätičke", "Veprim në footer", "Footer action")),

        // ========================================
        // ALERT SHOWCASE MESSAGES
        // ========================================
        ALERT_INFO_MSG(new Translation(
                "Обычное информационное сообщение.",
                "Bežná informačná správa.",
                "Mesazh i zakonshëm informativ.",
                "A regular informational message."
        )),
        ALERT_SUCCESS_MSG(new Translation(
                "Операция выполнена успешно.",
                "Operácia bola vykonaná úspešne.",
                "Operacioni u krye me sukses.",
                "Operation completed successfully."
        )),
        ALERT_WARNING_MSG(new Translation(
                "Проверьте введённые данные.",
                "Skontrolujte zadané údaje.",
                "Kontrolloni të dhënat e futura.",
                "Please check the entered data."
        )),
        ALERT_ERROR_MSG(new Translation(
                "Произошла ошибка выполнения команды.",
                "Vyskytla sa chyba pri vykonávaní príkazu.",
                "Ndodhi një gabim gjatë ekzekutimit të komandës.",
                "An error occurred while executing the command."
        )),

        // ========================================
        // FORM / DATATABLE / VISUALIZATION
        // ========================================
        FORM_NAME_PLACEHOLDER(new Translation("Введите имя объекта", "Zadajte názov objektu", "Shkruani emrin e objektit", "Enter object name")),
        FORM_NAME_HELPER(new Translation("Поле name обязательно.", "Pole name je povinné.", "Fusha name është e detyrueshme.", "Name field is required.")),

        DATATABLE_FILTER_PLACEHOLDER(new Translation("name / owner / mood", "name / owner / mood", "name / owner / mood", "name / owner / mood")),
        DATATABLE_FILTER_LABEL(new Translation("Фильтр", "Filter", "Filtri", "Filter")),
        DATATABLE_SORT_LABEL(new Translation("Сортировка", "Triedenie", "Renditja", "Sort column")),
        DATATABLE_APPLY(new Translation("Применить фильтр/сортировку", "Použiť filter/triedenie", "Apliko filtrim/renditje", "Apply Stream filter/sort")),
        DATATABLE_RESET(new Translation("Сбросить", "Resetovať", "Rivendos", "Reset")),
        DATATABLE_REFRESH(new Translation("Обновить из gateway", "Obnoviť z gateway", "Rifresko nga gateway", "Refresh from gateway")),
        DATATABLE_CARD_TITLE(new Translation("Коллекция HumanBeing", "Kolekcia HumanBeing", "Koleksioni HumanBeing", "HumanBeing collection")),
        DATATABLE_CARD_DESC(new Translation("Каждое поле объекта показано отдельной колонкой.", "Každé pole objektu je zobrazené v samostatnom stĺpci.", "Çdo fushë e objektit shfaqet në një kolonë të veçantë.", "Each object field is shown in a separate column.")),

        OBJECTS_DETECTED_BY_RADAR(new Translation("Объекты обнаружены радаром лаб 8", "Objekty boli detegované radarom lab 8", "Objektet u zbuluan nga radari lab 8", "Objects were detected by radar lab 8")),
        VISUALIZATION_CLICK_HINT(new Translation("Кликните по объекту на canvas", "Kliknite na objekt na canvas", "Klikoni mbi objektin në canvas", "Click object on canvas")),
        VISUALIZATION_ANIMATION_TITLE(new Translation("Анимация", "Animácia", "Animacion", "Animation")),
        VISUALIZATION_ANIMATION_DESC(new Translation("При обновлении данных Canvas запускает анимацию.", "Pri aktualizácii dát Canvas spustí animáciu.", "Gjatë përditësimit të të dhënave Canvas fillon animacionin.", "Canvas triggers animation on data update.")),
        VISUALIZATION_RELOAD(new Translation("Перезагрузить", "Znovu načítať", "Rifresko", "Reload and animate")),
        VISUALIZATION_RESET_ORIGIN(new Translation("Сбросить начало координат", "Resetovat počátek", "Rikthe origjinën", "Reset origin")),
        VISUALIZATION_CARD_TITLE(new Translation("Область визуализации", "Oblasť vizualizácie", "Zona e vizualizimit", "Visualization area")),
        VISUALIZATION_CARD_DESC(new Translation("Интерактивный Canvas", "Interaktívny Canvas", "Canvas ndërveprues", "Interactive Canvas")),

        // ========================================
        // SETTINGS
        // ========================================
        SETTINGS_APPLY_THEME(new Translation("Применить тему", "Použiť tému", "Apliko temën", "Apply Theme")),
        SETTINGS_THEME_TITLE(new Translation("Тема", "Téma", "Tema", "Theme")),
        SETTINGS_THEME_DESC(new Translation("Looked-up colors через CSS-классы.", "Looked-up colors cez CSS triedy.", "Looked-up colors nëpërmjet klasave CSS.", "Looked-up colors via CSS classes.")),
        SETTINGS_THEME_LABEL(new Translation("Тема", "Téma", "Tema", "Theme")),
        SETTINGS_APPLY_DIRECTION(new Translation("Применить направление", "Použiť smer", "Apliko drejtimin", "Apply Direction")),
        SETTINGS_DIRECTION_TITLE(new Translation("RTL", "RTL", "RTL", "RTL")),
        SETTINGS_DIRECTION_DESC(new Translation("Переключение NodeOrientation.", "Prepínanie NodeOrientation.", "Ndërrimi i NodeOrientation.", "Switch NodeOrientation for the root.")),
        SETTINGS_DIRECTION_LABEL(new Translation("Направление", "Smer", "Drejtimi", "Direction")),
        SETTINGS_APPLY_LANG(new Translation("Применить язык", "Použiť jazyk", "Apliko gjuhën", "Apply Language")),
        SETTINGS_LANG_TITLE(new Translation("Язык", "Jazyk", "Gjuha", "i18n")),
        SETTINGS_LANG_DESC(new Translation("Тексты берутся из Java class Messages.", "Texty sa berú z Java triedy Messages.", "Tekstet merren nga klasa Java Messages.", "Texts are taken from Java class Messages.")),
        SETTINGS_LANG_LABEL(new Translation("Язык", "Jazyk", "Gjuha", "Language")),

        // Empty state
        EMPTY_ACTION_REFRESH(new Translation("Обновить", "Obnoviť", "Rifresko", "Refresh"));


        private final Translation translation;

        Key(Translation translation) {
            this.translation = translation;
        }
    }

    private static Lang currentLang = Lang.RU;

    public static void setLang(Lang lang) {
        currentLang = lang == null ? Lang.RU : lang;
    }

    public static Lang getCurrentLang() {
        return currentLang;
    }

    public static Locale getCurrentLocale() {
        return currentLang.locale();
    }

    public static Lang nextLang() {
        Lang[] values = Lang.values();
        return values[(currentLang.ordinal() + 1) % values.length];
    }

    public static String get(Key key) {
        return key.translation.get(currentLang);
    }
}
