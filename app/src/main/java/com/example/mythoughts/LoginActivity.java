package com.example.mythoughts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mythoughts.util.JournalApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginBtn, regBtn;
    private EditText emailLogin, passLogin;
    private ProgressBar progressBar_login;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        regBtn = findViewById(R.id.btn_reg_login);
        loginBtn = findViewById(R.id.btn_login);
        emailLogin = findViewById(R.id.email_login);
        passLogin = findViewById(R.id.pass_login);
        progressBar_login = findViewById(R.id.progressBar_login);

        firebaseAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this,JournalList.class));
            finish();
        } else {
            // User logged in
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_reg_login :
                regBtnActivity();
                break;

            case R.id.btn_login :
                loginBtnActivity();
                break;
        }
    }

    private void loginBtnActivity() {
        String email = emailLogin.getText().toString().trim();
        String pass = passLogin.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            progressBar_login.setVisibility(View.VISIBLE);
            loginWithEmailPass(email,pass);
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    private void loginWithEmailPass(String email, String pass) {
        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String currentUserId = user.getUid();
                            collectionReference.whereEqualTo("userId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                            }
                                            if(!value.isEmpty()) {
                                                for (QueryDocumentSnapshot v : value) {
                                                    JournalApi journalApi = JournalApi.getInstance();
                                                    journalApi.setUsername(v.getString("username"));
                                                    journalApi.setUsersId(v.getString("userId"));
                                                    //take user to journalList
                                                    progressBar_login.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(LoginActivity.this,JournalList.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            //Toast.makeText(getApplicationContext(),"Wrong Email or Password",Toast.LENGTH_LONG).show();
                            progressBar_login.setVisibility(View.INVISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Wrong Email or Password", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void regBtnActivity() {
        startActivity(new Intent(LoginActivity.this, com.example.mythoughts.RegisterActivity.class));
    }
}