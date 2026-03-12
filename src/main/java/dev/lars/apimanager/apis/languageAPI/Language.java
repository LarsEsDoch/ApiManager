package dev.lars.apimanager.apis.languageAPI;

import java.util.Arrays;
import java.util.Optional;

public enum Language {
    ENGLISH(1),
    GERMAN(2),
    FRENCH(3),
    SPANISH(4),
    PORTUGUESE(5),
    ITALIAN(6),
    DUTCH(7),
    POLISH(8),
    RUSSIAN(9),
    UKRAINIAN(10),
    TURKISH(11),
    ARABIC(12),
    CHINESE_SIMPLIFIED(13),
    CHINESE_TRADITIONAL(14),
    JAPANESE(15),
    KOREAN(16),
    HINDI(17),
    SWEDISH(18),
    NORWEGIAN(19),
    DANISH(20),
    FINNISH(21),
    CZECH(22),
    SLOVAK(23),
    HUNGARIAN(24),
    ROMANIAN(25),
    GREEK(26);

    private final int id;

    Language(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Optional<Language> fromId(int id) {
        return Arrays.stream(values())
                .filter(lang -> lang.id == id)
                .findFirst();
    }
}