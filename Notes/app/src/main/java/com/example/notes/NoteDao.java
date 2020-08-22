package com.example.notes;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

/**
 * Data Access Object (DAO) is a mapping of SQL queries to functions.
 * DAO for Note.
 * Note Operations:
 *      Insert
 *      Update
 *      Delete
 *      DeleteA11
 *      GetNoteComplete - Get all notes in complete. Returns LiveData which can be observed.
 *      GetAnyNote - Gets one note to verify that a note exist
 */
@Dao
public interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Note note);

    @Update
    void update(Note... notes);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM Note")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM Note ORDER BY OrderNum DESC")
    LiveData<List<NoteComplete>> getNoteComplete();

    @Query("SELECT * FROM Note Limit 1")
    Note[] getAnyNote();

    @Query("SELECT MAX(OrderNum) FROM Note")
    int getMaxOrderNum();
}
