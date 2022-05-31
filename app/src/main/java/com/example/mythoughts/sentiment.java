package com.example.mythoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

public class sentiment extends AppCompatActivity {
    private EditText text;
    private Button btn;
    private TextView text1;
    private TextView text2;
    private RequestQueue queue;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment);

        text = (EditText) findViewById(R.id.editThought_add);

        text1=(TextView) findViewById(R.id.polarity4);
        text2=(TextView) findViewById(R.id.polarity);
        btn = (Button) findViewById(R.id.postBtn);
        imageView=(ImageView)findViewById(R.id.imageView3);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue = Volley.newRequestQueue(sentiment.this);


                // first StringRequest: getting items searched
                StringRequest stringRequest = searchNameStringRequest(text.getText().toString());
                // executing the request (adding to queue)
                queue.add(stringRequest);
            }
        });
    }

    private StringRequest searchNameStringRequest(String nameSearch) {
        final String URL_PREFIX = "https://projectalpha321.pythonanywhere.com/?input=polarity "+nameSearch;

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
                            text1.setText(object);
                            if (object.equals("Positive")){
                                text2.setText("1.0");
                                imageView.setImageResource(R.drawable.smile);
                            }
                            else if (object.equals("Negative")){
                                text2.setText("-0.5");
                                imageView.setImageResource(R.drawable.angry);
                            }
                            else{
                                text2.setText("0.1");
                                imageView.setImageResource(R.drawable.neutral);
                            }
                            // catch for the JSON parsing error
                        } catch (Exception e) {
                            Toast.makeText(sentiment.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } // public void onResponse(String response)
                }, // Response.Listener<String>()
                new Response.ErrorListener() {
                    // 4th param - method onErrorResponse lays the code procedure of error return
                    // ERROR
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // display a simple message on the screen
                        Toast.makeText(sentiment.this, "Food source is not responding (USDA API)", Toast.LENGTH_LONG).show();
                    }
                });
    }


}