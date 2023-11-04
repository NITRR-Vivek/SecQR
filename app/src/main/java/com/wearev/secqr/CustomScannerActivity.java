package com.wearev.secqr;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomScannerActivity extends AppCompatActivity {
    private Dialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        Objects.requireNonNull(getSupportActionBar()).setTitle("QR Scan Result");

        Intent intent = getIntent();
        String qrText = intent.getStringExtra("qr_content");

        SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
        int userPos = Integer.parseInt(pref.getString("userPosition","3"));

        TextView qrResultText = findViewById(R.id.qrResultText);

        if (qrText != null) {
            String[] parts = qrText.split("\\$\\$");

            String part1Text = parts[0];
            String part2Text = null;
            String part3Text = null;

            if (parts.length > 1) {
                try {
                    part2Text = EncryptDecrypt.decrypt(parts[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (parts.length > 2) {
                try {
                    part3Text = EncryptDecrypt.decrypt(parts[2]);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            switch (userPos) {

                case 3:
                    qrResultText.setText(part1Text);
                    break;
                case 2:
                     qrResultText.setText(String.format("%s%s", part1Text, part2Text));
                    break;
                case 1:
                     qrResultText.setText(String.format("%s%s%s", part1Text, part2Text, part3Text));
                    break;
                default:
                    System.out.println("Invalid user ");
                    break;
            }
            String regexPattern = "Mobile No: (\\d{10})";
            Pattern pattern = Pattern.compile(regexPattern);
            assert part3Text != null;
            Matcher matcher = pattern.matcher(part3Text);
            if (matcher.find()) {
                String mobileNumber = matcher.group(1);
                assert mobileNumber != null;
                mobileNumber = mobileNumber.replaceAll("[^0-9]", "");
                showCustomDialog((int) Long.parseLong(mobileNumber));
                System.out.println("Mobile Number: " + (mobileNumber.isEmpty() ? "0" : mobileNumber));
            } else {
                System.out.println("Mobile Number not found.");
            }
        }
    }
    private void showCustomDialog( int mobileNumber) {
        customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialogue_layout_2);
        Button buttonCall112 = customDialog.findViewById(R.id.buttonCall1122);
        Button buttonCancel = customDialog.findViewById(R.id.buttonCancel2);
        Button callOwner = customDialog.findViewById(R.id.btnCallOwner);
        Button messageOwner = customDialog.findViewById(R.id.btnMessageOwner);

        callOwner.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +"91"+mobileNumber));
            startActivity(callIntent);
        });

        messageOwner.setOnClickListener(view -> {
            Intent messageIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" +"91"+ mobileNumber));
            startActivity(messageIntent);
        });

         buttonCall112.setOnClickListener(v -> {
             Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:112"));
            startActivity(intent);
        });
         buttonCancel.setOnClickListener(v -> customDialog.dismiss());
         customDialog.show();
    }
}
