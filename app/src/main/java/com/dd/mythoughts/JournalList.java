package com.dd.mythoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.mythoughts.model.Journal;
import com.dd.mythoughts.ui.JournalRecyclerView;
import com.dd.mythoughts.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "JournalList Activity";
    private FirebaseAuth firebaseAuth;
    //private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journals");
    private RecyclerView recyclerView;
    private JournalRecyclerView journalRecyclerView;
    private List<Journal> journalList;
    private TextView noThoughtsFound;
    private ProgressBar progressBar_list;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#8E2DE2"));

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        journalList = new ArrayList<>();

        progressBar_list = findViewById(R.id.progressBar_list);
        progressBar_list.setVisibility(View.VISIBLE);
        noThoughtsFound = findViewById(R.id.noThoughtsText);
        fab = findViewById(R.id.fab_add);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout_list);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(JournalList.this,AddThought.class));
                }
            }
        });
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return super.onCreateOptionsMenu(menu);
//
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_add :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(JournalList.this,AddThought.class));
                }
                break;

            case R.id.action_signout :
                if(user!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalList.this,MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //progressBar_list.setVisibility(View.VISIBLE);
        //Log.d(TAG, "onStart: "+ user.getUid());
        collectionReference.whereEqualTo("usersId", user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots!=null) {

                            for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                                Journal journal = journals.toObject(Journal.class);
                                //Log.d(TAG, "onSuccess: "+journal.getTitle());
                                journalList.add(journal);
                            }
                            progressBar_list.setVisibility(View.INVISIBLE);
                            journalRecyclerView = new JournalRecyclerView(JournalList.this,journalList);
                            recyclerView.setAdapter(journalRecyclerView);
                            journalRecyclerView.notifyDataSetChanged();

                        } else {
                            noThoughtsFound.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JournalList.this,"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        journalList.clear();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            drawerLayout.closeDrawers();

            switch (item.getItemId()) {

                case R.id.action_home_drawer :
                    if(user!=null && firebaseAuth!=null) {
                        journalList.clear();
                        startActivity(new Intent(JournalList.this,JournalList.class));
                        finish();
                    } return true;

                case R.id.action_add_drawer :
                    if(user!=null && firebaseAuth!=null) {
                        startActivity(new Intent(JournalList.this,AddThought.class));
                    } return true;

                case R.id.action_privacy_drawer :
                    if(user!=null && firebaseAuth!=null) {
                        startActivity(new Intent(JournalList.this,Privacy.class));
                        finish();
                    } return true;

                case R.id.action_about_drawer :
                    if(user!=null && firebaseAuth!=null) {
                        startActivity(new Intent(JournalList.this,About.class));
                        finish();
                    } return true;

                case R.id.action_signout_drawer :
                    if(user!=null && firebaseAuth!=null) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(JournalList.this,MainActivity.class));
                        finish();
                    } return true;


                default : return true;
            }

    }
}