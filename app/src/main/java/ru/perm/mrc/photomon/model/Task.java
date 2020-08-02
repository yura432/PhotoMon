package ru.perm.mrc.photomon.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Task {
    private final String name;
    private final int id;
    @Setter
    private boolean state;
    @Setter
    private ArrayList<Category> categories;

    public Task(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
