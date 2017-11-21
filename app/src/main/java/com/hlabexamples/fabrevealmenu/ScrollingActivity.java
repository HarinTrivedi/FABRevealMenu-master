package com.hlabexamples.fabrevealmenu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.hlab.fabrevealmenu.view.FABRevealMenu;

public class ScrollingActivity extends AppCompatActivity {
    FABRevealMenu fabMenu;

    @Override
    public void onBackPressed() {
        if (fabMenu.isShowing())
            fabMenu.closeMenu();
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fabMenu = findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {

                View customView = View.inflate(this, R.layout.layout_custom_menu, null);
                setupCustomFilterView(customView);
                fabMenu.setCustomView(customView);
                fabMenu.bindAnchorView(fab);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupCustomFilterView(View customView) {
        if (customView != null) {
            Button btnApply = customView.findViewById(R.id.btnApply);
            CheckBox cb1 = customView.findViewById(R.id.cb1);
            CheckBox cb2 = customView.findViewById(R.id.cb2);
            CheckBox cb3 = customView.findViewById(R.id.cb3);
            CheckBox cb4 = customView.findViewById(R.id.cb4);

            final CheckBox[] filters = new CheckBox[]{cb1, cb2, cb3, cb4};

            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabMenu.closeMenu();
                    StringBuilder builder = new StringBuilder("Selected:");
                    for (CheckBox filter : filters) {
                        if (filter.isChecked()) {
                            builder.append("\n").append(filter.getText().toString());
                        }
                    }
                    Toast.makeText(ScrollingActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
