package ru.perm.mrc.photomon.model;

import java.io.File;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import ru.perm.mrc.photomon.data.DBHelper;

@Getter
public class Product {
    private String name;
    private final String comment;
    private final String photoFilename;
    private final int id;
    private final int categoryId;
    private final DBHelper dbHelper;
    @Setter
    private boolean state;
    @Setter
    private ArrayList<File> files;

    public Product(String name, int id, int categoryId, String comment, String photoFilename, DBHelper dbHelper) {
        this.name = name;
        this.id = id;
        this.categoryId = categoryId;
        this.comment = comment;
        this.photoFilename = photoFilename;
        this.dbHelper = dbHelper;
    }
}
