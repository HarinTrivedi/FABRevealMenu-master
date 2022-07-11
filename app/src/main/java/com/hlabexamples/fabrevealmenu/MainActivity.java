package com.hlabexamples.fabrevealmenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(this);

        showXmlFragment();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_xml) {
            showXmlFragment();
            return true;
        } else if (id == R.id.nav_code) {
            showCodeFragment();
            return true;
        } else if (id == R.id.nav_custom) {
            Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void showXmlFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DemoXmlFragment()).commit();
    }

    private void showCodeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DemoCodeFragment()).commit();
    }

}
