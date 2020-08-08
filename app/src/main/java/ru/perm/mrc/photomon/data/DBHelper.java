package ru.perm.mrc.photomon.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import ru.perm.mrc.photomon.data.DBContract.*;


public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "task.db";

    private static int version = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + TasksTable.TABLE_NAME + " ("
                + TasksTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TasksTable.COLUMN_NAME + " TEXT NOT NULL, "
                + TasksTable.COLUMN_ASSIGNMENT_DATE + " TEXT NOT NULL, "
                + TasksTable.COLUMN_DEADLINE_DATE + " TEXT NOT NULL, "
                + TasksTable.COLUMN_STATE + " INTEGER NOT NULL DEFAULT " + TasksTable.STATE_NOT_STARTED +");";

        String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoriesTable.TABLE_NAME + " ("
                + CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CategoriesTable.COLUMN_NAME + " TEXT NOT NULL, "
                + CategoriesTable.COLUMN_TASK_ID + " INTEGER, "
                + CategoriesTable.COLUMN_STATE + " INTEGER NOT NULL DEFAULT " + CategoriesTable.STATE_NOT_STARTED + ","
                + " FOREIGN KEY (" + CategoriesTable.COLUMN_TASK_ID + ") REFERENCES " + TasksTable.TABLE_NAME + "(" +TasksTable._ID + "));";

        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsTable.TABLE_NAME + " ("
                + ProductsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsTable.COLUMN_NAME + " TEXT NOT NULL, "
                + ProductsTable.COLUMN_COMMENT + " TEXT NOT NULL, "
                + ProductsTable.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, "
                + ProductsTable.COLUMN_PHOTO_FILENAME + " TEXT NOT NULL, "

                + ProductsTable.COLUMN_STATE + " INTEGER NOT NULL DEFAULT " + ProductsTable.STATE_NOT_STARTED + ","
                + " FOREIGN KEY (" + ProductsTable.COLUMN_CATEGORY_ID + ") REFERENCES " + CategoriesTable.TABLE_NAME + "(" +CategoriesTable._ID +"));";

        sqLiteDatabase.beginTransaction();

        try {

            sqLiteDatabase.execSQL(SQL_CREATE_TASKS_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_CATEGORIES_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
            sqLiteDatabase.setTransactionSuccessful();
        }
        catch (SQLException e){

        }
        finally {
            sqLiteDatabase.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
