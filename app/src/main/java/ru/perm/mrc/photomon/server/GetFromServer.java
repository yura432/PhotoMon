package ru.perm.mrc.photomon.server;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import ru.perm.mrc.photomon.data.DBContract;
import ru.perm.mrc.photomon.data.DBHelper;

public class GetFromServer {

    public static void getTasks(DBHelper dbHelper){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBContract.TasksTable.TABLE_NAME,null,null);

        ContentValues values = new ContentValues();
        long taskID;
        long catID;

        values.put(DBContract.TasksTable.COLUMN_NAME,"Ашан");
        values.put(DBContract.TasksTable.COLUMN_ASSIGNMENT_DATE, "AssDate");
        values.put(DBContract.TasksTable.COLUMN_DEADLINE_DATE, "DeadDate");

        taskID = db.insert(DBContract.TasksTable.TABLE_NAME,null,values);
        values.clear();

        values.put(DBContract.CategoriesTable.COLUMN_NAME, "макароны");
        values.put(DBContract.CategoriesTable.COLUMN_TASK_ID,taskID);

        catID = db.insert(DBContract.CategoriesTable.TABLE_NAME,null,values);
        values.clear();

        values.put(DBContract.CategoriesTable.COLUMN_NAME, "масло");
        values.put(DBContract.CategoriesTable.COLUMN_TASK_ID,taskID);

        catID = db.insert(DBContract.CategoriesTable.TABLE_NAME,null,values);
        values.clear();

        values.put(DBContract.CategoriesTable.COLUMN_NAME, "крупы");
        values.put(DBContract.CategoriesTable.COLUMN_TASK_ID,taskID);

        catID = db.insert(DBContract.CategoriesTable.TABLE_NAME,null,values);
        values.clear();




        values.put(DBContract.TasksTable.COLUMN_NAME,"Карусель");
        values.put(DBContract.TasksTable.COLUMN_ASSIGNMENT_DATE, "AssDate");
        values.put(DBContract.TasksTable.COLUMN_DEADLINE_DATE, "DeadDate");

        db.insert(DBContract.TasksTable.TABLE_NAME,null,values);



    }

}
