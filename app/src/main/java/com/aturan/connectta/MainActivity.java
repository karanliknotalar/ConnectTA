package com.aturan.connectta;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.aturan.connectta.Fragment.DiziFragment;
import com.aturan.connectta.Fragment.FilmFragment;
import com.aturan.connectta.Fragment.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView navMenuView;
    private DiziFragment diziFragment;
    private FilmFragment filmFragment;
    private LoginFragment loginFragment;


    private void init(){

        diziFragment = new DiziFragment();
        filmFragment = new FilmFragment();
        loginFragment = new LoginFragment();

        navMenuView = findViewById(R.id.main_aktivity_bottomNavMenu);

        setFragment(loginFragment);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        init();

        navMenuView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_login:
                        setFragment(loginFragment);
                        return true;
                    case R.id.menu_film:
                        setFragment(filmFragment);
                        return true;
                    case R.id.menu_dizi:
                        setFragment(diziFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_aktivity_frameLayout, fragment);
        transaction.commit();
    }

    int cikis = 0;
    @Override
    public void onBackPressed() {
        cikis++;
        if (cikis == 1)
            Toast.makeText(this, "Çıkmak için tekrar bas", Toast.LENGTH_SHORT).show();
        if (cikis == 2){
            finishAffinity();
            System.exit(0);
        }
    }


}