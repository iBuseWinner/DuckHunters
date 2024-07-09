package ru.fennec.free.duckhunters.handlers.database.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

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

    @AnnotationBasedSorter.Order(1)
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
    }

}
