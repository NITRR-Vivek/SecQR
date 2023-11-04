package com.wearev.secqr;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Button;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    DatabaseReference userRef =  FirebaseDatabase.getInstance().getReference("users");

    private final ActivityResultLauncher<String> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
                if(isGranted){
                    showCamera();
                }else{
                    Toast.makeText(this, "Camera Permission is Required !", Toast.LENGTH_SHORT).show();
                }
            });
    private final ActivityResultLauncher <ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result ->{
        if(result.getContents() == null){
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }else{

            Intent intent = new Intent(this, CustomScannerActivity.class);
            intent.putExtra("qr_content", result.getContents());
            startActivity(intent);
        }
    });


    private void showCamera() {
        ScanOptions options  = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scanning...");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_landscape);
        }
        Button btnQRGenerate = findViewById(R.id.qrGenerate);
        FloatingActionButton qrCustomScanner = findViewById(R.id.qrCustomScanner);
        Button btnVehicleInfo = findViewById(R.id.btnVehicleInfo);
        Button btnHistory = findViewById(R.id.btnHistory);

        Objects.requireNonNull(getSupportActionBar()).setTitle("SecQR");
        getSupportActionBar().setSubtitle("Secure your vehicle data with SecQR");


        // if the user is not logged in send to loginActivity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        }

        userRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                if (userProfile != null) {
                    String name = userProfile.getName();
                    String email = userProfile.getEmail();
                    String mobile = userProfile.getMobile();
                    String userPosition = userProfile.getPosition();
                    SharedPreferences pref = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("flag",true);
                    editor.putString("name",name);
                    editor.putString("email",email);
                    editor.putString("mobile",mobile);
                    editor.putString("userPosition",userPosition);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             }
        });


        btnQRGenerate.setOnClickListener(v->{
            Intent in = new Intent(MainActivity.this, QRActivity.class);
            startActivity(in);
        });
        qrCustomScanner.setOnClickListener(v->{

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showCamera();
            } else if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                Toast.makeText(this, "Camera Permission Required !", Toast.LENGTH_SHORT).show();
            }else{
                resultLauncher.launch(Manifest.permission.CAMERA);
            }
        });
        btnVehicleInfo.setOnClickListener(v->{
            Intent in = new Intent(MainActivity.this, VehicleInfoActivity.class);
            startActivity(in);
        });

        btnHistory.setOnClickListener(view->{
            Intent in = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(in);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.optShareApp){
            Intent iShare = new Intent(Intent.ACTION_SEND);
            iShare.setType("text/plain");
            iShare.putExtra(Intent.EXTRA_TEXT,"https://github.com/NITRR-Vivek/SecQR");
            iShare.putExtra(Intent.EXTRA_TEXT,"Hey There, Download this App from this Link.  ");
            startActivity(Intent.createChooser(iShare,"Share via"));
        } else if(itemId==R.id.optLogout){
            SharedPreferences pref = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("flag",true);
            editor.putString("name",null);
            editor.putString("email",null);
            editor.putString("mobile",null);
            editor.putString("userPosition","3");
            editor.apply();
            mAuth.signOut();
            finish();
            Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(this,AuthActivity.class);
            startActivity(in);
        }else if(itemId==R.id.optProfile){
            if (currentUser != null) {
                 Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                 startActivity(intent);
            } else {
                Toast.makeText(this, "You are not Logged In !", Toast.LENGTH_SHORT).show();
            }
        }else if(itemId==android.R.id.home){
            super.onBackPressed();
        }else {
            Toast.makeText(this, "Select", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
            AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
            exitDialog.setTitle("Exit ?");
            exitDialog.setMessage("Are you sure want to exit?");
            exitDialog.setIcon(R.drawable.ic_baseline_launch_24);
            exitDialog.setPositiveButton("No", (dialog, which) -> {
            });
            exitDialog.setNegativeButton("Yes", (dialog, which) -> MainActivity.super.onBackPressed());
            exitDialog.show();
    }
}