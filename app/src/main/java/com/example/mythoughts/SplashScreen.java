package com.example.mythoughts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.mythoughts.util.JournalApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
         currentUser= firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(SplashScreen.this,JournalList.class));
        } else {
            // User logged in
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (currentUser!=null) {
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUsersId = currentUser.getUid();

                    collectionReference.whereEqualTo("userId",currentUsersId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        return;
                                    }
                                    String name;
                                    if (value!=null) {
                                        for (QueryDocumentSnapshot data : value) {
                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUsersId(data.getString("userId"));
                                            journalApi.setUsername(data.getString("username"));

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(SplashScreen.this,JournalList.class));
                                                    finish();
                                                }
                                            },3000);

                                        }
                                    }
                                }
                            });

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashScreen.this,MainActivity.class));
                            finish();
                        }
                    },3000);

                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth!=null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}