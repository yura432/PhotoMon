package ru.perm.mrc.photomon.data;

import android.provider.BaseColumns;

public final class DBContract {

    public static final class TasksTable implements BaseColumns {
        public static final String TABLE_NAME = "Tasks";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_ASSIGNMENT_DATE = "AssDate";
        public static final String COLUMN_DEADLINE_DATE = "DeadDate";
        public static final String COLUMN_STATE = "State";

        public static final int STATE_DONE = 0;
        public static final int STATE_IN_PROGRESS = 1;
        public static final int STATE_NOT_STARTED = 2;
    }

    public static final class CategoriesTable implements BaseColumns {
        public static final String TABLE_NAME = "Categories";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_TASK_ID = "TaskID";
        public static final String COLUMN_STATE = "State";

        public static final int STATE_DONE = 0;
        public static final int STATE_IN_PROGRESS = 1;
        public static final int STATE_NOT_STARTED = 2;
    }

    public static final class ProductsTable implements BaseColumns {
        public static final String TABLE_NAME = "Products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_COMMENT = "Comment";
        public static final String COLUMN_CATEGORY_ID = "CategoryID";
        public static final String COLUMN_PHOTO_FILENAME = "PhotoFilename";
        public static final String COLUMN_STATE = "State";

        public static final int STATE_DONE = 0;
        public static final int STATE_NO_PRODUCT = 1;
        public static final int STATE_NOT_STARTED = 2;
    }


}
