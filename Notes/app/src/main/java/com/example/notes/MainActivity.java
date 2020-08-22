package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Main Activity
 * Activity lists all notes in recycler view.
 * A floating action button is used to add new notes.
 */
public class MainActivity extends AppCompatActivity {

    // Constants for passing Note and boolean to EditNoteActivity
    public static final String EXTRA_NOTE = "com.example.notes.NOTE";
    public static final String EXTRA_IS_NEW_NOTE = "com.example.notes.IS_NEW_NOTE";

    private NoteViewModel mNoteViewModel;
    private Set<Note> mChangedOrderNumNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView emptyListTextView = findViewById(R.id.emptyListText);

        // Used to store modified notes where order numbers are changed
        mChangedOrderNumNotes = new HashSet<>();

        // Initialize recycler view and note list adapter
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final NoteListAdapter adapter = new NoteListAdapter(this);

        // Set the adapter to the recycler view
        // Set layout of recycler view to a vertical linear layout
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize note view model
        mNoteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Observe the getAllNotes operation which returns LiveData
        // Update cached notes in the adapter when notes are modified in database
        mNoteViewModel.getAllNoteCompletes().observe(this, new Observer<List<NoteComplete>>() {
            @Override
            public void onChanged(List<NoteComplete> noteCompletes) {
                // If no notes, then display text view otherwise display recycler view
                if(noteCompletes.isEmpty()){
                    emptyListTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    emptyListTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setNotes(noteCompletes);
                }
            }
        });

        // Initialize floating action button and set onclick listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert(view);
            }
        });

        // Delete note by swipe
        // Move note and update order number
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP |
                ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                // Swap from nNotes and update the order numbers then when item is dropped update database
                // Swap moved notes
                Collections.swap(adapter.getNotes(),from, to);

                // Get order numbers of swapped notes
                int orderNum1 = adapter.getNoteAtPosition(from).note.getOrderNum();
                int orderNum2 = adapter.getNoteAtPosition(to).note.getOrderNum();

                // Swap order numbers
                adapter.getNoteAtPosition(from).note.setOrderNum(orderNum2);
                adapter.getNoteAtPosition(to).note.setOrderNum(orderNum1);

                // Add to list of changed notes to keep of track of changed notes to update in database
                // If same note is added, remove the old note and add the new one
                if(!mChangedOrderNumNotes.add(adapter.getNoteAtPosition(from).note)){
                    mChangedOrderNumNotes.remove(adapter.getNoteAtPosition(from).note);
                    mChangedOrderNumNotes.add(adapter.getNoteAtPosition(from).note);
                }
                if(!mChangedOrderNumNotes.add(adapter.getNoteAtPosition(to).note)){
                    mChangedOrderNumNotes.remove(adapter.getNoteAtPosition(to).note);
                    mChangedOrderNumNotes.add(adapter.getNoteAtPosition(to).note);
                }

                // Notify adapter that items are moved
                adapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // Update changed notes in database when item dropped.
                mNoteViewModel.updateNote(mChangedOrderNumNotes.toArray(new Note[mChangedOrderNumNotes.size()]));
                // Clear moved notes list as the notes have been updated
                mChangedOrderNumNotes.clear();
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get position of note from list
                int position = viewHolder.getAdapterPosition();
                // Get NoteComplete object from list
                NoteComplete noteCompleteToDelete = adapter.getNoteAtPosition(position);
                // Delete note from database
                mNoteViewModel.deleteNote(noteCompleteToDelete.note);
                // Notify Recycler view to animate deletion
                adapter.notifyItemRemoved(position);

                // Update order numbers
                for(int pos = 0; pos < position; pos++){
                    NoteComplete noteComplete = adapter.getNoteAtPosition(pos);
                    int newOrderNum = noteComplete.note.getOrderNum() -1;
                    noteComplete.note.setOrderNum(newOrderNum);
                    mChangedOrderNumNotes.add(noteComplete.note);
                }
                mNoteViewModel.updateNote(mChangedOrderNumNotes.toArray(new Note[mChangedOrderNumNotes.size()]));
                // Clear moved notes list as the notes have been updated
                mChangedOrderNumNotes.clear();

            }
        });

        // Attach item touch helper to recycler view
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Insert a new note
     * Create a new note and passes it to the EditNoteActivity
     * @param view View that was clicked
     */
    public void insert(View view) {
        // Create new NoteComplete with blank note and default Category
        // Set the new order number of the note
        int orderNum = mNoteViewModel.getMaxOrderNum() + 1;
        NoteComplete noteComplete = new NoteComplete();
        noteComplete.note.setOrderNum(orderNum);

        // Start activity with new note and boolean indicating that this is a new note
        // needed for insert operation instead of update
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(MainActivity.EXTRA_NOTE, noteComplete);
        intent.putExtra(EXTRA_IS_NEW_NOTE, true);
        startActivity(intent);

    }
}