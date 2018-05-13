package com.zhuravlenko2555dev.library.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.R;

public class AdministrationActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayoutReadersInLibrary, linearLayoutGivenOutLogs;
    private ImageView imageViewReadersInLibrary, imageViewGivenOutLogs;
    private TextView textViewReadersInLibrary, textViewGivenOutLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayoutReadersInLibrary = findViewById(R.id.linearLayoutReadersInLibrary);
        linearLayoutGivenOutLogs = findViewById(R.id.linearLayoutGivenOutLogs);

        linearLayoutReadersInLibrary.setOnClickListener(this);
        linearLayoutGivenOutLogs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutReadersInLibrary:
                Intent intentReadersInLibrary = new Intent(this, ReadersInLibraryActivity.class);
                startActivity(intentReadersInLibrary);

                break;
            case R.id.linearLayoutGivenOutLogs:
                Intent intentLinearLayoutGivenOutLogs = new Intent(this, ReadersInLibraryLogActivity.class);
                startActivity(intentLinearLayoutGivenOutLogs);

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 3, 3, getResources().getString(R.string.settings))
                .setIcon(R.drawable.settings)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIntent(new Intent(this, PrefActivity.class));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
