package com.example.notes;

import android.graphics.Color;

import java.io.Serializable;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * NoteComplete represents a "complete note" which bundles the Note, the related Reminder and Category.
 * The returned Reminder's NoteID must match the ID from Note.
 * The Category from Note must match Name from Category.
 */

public class NoteComplete implements Serializable {
    @Embedded
    Note note;
    @Relation(
            parentColumn = "ID",
            entityColumn = "NoteID"
    )
    Reminder reminder;

    @Relation(
            parentColumn = "Category",
            entityColumn = "Name"
    )
    Category category;

    NoteComplete(){
        this.note = new Note();
        this.category = new Category("NONE", Color.BLACK);
    }
}
