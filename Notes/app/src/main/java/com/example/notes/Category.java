package com.example.notes;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Category stores all the note categories that the user adds as well as a color that represents
 * that category.
 *
 * The primary key is a String that is the name of the category.
 *
 * |Name|Color|
 */

@Entity
public class Category implements Serializable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Name")
    private String mName;
    @ColumnInfo(name = "Color")
    private int mColor;

    public Category(@NonNull String name, int color) {
        this.mName = name;
        this.mColor = color;
    }

    //Getters
    public String getName() {
        return this.mName;
    }

    public int getColor() {
        return this.mColor;
    }

    //Setters
    public void setName(String name) {
        this.mName = name;
    }

    public void setColor(int color) {
        this.mColor = color;
    }
}
