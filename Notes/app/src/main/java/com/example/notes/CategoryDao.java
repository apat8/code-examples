package com.example.notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Data Access Object (DAO) is a mapping of SQL queries to functions.
 * DAO for Category.
 * Note Operations:
 *      Insert - Insert a category and returns the int
 *      Update - Update a category
 *      Delete - Delete a single category
 *      DeleteA11 - Delete all Categories
 */

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM Category")
    void deleteAll();


}
