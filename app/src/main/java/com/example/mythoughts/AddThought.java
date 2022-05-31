package com.example.mythoughts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mythoughts.model.Journal;
import com.example.mythoughts.util.JournalApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddThought extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 3;
    private TextView username_add, date_add;
    private Button postBtn;
    private EditText title_add, thought_add;
    private ImageView bgImg_add, upload_add;
    private ProgressBar progressBar_add;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Journals");
    private Uri imageUri; //Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-thoughts-e64e9.appspot.com/o/journal_images%2Fmy_image_1622810828?alt=media&token=fe33949f-6c17-48d1-b851-b25788b8dc48");

    private String currentUserName;
    private String currentUserId;
    private Button count;
    private TextView words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thought);

        getSupportActionBar().hide();

        username_add = findViewById(R.id.username_add);
        date_add = findViewById(R.id.date_add);
        postBtn = findViewById(R.id.postBtn);
        title_add = findViewById(R.id.editTitle_add);
        thought_add = findViewById(R.id.editThought_add);
        bgImg_add = findViewById(R.id.bgImg_add);
        upload_add = findViewById(R.id.uploadImg_add);
        progressBar_add = findViewById(R.id.progressBar_add);
        count=(Button)findViewById(R.id.count);


        postBtn.setOnClickListener(this);
        upload_add.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] separated = thought_add.getText().toString().split(" ");
                count.setText(separated.length+" words");
            }
        });
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        if(JournalApi.getInstance()!=null) {
            //currentUserId = JournalApi.getInstance().getUsersId();
            currentUserName = JournalApi.getInstance().getUsername();

            username_add.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user!= null) {

                } else {

                }
            }
        };

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.postBtn :
                progressBar_add.setVisibility(View.VISIBLE);
                postBtnActivity();
                break;

            case R.id.uploadImg_add :
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }

    private void postBtnActivity() {

        String title = title_add.getText().toString().trim();
        String thought = thought_add.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought) && imageUri!=null) {

            StorageReference filepath = storageReference
                    .child("journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //add data to firestore
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thought);
                            journal.setImageUrl(uri.toString());
                            journal.setUsersId(currentUserId);
                            journal.setUserName(currentUserName);
                            journal.setTimeAdded(new Timestamp(new Date()));

                            collectionReference.add(journal)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressBar_add.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(AddThought.this, com.example.mythoughts.JournalList.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddThought.this, ""+e, Toast.LENGTH_LONG).show();
                                    progressBar_add.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddThought.this, ""+e, Toast.LENGTH_LONG).show();
                            progressBar_add.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddThought.this, ""+e, Toast.LENGTH_LONG).show();
                    progressBar_add.setVisibility(View.INVISIBLE);
                }
            });
        } else if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought)) {

            imageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/my-thoughts-e64e9.appspot.com/o/journal_images%2Fmy_image_1622810828?alt=media&token=fe33949f-6c17-48d1-b851-b25788b8dc48");

                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thought);
                            journal.setImageUrl(imageUri.toString());
                            journal.setUsersId(currentUserId);
                            journal.setUserName(currentUserName);
                            journal.setTimeAdded(new Timestamp(new Date()));

                            collectionReference.add(journal)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            progressBar_add.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(AddThought.this, com.example.mythoughts.JournalList.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddThought.this, ""+e, Toast.LENGTH_LONG).show();
                                    progressBar_add.setVisibility(View.INVISIBLE);
                                }
                            });

        } else {
            Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_LONG).show();
            progressBar_add.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                bgImg_add.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}