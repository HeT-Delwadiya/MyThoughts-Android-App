package com.dd.mythoughts.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.dd.mythoughts.R;
import com.dd.mythoughts.model.Journal;
import com.dd.mythoughts.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;



public class JournalRecyclerView extends RecyclerView.Adapter<JournalRecyclerView.ViewHolder> {

    private Context context;
    private List<Journal> journalList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journals");

    public JournalRecyclerView(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journal_row,parent,false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        holder.title_list.setText(journal.getTitle());
        holder.thought_list.setText(journal.getThoughts());
        holder.dateAdded_list.setText(DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()*1000).toString());
        holder.username_list.setText(journal.getUserName());
        //Image download and show - picasso
        Picasso.get()
                .load(journal.getImageUrl())
                .fit()
                .placeholder(R.drawable.b)
                .into(holder.imageView_list);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView_list, share_list, delete_list;
        public TextView title_list, thought_list, dateAdded_list, username_list;
        public String userId, username;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            imageView_list = itemView.findViewById(R.id.imageView_list);
            title_list = itemView.findViewById(R.id.title_list);
            thought_list = itemView.findViewById(R.id.thought_list);
            dateAdded_list = itemView.findViewById(R.id.dateAdded_list);
            username_list = itemView.findViewById(R.id.username_list);
            share_list = itemView.findViewById(R.id.share_list);
            delete_list = itemView.findViewById(R.id.delete_list);

            delete_list.setOnClickListener(this);
            share_list.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.share_list :
                        shareBtn();
                    break;

                case R.id.delete_list :
                    deleteBtn();
                    break;
            }

        }

        private void shareBtn() {

            //TODO: set share btn functional
            int position = getAdapterPosition();
            Journal journal = journalList.get(position);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Title: "+journal.getTitle() + "\n\n" + "Thought: "+journal.getThoughts() + "\n\n" + "Want to write your thoughts? Download MyThought app from PlayStore");
            context.startActivity(Intent.createChooser(intent,"Share Thought..."));
        }

        private void deleteBtn() {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Thought")
                    .setMessage("Are you sure you want to delete this thought?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            int position = getAdapterPosition();
                            Journal journal = journalList.get(position);
                            //Log.d(TAG, "deleteBtn: "+journal.getTitle());
                            collectionReference.whereEqualTo("title",""+journal.getTitle()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                    }
                                    if(!value.isEmpty()) {
                                        for (QueryDocumentSnapshot v : value) {
                                            v.getReference().delete();
                                        }
                                    }
                                }
                            });
                            journalList.remove(position);
                            notifyDataSetChanged();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(R.string.no, null)
                    .setIcon(R.drawable.ic_baseline_error_24)
                    .show();

        }

    }
}