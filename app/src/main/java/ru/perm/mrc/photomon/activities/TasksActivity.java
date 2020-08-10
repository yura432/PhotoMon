package ru.perm.mrc.photomon.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.perm.mrc.photomon.R;
import ru.perm.mrc.photomon.data.DBContract;
import ru.perm.mrc.photomon.data.DBHelper;
import ru.perm.mrc.photomon.server.GetFromServer;

public class TasksActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout tasks;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tasks);

        tasks = findViewById(R.id.tasks);

        dbHelper = new DBHelper(this);

        Button btnToGetTasks = findViewById(R.id.button);

        redraw();

        btnToGetTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetFromServer.getTasks(dbHelper);
                redraw();
            }
        });

    }

    private void redraw(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String [] columns = new String[]{DBContract.TasksTable._ID,DBContract.TasksTable.COLUMN_NAME};
        Cursor cursor = db.query(DBContract.TasksTable.TABLE_NAME,columns,null,null,null,null,null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tasks.removeAllViews();
        while (cursor.moveToNext()){
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.fragment_task,null);
            textView.setText(cursor.getString(cursor.getColumnIndex(DBContract.TasksTable.COLUMN_NAME)));

            textView.setId(cursor.getInt(cursor.getColumnIndex(DBContract.TasksTable._ID)));

            textView.setOnClickListener(this);

            tasks.addView(textView,layoutParams);

        }
        cursor.close();

    }


    @Override
    public void onClick(View view) {

        TextView textView = (TextView) view;

        Intent intent = new Intent(this,CategoriesActivity.class);
        intent.putExtra("taskID",view.getId());
        intent.putExtra("taskName", textView.getText());
        startActivity(intent);
    }
}
