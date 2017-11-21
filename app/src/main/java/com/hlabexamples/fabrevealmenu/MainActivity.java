package com.hlabexamples.fabrevealmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BaseFragment currentFragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            Fragment fragment = null;
            currentFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_xml) {
                fragment = new DemoXmlFragment();
                currentFragment = (BaseFragment) fragment;
            } else if (id == R.id.nav_code) {
                fragment = new DemoCodeFragment();
                currentFragment = (BaseFragment) fragment;
            } else if (id == R.id.nav_custom) {
                Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
                startActivity(intent);
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFragment).commit();
                return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment fragment = new DemoXmlFragment();
        currentFragment = (BaseFragment) fragment;
    }

}
