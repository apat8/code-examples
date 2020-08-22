package com.example.notes;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * ViewModel acts as a communication center between the repository (data) and UI.
 * ViewModel survives activity recreation.
 *
 */

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository mRepository;
    private LiveData<List<NoteComplete>> mAllNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.getAllNoteCompletes();
    }

    LiveData<List<NoteComplete>> getAllNoteCompletes(){
        return mAllNotes;
    }

    // Note Operations
    public long insertNote(Note note){
        return mRepository.insertNote(note);
    }
    public void deleteNote(Note note){
        mRepository.deleteNote(note);
    }
    public void updateNote(Note... notes){
        mRepository.updateNote(notes);
    }
    public Note getAnyNote(){
        return mRepository.getAnyNote();
    }
    public int getMaxOrderNum(){
        return mRepository.getMaxOrderNum();
    }

    // Reminder Operations
    public long insertReminder(Reminder reminder){
        return mRepository.insertReminder(reminder);
    }
    public void deleteReminder(Reminder reminder){
        mRepository.deleteReminder(reminder);
    }
    public void updateReminder(Reminder reminder){
        mRepository.updateReminder(reminder);
    }

    // Category Operations
    public long insertCategory(Category category){
        return mRepository.insertCategory(category);
    }
    public void deleteCategory(Category category){
        mRepository.deleteCategory(category);
    }
    public void updateCategory(Category category){
        mRepository.updateCategory(category);
    }
}
