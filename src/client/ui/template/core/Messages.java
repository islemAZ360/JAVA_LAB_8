package client.ui.template.core;

import java.util.Locale;

public final class Messages {
    private Messages() {}

    public enum Lang {
        RU("Русский", "ru", "RU"),
        SK("Slovenčina", "sk", "SK"),
        SQ("Shqip", "sq", "AL"),
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

        CURRENT_USER(new Translation("Текущий пользователь", "Aktuálny používateľ", "Përdoruesi aktual", "Current user")),
        LANGUAGE(new Translation("Язык", "Jazyk", "Gjuha", "Language")),
        THEME_DARK(new Translation("Тёмная тема", "Tmavá téma", "Tema e errët", "Dark theme")),
        THEME_LIGHT(new Translation("Светлая тема", "Svetlá téma", "Tema e ndritshme", "Light theme")),
        LOGOUT(new Translation("Выйти", "Odhlásiť sa", "Dil", "Logout")),

        NAVIGATION(new Translation("Навигация", "Navigácia", "Navigimi", "Navigation")),
        COLLECTION(new Translation("Коллекция", "Kolekcia", "Koleksioni", "Collection")),
        COMMANDS(new Translation("Команды", "Príkazy", "Komandat", "Commands")),
        VISUALIZATION(new Translation("Область визуализации", "Vizualizácia", "Vizualizimi", "Visualization area")),
        SETTINGS(new Translation("Настройки", "Nastavenia", "Cilësimet", "Settings")),

        ADD(new Translation("Добавить", "Pridať", "Shto", "Add")),
        EDIT(new Translation("Изменить", "Upraviť", "Ndrysho", "Edit")),
        DELETE(new Translation("Удалить", "Odstrániť", "Fshi", "Delete")),
        REFRESH(new Translation("Обновить", "Obnoviť", "Përditëso", "Refresh")),
        SAVE(new Translation("Сохранить", "Uložiť", "Ruaj", "Save")),
        CANCEL(new Translation("Отмена", "Zrušiť", "Anulo", "Cancel")),

        CLEAR_MINE(new Translation("Очистить мои", "Vymazať moje", "Pastro të miat", "Clear mine")),
        INFO(new Translation("Информация", "Informácie", "Informacion", "Info")),
        ADD_IF_MAX(new Translation("Добавить если max", "Pridať ak max", "Shto nëse max", "Add if max")),
        ADD_IF_MIN(new Translation("Добавить если min", "Pridať ak min", "Shto nëse min", "Add if min")),
        REMOVE_GREATER(new Translation("Удалить большие", "Odstrániť väčšie", "Fshi më të mëdhatë", "Remove greater")),

        OBJECT_TABLE_TITLE(new Translation("Таблица объектов", "Tabuľka objektov", "Tabela e objekteve", "Object table")),
        OBJECT_TABLE_DESCRIPTION(new Translation(
                "Каждое поле HumanBeing — отдельная колонка",
                "Každé pole HumanBeing je samostatný stĺpec",
                "Çdo fushë HumanBeing është kolonë më vete",
                "Each HumanBeing field is a separate column"
        )),
        FILTER_PROMPT(new Translation(
                "Фильтр по name/owner/mood/weapon/car...",
                "Filter podľa name/owner/mood/weapon/car...",
                "Filtro sipas name/owner/mood/weapon/car...",
                "Filter by name/owner/mood/weapon/car..."
        )),
        APPLY_STREAM_FILTER(new Translation("Применить Stream filter", "Použiť Stream filter", "Apliko Stream filter", "Apply Stream filter")),
        RESET(new Translation("Сбросить", "Resetovať", "Rivendos", "Reset")),
        SORT_BY_NAME(new Translation("Sort by name", "Triediť podľa mena", "Rendit sipas emrit", "Sort by name")),

        VISUALIZATION_TITLE(new Translation("Область визуализации", "Oblasť vizualizácie", "Zona e vizualizimit", "Visualization area")),
        VISUALIZATION_DESCRIPTION(new Translation(
                "Цвет зависит от owner, координаты — от coordinates.x/y, размер — от impactSpeed",
                "Farba závisí od owner, súradnice od coordinates.x/y, veľkosť od impactSpeed",
                "Ngjyra varet nga owner, koordinatat nga coordinates.x/y, madhësia nga impactSpeed",
                "Color depends on owner, coordinates on coordinates.x/y, size on impactSpeed"
        )),
        CLICK_OBJECT(new Translation("Выберите объект", "Vyberte objekt", "Zgjidh objektin", "Select object")),
        CLICK_OBJECT_DESCRIPTION(new Translation(
                "При клике по объекту информация появится ниже",
                "Po kliknutí na objekt sa informácie zobrazia nižšie",
                "Pas klikimit mbi objekt, informacioni shfaqet poshtë",
                "Click an object to show information below"
        )),
        SELECT_OBJECT_HINT(new Translation(
                "Выберите объект в таблице или на Canvas",
                "Vyberte objekt v tabuľke alebo na Canvas",
                "Zgjidhni objektin në tabelë ose në Canvas",
                "Select an object in the table or on the Canvas"
        )),

        READY(new Translation("Готово", "Pripravené", "Gati", "Ready")),
        EMPTY_TITLE(new Translation("Нет данных", "Žiadne údaje", "Nuk ka të dhëna", "No data")),
        EMPTY_DESCRIPTION(new Translation("Коллекция пока пустая", "Kolekcia je zatiaľ prázdna", "Koleksioni është bosh", "The collection is empty"));

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
