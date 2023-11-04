package com.wearev.secqr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupTabFragment extends Fragment {
    private TextInputLayout passbox, confirmpassBox;
    private TextInputEditText emailEditText, passwordEditText, confirmedPasswordEditText,nameEditText, mobileNumEditText;
    private AppCompatButton signUpButton;
    private ProgressBar pgbar1;
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
        View view = inflater.inflate(R.layout.signup_tab_fragment, container, false);

        passbox = view.findViewById(R.id.passwordBox2);
        confirmpassBox = view.findViewById(R.id.confirmPassBox);
        emailEditText = view.findViewById(R.id.emailSignUp);
        nameEditText = view.findViewById(R.id.nameSignup);
        mobileNumEditText = view.findViewById(R.id.mobileNum);
        passwordEditText = view.findViewById(R.id.passWordSignUp);
        confirmedPasswordEditText = view.findViewById(R.id.confirmPassWord);
        TextView existingUser = view.findViewById(R.id.textView2);
        signUpButton = view.findViewById(R.id.signUpButton);
        pgbar1 = view.findViewById(R.id.progressBarSignup);

        signUpButton.setOnClickListener(v ->{

            pgbar1.setVisibility(View.VISIBLE);
            signUp();
        });
        existingUser.setOnClickListener(v->{
            ViewPager viewPager = requireActivity().findViewById(R.id.viewPager);
            viewPager.setCurrentItem(0);
        });

        return view;
    }
    private void signUp() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        String mobile = Objects.requireNonNull(mobileNumEditText.getText()).toString();
        String userPosition = "3";
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String confirmedPassword = Objects.requireNonNull(confirmedPasswordEditText.getText()).toString();

        if (TextUtils.isEmpty(email)){
            emailEditText.setError("This Field is required");
            emailEditText.requestFocus();
            pgbar1.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(name)){
            emailEditText.setError("This Field is required");
            emailEditText.requestFocus();
            pgbar1.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(mobile)){
            emailEditText.setError("This Field is required");
            emailEditText.requestFocus();
            pgbar1.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)){
            passwordEditText.setError("This Field is required");
            passwordEditText.requestFocus();
            pgbar1.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(confirmedPassword)){
            confirmedPasswordEditText.setError("This Field is required");
            confirmedPasswordEditText.requestFocus();
            pgbar1.setVisibility(View.GONE);
            return;
        }

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int passwordLength = s.length();
                if (passwordLength > 10) {
                    signUpButton.setEnabled(true);
                    passbox.setBoxStrokeColor(requireContext().getColor(R.color.app_color));

                } else if(passwordLength < 10 && passwordLength>6) {
                    passbox.setBoxStrokeColor(requireContext().getColor(R.color.yellow));
                 }else {
                    passbox.setBoxStrokeColor(requireContext().getColor(R.color.red));
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmedPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int passwordLength = s.length();
                if (passwordLength > 10) {
                    signUpButton.setEnabled(true);
                    confirmpassBox.setBoxStrokeColor(requireContext().getColor(R.color.app_color));

                } else if (passwordLength <10 && passwordLength>6) {
                    confirmpassBox.setBoxStrokeColor(requireContext().getColor(R.color.yellow));
                 }else {
                    confirmpassBox.setBoxStrokeColor(requireContext().getColor(R.color.red));
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if(password.equals(confirmedPassword) && password.length()>6){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {

                            SharedPreferences pref = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("flag",true);
                            editor.putString("name",name);
                            editor.putString("email",email);
                            editor.putString("mobile",mobile);
                            editor.putString("userPosition",userPosition);
                            editor.apply();

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            DatabaseReference userRef =  FirebaseDatabase.getInstance().getReference("users");

                            assert currentUser != null;
                            String userId = currentUser.getUid();
                            UserProfile userProfile = new UserProfile(name, email, mobile, userPosition);
                            userRef.child(userId).setValue(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                    });


                            pgbar1.setVisibility(View.GONE);

                            Intent in = new Intent(getActivity(),MainActivity.class);
                            startActivity(in);
                            Toast.makeText(getContext(), "Registered Successfully !", Toast.LENGTH_SHORT).show();
                            requireActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "Sign In Failed !", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            pgbar1.setVisibility(View.GONE);
            passbox.setBoxStrokeColor(requireContext().getColor(R.color.red));
            confirmpassBox.setBoxStrokeColor(requireContext().getColor(R.color.red));
            Toast.makeText(getContext(), "Minimum 7 digit password is required !", Toast.LENGTH_SHORT).show();
        }
    }
}