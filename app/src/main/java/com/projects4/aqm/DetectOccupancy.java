package com.projects4.aqm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

import java.io.IOException;

public class DetectOccupancy extends AppCompatActivity {

    EditText co2v, hum, temp, lux;
    TextView occ, title;
    Button ok;
    ImageView prev;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detect_occ);

        context = this;

        // Initialize UI elements
        co2v = findViewById(R.id.aa);
        hum = findViewById(R.id.ab);
        temp = findViewById(R.id.ac);
        lux = findViewById(R.id.ad);
        occ = findViewById(R.id.output);
        title = findViewById(R.id.welcome);

        Intent intent = getIntent();
        try {
            if(intent.getStringExtra("title").isEmpty()){
                co2v.setText("");
                hum.setText("");
                temp.setText("");
                lux.setText("");
            }
            else {
                co2v.setText(intent.getStringExtra("co2"));
                hum.setText(intent.getStringExtra("hum"));
                temp.setText(intent.getStringExtra("temp"));
                lux.setText(intent.getStringExtra("lux"));
                title.setText(intent.getStringExtra("title"));
            }
        }
        catch (Exception e) {
            Toast.makeText(context, "Fill in the fields to proceed!", Toast.LENGTH_SHORT).show();
        }

        prev = findViewById(R.id.back);
        prev.setOnClickListener(view -> finish());

        ok = findViewById(R.id.proceed);
        ok.setOnClickListener(view -> {
            if(
                    co2v.getText().toString().isEmpty() ||
                    hum.getText().toString().isEmpty() ||
                    temp.getText().toString().isEmpty() ||
                    lux.getText().toString().isEmpty()
            ){
                Toast.makeText(this, "Fill the Required Fields!", Toast.LENGTH_SHORT).show();
            }
            else {
                float humidity = Float.parseFloat(hum.getText().toString());
                float temperature = Float.parseFloat(temp.getText().toString());
                float light = Float.parseFloat(lux.getText().toString());
                float co2 = Float.parseFloat(co2v.getText().toString());

                OccupancyPredictionTask predictionTask = new OccupancyPredictionTask();
                predictionTask.execute(humidity, temperature, light, co2);
            }
        });
    }

    private class OccupancyPredictionTask extends AsyncTask<Float, Void, Integer> {
        @Override
        protected Integer doInBackground(Float... params) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            // Create the JSON payload
            float[][] data = {{params[0], params[1], params[2], params[3]}};
            JsonObject payload = new JsonObject();
            payload.add("data", new Gson().toJsonTree(data));

            // Convert the payload to string
            String requestBody = payload.toString();

            // Create the request
            Request request = new Request.Builder()
                    .url("https://occupancyclassifier.onrender.com/occupancy")
                    .post(RequestBody.create(mediaType, requestBody))
                    .build();

            try {
                // Send the request and retrieve the response
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    // Extract the JSON data from the response
                    String responseData = response.body().string();
                    JsonArray jsonArray = JsonParser.parseString(responseData).getAsJsonArray();
                    int prediction = jsonArray.get(0).getAsInt();

                    return prediction;
                } else {
                    Log.e("Request Error", "Request was not successful. Code: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Request Error", "IOException: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer prediction) {
            if (prediction != null) {
                String msg = (prediction == 0) ? "The room is unoccupied" : "The room is occupied";
                occ.setText(msg);
                occ.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Prediction : " + prediction, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
