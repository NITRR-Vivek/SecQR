package com.wearev.secqr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private EditText nameEditText,edtMobile;
    private TextView textViewName,textMobile;
    private TextView textEmail;
    private TextView textUserPosition;
    private Button saveProfile;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");

        nameEditText = findViewById(R.id.edtNameProfile);
        edtMobile = findViewById(R.id.edtMobile);
        textViewName = findViewById(R.id.textViewName);
        saveProfile = findViewById(R.id.buttonSave);
        textEmail = findViewById(R.id.textViewEmail);
        textMobile = findViewById(R.id.txtMobile);
        TextView textID = findViewById(R.id.textViewID);
        textUserPosition = findViewById(R.id.textViewUserPosition);
        ShapeableImageView btnEdtProfile = findViewById(R.id.edtProfile);

        if (currentUser != null && currentUser.isAnonymous()) {
             textViewName.setText(" Guest");
             textEmail.setText(" null");
             textMobile.setText(" null");
             textUserPosition.setText(" Public (Default)");
             btnEdtProfile.setOnClickListener(v-> Toast.makeText(this, "Register First to Edit the Profile !", Toast.LENGTH_SHORT).show());
        } else {


            String userId = currentUser.getUid();
            textID.setText(userId);

            DatabaseReference userRef =  FirebaseDatabase.getInstance().getReference("users");


            btnEdtProfile.setOnClickListener(view->{
                nameEditText.setVisibility(View.VISIBLE);
                edtMobile.setVisibility(View.VISIBLE);
                saveProfile.setVisibility(View.VISIBLE);
            });

            saveProfile.setOnClickListener(v -> {
                String updatedName = nameEditText.getText().toString();
                String updatedMobile = edtMobile.getText().toString();
                String email = currentUser.getEmail();

                UserProfile userProfile = new UserProfile(updatedName, email,updatedMobile,position);
                userRef.child(userId).setValue(userProfile)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            saveProfile.setVisibility(View.GONE);
                            nameEditText.setVisibility(View.GONE);
                            edtMobile.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            userRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                    if (userProfile != null) {

                        String name = " "+userProfile.getName();
                        String email = " "+userProfile.getEmail();
                        String mobile = " "+userProfile.getMobile();
                        position = " "+userProfile.getPosition();

                        SharedPreferences pref = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("flag",true);
                        editor.putString("name"," "+name);
                        editor.putString("email"," "+email);
                        editor.putString("mobile"," "+mobile);
                        editor.putString("userPosition"," "+position);
                        editor.apply();

                        textViewName.setText(name);
                        textEmail.setText(email);
                        textMobile.setText(mobile);
                        textUserPosition.setText(position);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Error reading data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
