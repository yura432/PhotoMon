package ru.perm.mrc.photomon.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import ru.perm.mrc.photomon.data.DBContract;
import ru.perm.mrc.photomon.data.DBHelper;

@Getter
public class Category {
    private final String name;
    private final int id;
    private final int taskId;
    private final DBHelper dbHelper;
    @Setter
    private boolean state;
    @Setter
    private boolean isOpen = false;
    @Setter
    private ArrayList<Product> products;

    public Category(String name, int catID, int taskId, DBHelper dbHelper) {
        this.name = name;
        this.id = catID;
        this.taskId = taskId;
        this.dbHelper = dbHelper;

        products = new ArrayList<>();

        String [] columns = new String[]{DBContract.ProductsTable._ID,
                DBContract.ProductsTable.COLUMN_COMMENT,
                DBContract.ProductsTable.COLUMN_PHOTO_FILENAME,
                DBContract.ProductsTable.COLUMN_NAME
        };

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBContract.ProductsTable.TABLE_NAME,columns,DBContract.ProductsTable.COLUMN_CATEGORY_ID + " = ?",new String[]{Integer.toString(catID)},null,null,null);

        int idColIndex = cursor.getColumnIndex(DBContract.ProductsTable._ID);
        int commentColIndex = cursor.getColumnIndex(DBContract.ProductsTable.COLUMN_COMMENT);
        int filenameColIndex = cursor.getColumnIndex(DBContract.ProductsTable.COLUMN_PHOTO_FILENAME);
        int nameColIndex = cursor.getColumnIndex(DBContract.ProductsTable.COLUMN_NAME);

        while (cursor.moveToNext()){
            int productID = cursor.getInt(idColIndex);
            String productComment = cursor.getString(commentColIndex);
            String productFilename = cursor.getString(filenameColIndex);
            String productName = cursor.getString(nameColIndex);

            products.add(new Product(productName,productID,catID,productComment,productFilename, dbHelper));
        }
    }
}
