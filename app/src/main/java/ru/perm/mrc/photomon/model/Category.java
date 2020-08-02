package ru.perm.mrc.photomon.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Category {
    private final String name;
    private final int id;
    private final int taskId;
    @Setter
    private boolean state;
    @Setter
    private ArrayList<Product> products;

    public Category(String name, int id, int taskId) {
        this.name = name;
        this.id = id;
        this.taskId = taskId;
    }
}
