package com.example.mythoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameReg, emailReg, passReg;
    private Button regBtn, loginBtnReg;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        usernameReg = findViewById(R.id.username_reg);
        emailReg = findViewById(R.id.email_reg);
        passReg = findViewById(R.id.pass_reg);
        regBtn = findViewById(R.id.btn_reg);
        loginBtnReg = findViewById(R.id.btn_login_reg);
        progressBar = findViewById(R.id.progressBar_reg);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser!=null) {
                    startActivity(new Intent(RegisterActivity.this,JournalList.class));
                    finish();
                } else {
                    //user not logged in
                }
            }
        };

        regBtn.setOnClickListener(this);
        loginBtnReg.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_reg :
                regBtnActivity();
                break;

            case R.id.btn_login_reg :
                loginBtnActivity();
                break;
        }
    }

    private void loginBtnActivity() {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        finish();
    }

    private void regBtnActivity() {
        String username = usernameReg.getText().toString().trim();
        String email = emailReg.getText().toString().trim();
        String password = passReg.getText().toString().trim();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountActivity(username, email, password);
        } else {
            Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    private void createAccountActivity(String username, String email, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Take user to add thought activity & add user data to db
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();
                        //String currentUserName = username.toString();
                        Log.d("RegisterActivity",""+username);

                        JournalApi newUser = new JournalApi(username, currentUserId);

                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userId", currentUserId);
                        userObj.put("username", username);

                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult().exists()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            JournalApi journalApi = JournalApi.getInstance(); //Global API
                                            journalApi.setUsersId(currentUserId);
                                            journalApi.setUsername(username);
                                            startActivity(new Intent(RegisterActivity.this, AddThought.class));
                                            finish();
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, ""+e, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, ""+e, Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_LONG).show();
        }
    }
}