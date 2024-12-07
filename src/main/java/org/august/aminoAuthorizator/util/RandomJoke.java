package org.august.aminoAuthorizator.util;

import java.util.Random;

public class RandomJoke {
    private static final String[] PHRASES = {
            "§eТы как раз вовремя!",
            "§eСервер ждал только тебя!",
            "§eКто пришёл? Король!",
            "§eЛогин успешен, но это не точно.",
            "§eТы снова здесь? Отлично!",
            "§eМир стал веселее!",
            "§eСервер +1 к крутости.",
            "§eКуда без тебя?",
            "§eДобро пожаловать, герой!",
            "§eТеперь можно начинать!"
    };

    public static String getRandomPhrase() {
        return PHRASES[new Random().nextInt(PHRASES.length)];
    }
}