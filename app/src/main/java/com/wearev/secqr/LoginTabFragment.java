package com.wearev.secqr;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class LoginTabFragment extends Fragment {

    private TextInputEditText emailLogin, passwordLogin;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent iHome = new Intent(requireContext(), MainActivity.class);
            startActivity(iHome);
            requireActivity().finish();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_tab_fragment, container, false);

        emailLogin = view.findViewById(R.id.emailLogin);
        passwordLogin = view.findViewById(R.id.passwordLogin);
        AppCompatButton loginButton = view.findViewById(R.id.loginButton);
        TextView forgetPass = view.findViewById(R.id.forgetPassLogin);
        progressBar = view.findViewById(R.id.progressBarLogin);
        TextView loginAsGuest = view.findViewById(R.id.loginAsGuest);

        loginButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            login();
        });

        forgetPass.setOnClickListener(v-> resetPassword());
        loginAsGuest.setOnClickListener(v->
                mAuth.signInAnonymously()
                .addOnCompleteListener((Activity) requireContext(), (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences pref = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("flag",true);
                        editor.putString("name","guest");
                        editor.putString("email",null);
                        editor.putString("mobile",null);
                        editor.putString("userPosition","3");
                        editor.apply();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Failed to login as guest.", Toast.LENGTH_SHORT).show();
                    }
                }));

        return view;
    }

    private void login() {

        String email = Objects.requireNonNull(emailLogin.getText()).toString();
        String password = Objects.requireNonNull(passwordLogin.getText()).toString();

        if (TextUtils.isEmpty(email)){

            emailLogin.setError("This Field is required");
            emailLogin.requestFocus();
             progressBar.setVisibility(View.GONE);
            return;
        }else{
            emailLogin.setError(null);
        }
        if (TextUtils.isEmpty(password)){
            passwordLogin.setError("This Field id Required !");
            passwordLogin.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }else{
            passwordLogin.setError(null);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        SharedPreferences pref = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putBoolean("flag",true);
                        editor.apply();
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        Intent in = new Intent(getActivity(),MainActivity.class);
                        startActivity(in);
                        requireActivity().finish();
                    } else {
                        // Login failed
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(requireContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void resetPassword() {
        String email = Objects.requireNonNull(emailLogin.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireActivity(), "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Password reset email sent. Check your email.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireActivity(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}