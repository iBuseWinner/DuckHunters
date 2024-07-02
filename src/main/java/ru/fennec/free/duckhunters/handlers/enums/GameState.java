package ru.fennec.free.duckhunters.handlers.enums;

public enum GameState {
    NOT_LOADED, //Арена ещё загружена
    LOADING, //Арена загружается
    WAITING, //Ожидание игроков
    STARTING, //Обратный отсчет
    PLAYING, //Процесс игры
    ENDED //Игра окончена
}
