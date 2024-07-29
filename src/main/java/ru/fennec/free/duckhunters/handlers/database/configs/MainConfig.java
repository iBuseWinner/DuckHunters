package ru.fennec.free.duckhunters.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface MainConfig {

    @AnnotationBasedSorter.Order(1)
    @SubSection
    DatabaseSection database();

    interface DatabaseSection {
        @AnnotationBasedSorter.Order(1)
        @ConfDefault.DefaultString("SQL")
        @ConfComments("Тип хранения данных репутации игроков. SQL - локально в папке плагина; MYSQL - удалённая база данных")
        DatabaseType type();

        @AnnotationBasedSorter.Order(2)
        @ConfDefault.DefaultString("localhost:3306")
        String url();

        @AnnotationBasedSorter.Order(3)
        @ConfDefault.DefaultString("root")
        String username();

        @AnnotationBasedSorter.Order(4)
        @ConfDefault.DefaultString("")
        String password();

        @AnnotationBasedSorter.Order(5)
        @ConfDefault.DefaultString("duckhunters")
        String database();

        @AnnotationBasedSorter.Order(6)
        @ConfDefault.DefaultString("duckhunters")
        String tableName();

        @AnnotationBasedSorter.Order(9)
        @ConfDefault.DefaultString("?autoReconnect=true")
        String args();

        enum DatabaseType {
            SQL, //Локальная база данных (В папке плагина)
            MYSQL //Удалённая база данных (Без создания отдельных файлов)
        }
    }

    @AnnotationBasedSorter.Order(2)
    @ConfComments("В этой папке (она внутри папки плагина DuckHunters) должны быть все миры, которые вы хотите использовать для карт")
    @ConfDefault.DefaultString("worlds")
    String worldsDir();

    @AnnotationBasedSorter.Order(3)
    @ConfComments("Это мир limbo. Туда игроки перемещаются во время загрузки новой арены (случайно выбирается из всего списка арен (папка /plugins/DuckHunters/worlds")
    @ConfDefault.DefaultString("limbo")
    String limboWorld();

    @AnnotationBasedSorter.Order(4)
    @ConfComments("Команды, которые будут выполняться от имени консоли, по отношению к победителям игры")
    @ConfDefault.DefaultStrings({"say ${player_name} is chipi chipi bro", "tell ${player_name} u won"})
    List<String> winnersCommand();

    @AnnotationBasedSorter.Order(100)
    @SubSection
    Arena arena();

    interface Arena {
        @AnnotationBasedSorter.Order(1)
        @ConfComments("Время обратного отсчёта")
        @ConfDefault.DefaultInteger(10)
        int startTime();

        @AnnotationBasedSorter.Order(2)
        @ConfComments("Время игры")
        @ConfDefault.DefaultInteger(900)
        int playTime();

        @AnnotationBasedSorter.Order(3)
        @ConfComments("Время игры")
        @ConfDefault.DefaultString("schematics")
        String schematicPath();

        @AnnotationBasedSorter.Order(4)
        @ConfComments("Время игры")
        @ConfDefault.DefaultString("BLOCK_NOTE_BLOCK_PLING")
        String countdownSoundName();

        @AnnotationBasedSorter.Order(5)
        @ConfComments("Время игры")
        @ConfDefault.DefaultDouble(1)
        float countdownSoundVolume();

        @AnnotationBasedSorter.Order(6)
        @ConfComments("Время игры")
        @ConfDefault.DefaultDouble(1)
        float countdownSoundPitch();
    }

}
