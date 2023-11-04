package com.wearev.secqr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VehicleInfoActivity extends AppCompatActivity {

    private EditText vehicleNumberEditText;
    private CheckBox consentCheckBox;
    private ProgressBar pgBar;
    TextView vehicleInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        vehicleNumberEditText = findViewById(R.id.vehicleNumberEditText);
        Button fetchInfoButton = findViewById(R.id.fetchInfoButton);
        vehicleInfoTextView = findViewById(R.id.vehicleInfoTextView);
        consentCheckBox = findViewById(R.id.consentCheckBox);
        pgBar = findViewById(R.id.pgBarVehicleInfo);


        fetchInfoButton.setOnClickListener(v -> {
            pgBar.setVisibility(View.VISIBLE);
//            fetchVehicleInfo();
            Toast.makeText(VehicleInfoActivity.this, "API service is stopped for now for protecting its usage !", Toast.LENGTH_LONG).show();
            pgBar.setVisibility(View.GONE);

        });
    }

    private void fetchVehicleInfo() {
        String vehicleNumber = vehicleNumberEditText.getText().toString();
        boolean consentGiven = consentCheckBox.isChecked();


        if (vehicleNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a vehicle number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!consentGiven) {
            Toast.makeText(this, "Please provide consent before fetching vehicle info", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\n" +
                "    \"regn_no\": \"" + vehicleNumber + "\",\n" +
                "    \"consent\": \"Y\",\n" +
                "    \"consent_text\": \"I hereby declare my consent agreement for fetching my information via AITAN Labs API\"\n" +
                "}";

        // Creating the API request
        Request request = new Request.Builder()
                .url("https://rto-vehicle-info.com/vehicle_info")
                .post(RequestBody.create(mediaType, requestBody))
                .addHeader("content-type", "application/json")
                .addHeader("Your-API-Key", "d0c3e07d74msh9df89fb9cd88d96p108719jsn46f52de5c09b")
                .addHeader("Your-API-Host", "rto-vehicle-info.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(VehicleInfoActivity.this, "Failed to fetch vehicle info. Please check your connection.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = Objects.requireNonNull(response.body()).string();

                runOnUiThread(() -> {
                    pgBar.setVisibility(View.GONE);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String formattedText = formatJSON(jsonResponse);
                        vehicleInfoTextView.setText(formattedText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            private String formatJSON(JSONObject jsonObject) throws JSONException {
                StringBuilder formattedText = new StringBuilder();

                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObject.get(key);

                    formattedText.append(key).append(": ");

                    if (value instanceof JSONObject) {
                        formattedText.append("\n").append(formatJSON((JSONObject) value));
                    } else {
                        formattedText.append(value);
                    }

                    formattedText.append("\n");
                }

                return formattedText.toString();
            }
        });
    }
}

