package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class EditNoteActivity extends AppCompatActivity implements ReminderDialog.ReminderDialogListener {

    // Declare edit text views
    private EditText mTitleEditText;
    private EditText mBodyEditText;

    // Declare NoteComplete and boolean from intent
    private NoteComplete mNoteComplete;
    private boolean mIsNewNote;

    // Declare view model to access database operations
    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Initialize title and body edit text views
        mTitleEditText = findViewById(R.id.titleEditText);
        mBodyEditText = findViewById(R.id.bodyEditText);

        // Initialize note view model
        mNoteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Get the note data if passed in and set the title and body text views
        Intent intent = getIntent();
        mIsNewNote = intent.getBooleanExtra(MainActivity.EXTRA_IS_NEW_NOTE, false);
        mNoteComplete = (NoteComplete) intent.getSerializableExtra(MainActivity.EXTRA_NOTE);
        if(mNoteComplete != null){
            mTitleEditText.setText(mNoteComplete.note.getTitle());
            mBodyEditText.setText(mNoteComplete.note.getBody());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Get edited string from edit views
        String editedTitle = mTitleEditText.getText().toString();
        String editedBody = mBodyEditText.getText().toString();

        // Check if note is being inserted (a new note) or updated (existing note)
        if(mIsNewNote){
            // If the title, body and reminder are empty then discard note otherwise insert note
            if(editedTitle.equals("") && editedBody.equals("") && mNoteComplete.reminder == null){
                Toast.makeText(this, "Empty Note Deleted", Toast.LENGTH_SHORT).show();
            }
            else{
                mNoteComplete.note.setTitle(editedTitle);
                mNoteComplete.note.setBody(editedBody);
                mNoteComplete.note.setLastEdited(new Date());
                mNoteViewModel.insertNote(mNoteComplete.note);
            }
        }else{
            // If the title or body is different from the unedited note, then update note
            if(!editedTitle.equals(mNoteComplete.note.getTitle()) || !editedBody.equals(mNoteComplete.note.getBody())){
                mNoteComplete.note.setTitle(editedTitle);
                mNoteComplete.note.setBody(editedBody);
                mNoteComplete.note.setLastEdited(new Date());
                mNoteViewModel.updateNote(mNoteComplete.note);
            }
        }
    }

    /**
     * Open the reminder dialog to set a reminder
     * @param view Button that was clicked
     */
    public void openReminderDialog(View view) {
        DialogFragment reminderDialog = new ReminderDialog();

        if(mNoteComplete.reminder != null){
            Bundle args = new Bundle();
            args.putSerializable("Date", mNoteComplete.reminder);
            reminderDialog.setArguments(args);
        }

        reminderDialog.show(getSupportFragmentManager(), "ReminderDialog");
    }


    /**
     * The dialog fragment receives a reference to this Activity through the
     * Fragment.onAttach() callback, which it uses to call the following methods
     * defined by the ReminderDialog.ReminderDialogListener interface
     * @param reminderDate Reminder Date
     */
    @Override
    public void onDialogPositiveClick(Date reminderDate) {
        if(mNoteComplete.reminder == null){
            Reminder reminder = new Reminder(mNoteComplete.note.getID(), reminderDate, ReminderFrequency.DAILY);
            mNoteViewModel.insertReminder(reminder);
        }
        else{
            mNoteComplete.reminder.setDateTime(reminderDate);
            mNoteViewModel.updateReminder(mNoteComplete.reminder);
        }
    }

    /**
     * The dialog fragment receives a reference to this Activity through the
     * Fragment.onAttach() callback, which it uses to call the following methods
     * defined by the NoticeDialogFragment.NoticeDialogListener interface
     */
    @Override
    public void onDialogNegativeClick() {
        mNoteViewModel.deleteReminder(mNoteComplete.reminder);
        mNoteComplete.reminder = null;
    }
}