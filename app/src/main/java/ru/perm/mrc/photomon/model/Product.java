package ru.perm.mrc.photomon.model;

import java.io.File;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Product {
    private String name;
    private final String comment;
    private final String photoFilename;
    private final int id;
    private final int categoryId;
    @Setter
    private boolean state;
    @Setter
    private ArrayList<File> files;

    public Product(String name, int id, int categoryId, String comment, String photoFilename) {
        this.name = name;
        this.id = id;
        this.categoryId = categoryId;
        this.comment = comment;
        this.photoFilename = photoFilename;
    }
}
