package com.app.bomnadador.DataBase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Statistic.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    private static DataBase INSTANCE;

    public abstract StatisticDAO estatisticasExamesDao();

    public static DataBase getDataBase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DataBase.class, "statistic-db")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}