package com.example.notes;

import android.app.Application;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import androidx.lifecycle.LiveData;

/**
 * Repository to handle multiple data sources.
 * Currently only one data source which is the Room database.
 *
 */

public class NoteRepository {

    // Declare all DAOs
    private NoteDao mNoteDao;
    private ReminderDao mReminderDao;
    private CategoryDao mCategoryDao;

    // Cached all notes
    private LiveData<List<NoteComplete>> mAllNotes;

    NoteRepository(Application application) {
        // Get Room database
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        // Initialize DAOs
        mNoteDao = db.noteDao();
        mReminderDao = db.reminderDao();
        mCategoryDao = db.categoryDao();
        // Get all notes
        mAllNotes = mNoteDao.getNoteComplete();
    }

    LiveData<List<NoteComplete>> getAllNoteCompletes() {
        return mAllNotes;
    }

    // Note Operations
    long insertNote(final Note note) {
        long ID = 0;
        Callable<Long> insertCallable = new Callable<Long>() {
            @Override
            public Long call() {
                return mNoteDao.insert(note);
            }
        };
        Future<Long> future = NoteRoomDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            ID = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return ID;
    }

    void deleteNote(final Note note) {
        NoteRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mNoteDao.delete(note);
            }
        });
    }

    void updateNote(final Note... notes) {
        NoteRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mNoteDao.update(notes);
            }
        });
    }

    Note getAnyNote() {
        Note anyNote = null;
        Callable<Note[]> anyNoteCallable = new Callable<Note[]>() {
            @Override
            public Note[] call() {
                return mNoteDao.getAnyNote();
            }
        };
        Future<Note[]> future = NoteRoomDatabase.databaseWriteExecutor.submit(anyNoteCallable);
        try {
            anyNote = future.get()[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return anyNote;
    }

    int getMaxOrderNum() {
        int orderNum = 0;
        Callable<Integer> getMaxOrderNumCallable = new Callable<Integer>() {
            @Override
            public Integer call() {
                return mNoteDao.getMaxOrderNum();
            }
        };
        Future<Integer> future = NoteRoomDatabase.databaseWriteExecutor.submit(getMaxOrderNumCallable);
        try {
            orderNum = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return orderNum;
    }

    // Reminder Operations
    long insertReminder(final Reminder reminder) {
        long ID = 0;
        Callable<Long> insertCallable = new Callable<Long>() {
            @Override
            public Long call() {
                return mReminderDao.insert(reminder);
            }
        };
        Future<Long> future = NoteRoomDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            ID = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return ID;
    }

    void deleteReminder(final Reminder reminder) {
        NoteRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mReminderDao.delete(reminder);
            }
        });
    }

    void updateReminder(final Reminder reminder) {
        NoteRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mReminderDao.update(reminder);
            }
        });
    }

    // Category Operations
    long insertCategory(final Category category) {
        long ID = 0;
        Callable<Long> insertCallable = new Callable<Long>() {
            @Override
            public Long call() {
                return mCategoryDao.insert(category);
            }
        };
        Future<Long> future = NoteRoomDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            ID = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return ID;
    }

    void deleteCategory(final Category category) {
        NoteRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCategoryDao.delete(category);
            }
        });
    }

    void updateCategory(final Category category) {
        NoteRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCategoryDao.update(category);
            }
        });
    }
}
