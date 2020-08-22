package com.example.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Note List Adapter
 * Adapter is used to connect data to the recycler view.
 * NoteListAdapter inserts and updates notes in the views.
 *
 * NoteViewHolder contains the View information for displaying one item from the item's list.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    /**
     * The ViewHolder is the layout of one item in the list.
     * The NoteViewHolder has textviews for title and body.
     */
    class NoteViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleItemView;
        private final TextView bodyItemView;

        private NoteViewHolder(View itemView){
            super(itemView);
            titleItemView = itemView.findViewById(R.id.titleTextview);
            bodyItemView = itemView.findViewById(R.id.bodyTextview);

            // onClickListener for each cardview
            // Add note and a boolean indicating that this is not a new note
            // In this case, the note will be updated vs being inserted
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EditNoteActivity.class);
                    intent.putExtra(MainActivity.EXTRA_NOTE, mNotes.get(getAdapterPosition()));
                    intent.putExtra(MainActivity.EXTRA_IS_NEW_NOTE, false);
                    mContext.startActivity(intent);

                }
            });
        }
    }

    private final LayoutInflater mInflater;
    private List<NoteComplete> mNotes; // Cached copy of notes
    private Context mContext;

    NoteListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    /**
     * Initializes the view holder.
     * Layout (recyclerview_item) is inflated and a NoteViewHolder is returned.
     * @param parent parent ViewGroup
     * @param viewType type of view
     * @return NoteViewHolder with recyclerview_item layout
     */
    @NonNull
    @Override
    public NoteListAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new NoteViewHolder(itemView);
    }

    /**
     * The data is bound to the ViewHolder.
     * The note is obtained from the Note list by the position and the title and body are set
     * on the ViewHolder.
     * @param holder NoteViewHolder
     * @param position  the position of the view in the list
     */
    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.NoteViewHolder holder, int position) {
        if(mNotes != null){
            NoteComplete note = mNotes.get(position);
            holder.titleItemView.setText(note.note.getTitle());
            holder.bodyItemView.setText(note.note.getBody());
        }
        else{
            holder.titleItemView.setText(R.string.no_notes);
            holder.bodyItemView.setText("");
        }
    }

    /**
     * Gets the number of notes in the notes list.
     * @return mNotes.size()
     */
    @Override
    public int getItemCount() {
        if(mNotes != null){
            return mNotes.size();
        }
        else return 0;
    }

    /**
     * Sets the updated lists of notes and notifies the adapter that data has changed
     * @param notes list of complete notes
     */
    public void setNotes(List<NoteComplete> notes){
        mNotes = notes;
        notifyDataSetChanged();
    }

    /**
     * Get a list of note completes
     * @return List of NoteCompletes
     */
    public List<NoteComplete> getNotes(){
        return mNotes;
    }

    /**
     * Get NoteComplete from position in the list.
     * @param position  position of the note to get in the list
     * @return NoteComplete
     */
    public NoteComplete getNoteAtPosition(int position){
        return mNotes.get(position);
    }
}
