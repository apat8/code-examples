package com.example.notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Data Access Object (DAO) is a mapping of SQL queries to functions.
 * DAO for Reminder.
 * Reminder Operations:
 *      Insert
 *      Update
 *      Delete
 *      DeleteA11
 */

@Dao
public interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("DELETE FROM Reminder")
    void deleteAll();
}
