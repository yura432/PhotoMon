package ru.perm.mrc.photomon.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import ru.perm.mrc.photomon.data.DBContract;
import ru.perm.mrc.photomon.data.DBHelper;

@Getter
public class Task {
    private final String name;
    private final int id;
    @Setter
    private boolean state;
    @Setter
    private ArrayList<Category> categories;


    public Task (DBHelper dbHelper, String name, int taskID){
        this.name = name;
        this.id = taskID;
        categories = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String [] columns = new String[]{DBContract.CategoriesTable._ID,DBContract.CategoriesTable.COLUMN_NAME};

        Cursor cursor = db.query(DBContract.CategoriesTable.TABLE_NAME,columns,DBContract.CategoriesTable.COLUMN_TASK_ID + " = ?",new String[]{Integer.toString(taskID)},null,null,null);

        int nameColIndex = cursor.getColumnIndex(DBContract.CategoriesTable.COLUMN_NAME);
        int idColIndex = cursor.getColumnIndex(DBContract.CategoriesTable._ID);

        while (cursor.moveToNext()){
            String catName = cursor.getString(nameColIndex);
            int catID = cursor.getInt(idColIndex);

            categories.add(new Category(catName,catID,taskID, dbHelper));
        }
    }
}
