package com.example.notes;

import android.content.Context;
import android.graphics.Color;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Main Room database serves as an access point to the underlying SQLite database.
 * Room database uses DAO to issue queries to the SQLite database.
 *
 * NoteRoomDatabase uses the singleton pattern to ensure only one instance of the database is
 * performs database actions.
 *
 * The database is pre-populated with Category "NONE" with the color black using a callback.
 *
 * Tables:
 *      Note
 *      Reminder
 *      Category
 *
 * TypeConverters:
 *      DateTimeConverter
 *      ReminderFrequencyConverter
 *
 */

@Database(entities = {Note.class, Reminder.class, Category.class}, version = 1, exportSchema = false)
@TypeConverters({DateTimeConverter.class, ReminderFrequencyConverter.class})
public abstract class NoteRoomDatabase extends RoomDatabase {

    // Declare DAOs
    public abstract NoteDao noteDao();
    public abstract ReminderDao reminderDao();
    public abstract CategoryDao categoryDao();

    // Room database instance
    private static volatile NoteRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 10;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static NoteRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (NoteRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteRoomDatabase.class, "note_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sPrepopulateDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Prepopulate database with:
    //      Category("NONE", Color.Black)
    private static RoomDatabase.Callback sPrepopulateDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    Category noneCategory = new Category("NONE", Color.BLACK);
                    CategoryDao categoryDao = INSTANCE.categoryDao();
                    categoryDao.insert(noneCategory);
//                    long id = catDao.insert(category);
//                    NoteDao noteDao = INSTANCE.noteDao();
//                    noteDao.deleteAll();
//                    ReminderDao reminderDao = INSTANCE.reminderDao();
//                    reminderDao.deleteAll();
//                    Note note = new Note("Title", "Body");
//                    note.setCategory(id);
//                    Reminder reminder = new Reminder(3, new Date(), ReminderFrequency.DAILY);
//                    noteDao.insert(note);
//                    reminderDao.insert(reminder);
                }
            });
        }
    };

}
