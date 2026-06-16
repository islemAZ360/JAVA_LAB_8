package client.ui.template.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LangEventBus {
    private LangEventBus() {}

    private static final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public static void setLang(Messages.Lang lang) {
        Messages.setLang(lang);
        listeners.forEach(Runnable::run);
    }

    public static void subscribe(Runnable onLangChange) {
        listeners.add(onLangChange);
    }

    public static void unsubscribe(Runnable onLangChange) {
        listeners.remove(onLangChange);
    }
}
