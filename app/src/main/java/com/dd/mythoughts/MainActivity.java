package com.dd.mythoughts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dd.mythoughts.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private Button getStarted;

//    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener authStateListener;
//    private FirebaseUser currentUser;
//
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getStarted = findViewById(R.id.getStarted);

        //firebaseAuth = FirebaseAuth.getInstance();

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                currentUser = firebaseAuth.getCurrentUser();
//                if (currentUser!=null) {
//                    currentUser = firebaseAuth.getCurrentUser();
//                    String currentUsersId = currentUser.getUid();
//
//                    collectionReference.whereEqualTo("userId",currentUsersId)
//                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                    if (error != null) {
//                                        return;
//                                    }
//                                    String name;
//                                    if (value!=null) {
//                                        for (QueryDocumentSnapshot data : value) {
//                                            JournalApi journalApi = JournalApi.getInstance();
//                                            journalApi.setUsersId(data.getString("userId"));
//                                            journalApi.setUsername(data.getString("username"));
//
//                                            startActivity(new Intent(MainActivity.this,JournalList.class));
//                                            finish();
//                                        }
//                                    }
//                                }
//                            });
//
//                } else {
//
//
//
//                }
//
//            }
//        };




    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        currentUser = firebaseAuth.getCurrentUser();
//        firebaseAuth.addAuthStateListener(authStateListener);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (firebaseAuth!=null) {
//            firebaseAuth.removeAuthStateListener(authStateListener);
//        }
//    }
}