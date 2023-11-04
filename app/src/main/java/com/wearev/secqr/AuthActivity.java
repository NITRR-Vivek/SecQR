package com.wearev.secqr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    float v=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SecQR : Auth");

        getSupportActionBar().setSubtitle("Authorize yourself !");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        LottieAnimationView animationView = findViewById(R.id.signinlottie);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Login");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Signup");


        //animation
        animationView.playAnimation();
        tabLayout.setTranslationY(300);
        tabLayout.setAlpha(v);
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();


    }
}