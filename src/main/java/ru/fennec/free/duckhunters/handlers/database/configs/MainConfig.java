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

}
