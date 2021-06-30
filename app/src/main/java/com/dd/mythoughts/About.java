package com.dd.mythoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.dd.mythoughts.model.Journal;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private WebView webView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private Button githubBtn, mailBtn;
    private String github = "https://github.com/HeT-Delwadiya";
    private String mail = "mailto:hetdelwadiya@gmail.com";

    private FirebaseAuth firebaseAuth;
    //private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //private String url = "https://gameprojects-unity3d.blogspot.com/p/about-us.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#8E2DE2"));

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("About Us");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        githubBtn = findViewById(R.id.btn_github);
        mailBtn = findViewById(R.id.btn_mail);

        githubBtn.setOnClickListener(this);
        mailBtn.setOnClickListener(this);

        //webView = findViewById(R.id.webview_about);
        //webView.setWebViewClient(new MyBrowser());
        //webView.loadUrl(url);

//        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webView.loadUrl(url);

        drawerLayout = findViewById(R.id.drawer_layout_about);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view_about);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_add :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(About.this,AddThought.class));
                }
                break;

            case R.id.action_signout :
                if(user!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(About.this,MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {

            case R.id.action_home_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(About.this,JournalList.class));
                    finish();
                } return true;

            case R.id.action_add_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(About.this,AddThought.class));
                } return true;

            case R.id.action_privacy_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(About.this,Privacy.class));
                    finish();
                } return true;

            case R.id.action_about_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(About.this,About.class));
                    finish();
                } return true;

            case R.id.action_signout_drawer :
                if(user!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(About.this,MainActivity.class));
                    finish();
                } return true;


            default : return true;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_github : github();
            break;

            case R.id.btn_mail : mail();
            break;
        }
    }

    private void mail() {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setData(Uri.parse(mail));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //openWebPage(""+ mail);
    }

    private void github() {
        try {
            Intent mIntent = new Intent(Intent.ACTION_VIEW);
            mIntent.setData(Uri.parse(github));
            startActivity(mIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //openWebPage(""+ github);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}