package com.example.mythoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class correct extends AppCompatActivity {
    private EditText text;
    private Button btn;
    private EditText text1;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct);


        text = (EditText) findViewById(R.id.editThought_add);
        text1 = (EditText) findViewById(R.id.editThought_add2);

        btn = (Button) findViewById(R.id.postBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue = Volley.newRequestQueue(correct.this);


                // first StringRequest: getting items searched
                StringRequest stringRequest = searchNameStringRequest(text.getText().toString());
                // executing the request (adding to queue)
                queue.add(stringRequest);
            }
        });
    }

    private StringRequest searchNameStringRequest(String nameSearch) {
        final String URL_PREFIX = "https://projectalpha321.pythonanywhere.com/?input="+nameSearch;

        String url = URL_PREFIX;
        return new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    // 3rd param - method onResponse lays the code procedure of success return
                    // SUCCESS
                    @Override
                    public void onResponse(String response) {
                        // try/catch block for returned JSON data
                        // see API's documentation for returned format
                        try {
                            String object=(response).toString();
                            //int maxItems = result.getInt("end");
                            //JSONArray resultList = result.getJSONArray("item");
                            Toast.makeText(correct.this, object+"", Toast.LENGTH_LONG).show();
                            text1.setText(object);
                            // catch for the JSON parsing error
                        } catch (Exception e) {
                            Toast.makeText(correct.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } // public void onResponse(String response)
                }, // Response.Listener<String>()
                new Response.ErrorListener() {
                    // 4th param - method onErrorResponse lays the code procedure of error return
                    // ERROR
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // display a simple message on the screen
                        Toast.makeText(correct.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
                    }
                });
    }



    }

