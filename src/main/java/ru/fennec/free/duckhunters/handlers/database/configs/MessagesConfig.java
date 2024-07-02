package ru.fennec.free.duckhunters.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface MessagesConfig {

    @AnnotationBasedSorter.Order(1)
    @ConfDefault.DefaultString("&6&lDuck&c&lHunters &8»&7")
    @ConfComments("Префикс плагина можно использовать во всех строках, используемых данным плагином, с помощью заменителя ${prefix}")
    String prefix();

    @SubSection
    AdminSection adminSection();

    @SubSection
    PlayerSection playerSection();

    interface AdminSection {

        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("${prefix} &fКонфиги плагина успешно перезагружены!")
        @ConfComments("Ответ Администратору, когда конфиги плагина были успешно перезагружены")
        String configsReloadedSuccessfully();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("${prefix} &fУ Вас недостаточно прав для выполнения данной команды!")
        @ConfComments("Сообщение, если у игрока нет права duckhunters.admin.<какое-то>")
        String noPermission();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultString("${prefix} &fИнформация об игроке &a${player_name}&f успешно сброшена!")
        @ConfComments("Сообщение, когда сбрасывается информация об игроке")
        String playerReset();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("${prefix} &fИгроку &a${player_name}&f было установлено значение &a${player_statistic_selected_value}&f в поле ${player_statistic_selected_key}!")
        @ConfComments("Сообщение, когда игроку изменяется статистика")
        String playerSet();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} &cЗначение должно быть числом!")
        @ConfComments("Сообщение, когда в команде вместо числа указывается что-то другое")
        String mustBeNumber();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} &cЧисло превышает максимальное значение типа Long!")
        @ConfComments("Сообщение, когда в команде число очень большое")
        String numberIsTooLong();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина для Администраторов:",
                "  &a/duckhunters reload -&f перезагрузить конфиги плагина (не трогает БД)",
                "  &a/duckhunters player <Игрок> reset -&f сбросить статистику игроку",
                "  &a/duckhunters player <Игрок> set <Поле> <Значение> -&f установить значение игроку",
                "  &a/duckhunters player <Игрок> add <Поле> <Значение> -&f добавить значение игроку"})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();
    }

    interface PlayerSection {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("${prefix} &fДобро пожаловать! Эта игра чем-то похожа на DeathRun, но здесь нет ловушек, а у охотника - лук вместо кнопок!")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnFirstJoin();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("${prefix} &fДобро пожаловать! Готовы поймать всех уток?")
        @ConfComments("Сообщение игроку при входе на сервер. Чтобы выключить сообщение, оставьте поле пустым.")
        String notifyOnJoin();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultStrings({"${prefix} &fАктуальный список команд плагина для Игроков:",
                "  &a/duckhunters help -&f показать данный список команд",
                "  &a/duckhunters <self|me|info> -&f посмотреть информацию о себе",
                "  &a/duckhunters <Игрок> -&f посмотреть информацию об онлайн игроке",
                "  &a/duckhunters start -&f запустить обратный отсчёт игры",
                "  &a/duckhunters forcestart -&f принудительно запустить игру",})
        @ConfComments("Ответ Администратору, когда он запрашивает список команд")
        List<String> helpStrings();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("${prefix} &cКоманда доступна только игрокам!")
        @ConfComments("Сообщение, если команду использует не игрок")
        String notAPlayer();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("${prefix} &cИгрок оффлайн!")
        @ConfComments("Сообщение, если игрок оффлайн")
        String playerIsOffline();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultString("${prefix} &cВнутренняя ошибка плагина: игрок не в кэше плагина!")
        @ConfComments("Сообщение, если игрок не в кэше плагина")
        String playerNotInCache();

        //ToDo: статистика игрока в этом и нижнем поле
        @AnnotationBasedSorter.Order(10)
        @ConfDefault.DefaultString("${prefix} &fУ Вас сейчас &a${player_reputation}&f очков репутации, Ваше звание: &a${player_title}&f!")
        String selfInfo();

        @AnnotationBasedSorter.Order(11)
        @ConfDefault.DefaultString("${prefix} &fУ игрока &a${player_name}&f сейчас &a${player_reputation}&f очков репутации, его звание: &a${player_title}&f!")
        String playerInfo();

    }

}
