package com.example.mythoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Privacy extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private WebView webView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private FirebaseAuth firebaseAuth;
    //private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private String url = "https://www.freeprivacypolicy.com/live/3cc9dfa5-a8c5-4585-96f0-0ef3c1e5713e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#8E2DE2"));

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("Privacy Policy");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        webView=(WebView)findViewById(R.id.webview_privacy);
        //webView.setWebViewClient(new MyBrowser());

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

        drawerLayout = findViewById(R.id.drawer_layout_privacy);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view_privacy);
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
                    startActivity(new Intent(Privacy.this,AddThought.class));
                }
                break;

            case R.id.action_signout :
                if(user!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(Privacy.this,MainActivity.class));
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
                    startActivity(new Intent(Privacy.this,JournalList.class));
                    finish();
                } return true;

            case R.id.action_add_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(Privacy.this,AddThought.class));
                } return true;

            case R.id.action_privacy_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(Privacy.this,Privacy.class));
                    finish();
                } return true;

            case R.id.action_about_drawer :
                if(user!=null && firebaseAuth!=null) {
                    startActivity(new Intent(Privacy.this,About.class));
                    finish();
                } return true;

            case R.id.action_signout_drawer :
                if(user!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(Privacy.this,MainActivity.class));
                    finish();
                } return true;


            default : return true;
        }

    }
}